package com.example.PFE.model;

import org.hibernate.annotations.NaturalId;

import com.example.PFE.enumeration.RoleName;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ROLE_ID")
	private Integer id;

	@Enumerated(EnumType.STRING)
	@NaturalId
	private RoleName name;

	public Role() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public RoleName getName() {
		return name;
	}

	public void setName(RoleName name) {
		this.name = name;
	}

	public Role(RoleName name) {
		this.name = name;
	}
	
	
}
