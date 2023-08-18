package com.nrt.entity;

import java.sql.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_details")
public class User {

	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "user_email")
	private String email;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "password")
	private String password;

	@Column(name = "phone_no")
	private String phoneNo;

	@Column(name = "status")
	private int status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy")
	@Column(name = "created_at")
	private Date createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy")
	@Column(name = "updated_at")
	private Date updatedAt;

	@OneToOne(cascade = CascadeType.ALL, targetEntity = Role.class)
	private Role role;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy")
	@Column(name = "password_updated_at")
	private Date passwordUpdated;

}