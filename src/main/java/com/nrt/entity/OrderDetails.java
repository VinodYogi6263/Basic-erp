package com.nrt.entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_details")
public class OrderDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "order_id")
	private long id;
	
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "shopkeeper_id")
	private String shopkeeperId;
	
	private String userName;
	@Column(name = "shippingAddress")
	private String shippingAddress;
	
	@Column(name = "total_amount")
	private long  totalAmount;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "created_At")
	private Date created_At;
	
	// Map to a list of ordered items
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderedItemDetails> orderedItems = new ArrayList<>();


}
// Constructors, getters, setters, and other methods
