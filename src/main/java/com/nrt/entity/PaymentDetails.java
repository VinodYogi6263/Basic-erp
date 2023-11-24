package com.nrt.entity;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int pid;

	@Column(name = "order_id")
	private String id;

	private String status;

	private long amount_paid;

	private long amount;

	private Date created_at;

	private long amount_due;

	private String currency;

	private String receipt;

	private String signature;

	private String entity;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	private int attempts;

}
