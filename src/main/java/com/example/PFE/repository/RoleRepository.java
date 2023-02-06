package com.example.PFE.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PFE.enumeration.RoleName;
import com.example.PFE.model.Role;


public interface RoleRepository extends JpaRepository<Role,Long>{
	Role findByName(RoleName name);

}