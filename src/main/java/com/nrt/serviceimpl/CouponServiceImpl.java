package com.nrt.serviceimpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nrt.entity.CartItem;
import com.nrt.entity.Coupon;
import com.nrt.entity.User;
import com.nrt.entity.UserCouponMapping;
import com.nrt.exception.CantDeleteException;
import com.nrt.repository.CartItemRepository;
import com.nrt.repository.CouponRepository;
import com.nrt.repository.UserCouponMappingRepo;
import com.nrt.repository.UserRepository;
import com.nrt.request.CouponRequest;
import com.nrt.responce.ApplyCouponResponse;
import com.nrt.service.CouponService;
import com.nrt.util.CommonUtil;
import com.nrt.util.MasterEnum.DiscountType;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CouponServiceImpl implements CouponService {

	@Autowired
	private CouponRepository couponRepository;
	@Autowired
	CartItemRepository cartItemRepository;
	@Autowired
	CartItemServiceImpl cartItemServiceImpl;
	@Autowired
	UserCouponMappingRepo userCouponMappingRepo;
	@Autowired
	UserRepository userRepository;

	@Override
	public Boolean Register(CouponRequest couponRequest) {
		Coupon coupon = new Coupon();
		if (couponRequest.getCouponId() != 0) {
			coupon.setCouponId(couponRequest.getCouponId());
			Coupon Coupon = couponRepository.findById(couponRequest.getCouponId()).get();
			coupon.setCouponStatus(Coupon.getCouponStatus());
		}
		coupon.setActivetAt(couponRequest.getCouponActivetAt());
		coupon.setAmount(couponRequest.getCouponAmount());
		coupon.setApplyPerUser(couponRequest.getCouponApplyPerUser());
		coupon.setCode(couponRequest.getCouponCode());
		coupon.setCreatedAt(LocalDate.now());
		coupon.setDescription(couponRequest.getCouponDescription());
		coupon.setExpiresAt(couponRequest.getCouponExpiresAt());
		coupon.setIfAmountAbove(couponRequest.getCouponIfAmountAbove());
		coupon.setType(DiscountType.getById(couponRequest.getCouponType()));

		try {
			couponRepository.save(coupon);
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		}

	}

	@Override
	public CouponRequest getCouponById(Long couponId) {
		Optional<Coupon> couponOptional = couponRepository.findById(couponId);
		if (couponOptional.isPresent()) {
			CouponRequest couponRequest = new CouponRequest();
			Coupon coupon = couponOptional.get();
			couponRequest.setCouponActivetAt(coupon.getActivetAt());
			couponRequest.setCouponAmount(couponRequest.getCouponAmount());
			couponRequest.setCouponApplyPerUser(coupon.getApplyPerUser());
			couponRequest.setCouponCode(coupon.getCode());
			couponRequest.setCouponDescription(coupon.getDescription());
			couponRequest.setCouponExpiresAt(coupon.getExpiresAt());
			couponRequest.setCouponId(coupon.getCouponId());
			couponRequest.setCouponType(coupon.getType() == DiscountType.FLAT ? 1 : 2);
			couponRequest.setCouponIfAmountAbove(coupon.getIfAmountAbove());
			return couponRequest;
		} else {
			return null;
		}
	}

	@SuppressWarnings("static-access")
	public ApplyCouponResponse applyCoupon(@NotNull String couponCode, @NotNull String userId, HttpSession session) {
		log.info("coupon is : " + couponCode);
		ApplyCouponResponse applyCouponResponse = new ApplyCouponResponse();
		Coupon coupon = couponRepository.findByCode(couponCode);
		if (coupon == null) {
			applyCouponResponse.setMessage("coupon does not exist");
			log.error("coupon does not exist");
			applyCouponResponse.setIsApplied(Boolean.FALSE);
			return applyCouponResponse;
		}
		log.info("coupon is : " + coupon.toString());
		UserCouponMapping userCouponMapping = userCouponMappingRepo.findByCoupon(coupon);
		log.info("coupon 111 : ");
		int usageCount = 0;
		if (userCouponMapping != null) {
			usageCount = userCouponMapping.getUsageCount();
			log.info("coupon 1222 : ");
		}
		if (usageCount < coupon.getApplyPerUser()) {
			List<CartItem> cartItems = cartItemServiceImpl.getUsersAllCartItems(userId);
			long cartTotal = CouponServiceImpl.calculateCartTotal(cartItems);
			log.info("apply service method called : cartTotal amount is :" + cartTotal);

			if (CommonUtil.isExpired(coupon.getExpiresAt())) {
				log.info("not expired  ");

				if (coupon.getCouponStatus() == 1 && CommonUtil.isActive(coupon.getActivetAt())) {

					log.info("status  is active:" + coupon.getCouponStatus());

					if (coupon.getIfAmountAbove() <= cartTotal) {

						log.info("coupon can  be applied inrange");
						DiscountType discountType = coupon.getType();
						log.info("coupondiscountType " + discountType);

						if (discountType == discountType.FLAT) {

							log.info("coupon FLAT IS applied ");
							cartTotal -= coupon.getAmount();
							session.setAttribute("cartTotals", cartTotal);
							applyCouponResponse.setIsApplied(Boolean.TRUE);
							applyCouponResponse.setCartTotalAfterCouponApplied(cartTotal);

						} else {
							log.info("coupon PERCENTAHE IS applied ");
							cartTotal -= (cartTotal * coupon.getAmount()) / 100;
							session.setAttribute("cartTotals", cartTotal);
							applyCouponResponse.setIsApplied(Boolean.TRUE);
							applyCouponResponse.setCartTotalAfterCouponApplied(cartTotal);
						}
					} else {
						log.error("coupon can not be applied if amount is less than  : " + coupon.getIfAmountAbove());
						applyCouponResponse.setMessage(
								"Coupon can not be applied if amount is less than  : " + coupon.getIfAmountAbove());
						applyCouponResponse.setIsApplied(Boolean.FALSE);
					}
				} else {
					log.error("coupon is deactivated : " + coupon.getCouponStatus() + " or Upcomming.."
							+ coupon.getActivetAt());
					applyCouponResponse.setMessage("coupon is deactivated : " + coupon.getCouponStatus());
					applyCouponResponse.setIsApplied(Boolean.FALSE);
				}

			} else {
				applyCouponResponse.setMessage("coupon is expired");
				log.error("coupon is expired");
				applyCouponResponse.setIsApplied(Boolean.FALSE);
			}
		} else {
			applyCouponResponse.setMessage("coupon limit is exeed");
			log.error("coupon limit is exeed");
			applyCouponResponse.setIsApplied(Boolean.FALSE);
		}
		log.error("coupon data " + applyCouponResponse.toString());
		return applyCouponResponse;
	}

	private static long calculateCartTotal(List<CartItem> cart) {
		long cartTotal = 0;
		for (CartItem item : cart) {
			long itemTotal = item.getProduct().getSellingPrice() * item.getQuantity();
			cartTotal += itemTotal;
		}
		return cartTotal;
	}

	public void addCouponCount(String couponCode, @NotNull String userId) {
		Optional<User> user = userRepository.findByEmail(userId);
		Coupon coupon = couponRepository.findByCode(couponCode);
		UserCouponMapping userCouponMapping = userCouponMappingRepo.findByUserAndCoupon(user.get(), coupon);
		if (userCouponMapping != null) {
			int previousCount = userCouponMapping.getUsageCount();
			userCouponMapping.setUsageCount(previousCount + 1);
			userCouponMappingRepo.save(userCouponMapping);
		} else {
			UserCouponMapping newuserCouponMapping = new UserCouponMapping();
			newuserCouponMapping.setUser(user.get());
			newuserCouponMapping.setCoupon(coupon);
			int count = newuserCouponMapping.getUsageCount();
			newuserCouponMapping.setUsageCount(count + 1);
			UserCouponMapping U = userCouponMappingRepo.save(newuserCouponMapping);
			if (U != null)
				log.info("user count increased by one for coupon:");
			else
				log.error("user count NOT increased by one for coupon:");
		}
	}

	@Override
	public Boolean Delete(long couponId) {
		try {
			couponRepository.deleteById(couponId);
			return Boolean.TRUE;
		} catch (Exception e) {
			throw new CantDeleteException("cart item can not be deleted..! Cause> " + e.getMessage());

		}

	}

	@Override
	public List<Coupon> FindAllCoupon() {

		return couponRepository.findAll();
	}

	@Override
	public Boolean updateCouponStatus(Long couponId, int status) {
		Coupon coupon = couponRepository.findAllByCouponId(couponId);
		coupon.setCouponStatus(status);
		if (couponRepository.save(coupon) != null)
			return Boolean.TRUE;
		return Boolean.FALSE;

	}

}
