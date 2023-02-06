package com.example.PFE.controller;

import java.util.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.PFE.enumeration.RoleName;
import com.example.PFE.model.Role;
import com.example.PFE.model.User;
import com.example.PFE.repository.RoleRepository;
import com.example.PFE.repository.UserRepository;
import com.example.PFE.request.LoginRequest;
import com.example.PFE.request.RegisterRequest;
import com.example.PFE.response.JwtResponse;
import com.example.PFE.response.Message;
import com.example.PFE.security.jwt.JwtUtils;
import com.example.PFE.security.service.UserDetailsImpl;

import jakarta.validation.Valid;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class UserController {
	@Autowired 	
	AuthenticationManager authenticationManager;

	@Autowired	
	UserRepository userRepository;
	
	@Autowired	
	RoleRepository roleRepository;

	@Autowired	
	PasswordEncoder encoder;

	@Autowired	
	JwtUtils jwtUtils;
	
	@PostMapping("/register")
	public ResponseEntity<Message> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
		if (userRepository.existsByUsername(registerRequest.getUsername())) {
			return new ResponseEntity <>(new Message("Username is already taken!"),HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(registerRequest.getEmail())) {
			return new ResponseEntity <>(new Message("Email is already taken!"),HttpStatus.BAD_REQUEST);
		}

		// Create new user's account
		User user = new User(
				             registerRequest.getFirstName(),
	                         registerRequest.getLastName(),
				             registerRequest.getUsername(), 
				             registerRequest.getEmail(),
							 encoder.encode(registerRequest.getPassword()));
		
		Set<String> rolesInRequest= registerRequest.getRole();
		Set<Role> roles = new HashSet<>();
		rolesInRequest.forEach(role->{
			switch(role) {
			case "etudiant":
				Role enseignantRole= roleRepository.findByName(RoleName.ROLE_ENSEIGNANT);
				roles.add(enseignantRole);
				break;
			default:
			    Role etudiantRole= roleRepository.findByName(RoleName.ROLE_ETUDIANT);
			    roles.add(etudiantRole);
			
			}
		});
		user.setRoles(roles);
		userRepository.save(user);
		return new ResponseEntity <>(new Message("User registered successfully!"),HttpStatus.OK);
	}
	
	@GetMapping("/users")
	  public List<User> getAllUtilisateur() {
	    List<User> Utilisateur = new ArrayList<>();
	    userRepository.findAll().forEach(Utilisateur::add);
	 
	    return Utilisateur;
	  }
	@PostMapping("/auth/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();	
		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 userDetails.getAuthorities()));
		}

	  @PutMapping("/users/{id}")
	  public ResponseEntity<User> updateUtilisateur(@PathVariable("id") long id, @RequestBody User Utilisateur) {
	 
	    Optional<User> UtilisateurInfo = userRepository.findById(id);
	 
	    if (UtilisateurInfo.isPresent()) {
	    	User utilisateur = UtilisateurInfo.get();
	    	utilisateur.setFirstName(Utilisateur.getFirstName());
	    	utilisateur.setLastName(Utilisateur.getLastName());
	    	utilisateur.setUsername(Utilisateur.getUsername());
	    	utilisateur.setEmail(Utilisateur.getEmail());
	    	utilisateur.setPassword(Utilisateur.getPassword());
	      return new ResponseEntity<>(userRepository.save(Utilisateur), HttpStatus.OK);
	    } else {
	      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	  }

}
