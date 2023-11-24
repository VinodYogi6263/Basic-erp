package com.nrt.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nrt.util.MasterEnum.DiscountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "Coupon_Details")
@ToString
public class Coupon {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long couponId;

	@Column(name = "coupon_code")
	private String code;

	@Column(name = "coupon_type")
	@Enumerated(EnumType.STRING)
	private DiscountType Type;

	@Column(name = "coupon_amount")
	private long Amount;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy")
	@Column(name = "created_at")
	private LocalDate createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy")
	@Column(name = "expires_at")
	private LocalDate expiresAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy")
	@Column(name = "activet_at")
	private LocalDate activetAt;

	@Column(name = "if_amount_above")
	private long ifAmountAbove;

	@Column(name = "coupon_description")
	private String description;

	@Column(name = "coupon_applyPerUser")
	private long applyPerUser;

	@Column(name = "coupon_status")
	private int couponStatus;

	@OneToMany(mappedBy = "coupon")
	private List<UserCouponMapping> userMappings;

}
