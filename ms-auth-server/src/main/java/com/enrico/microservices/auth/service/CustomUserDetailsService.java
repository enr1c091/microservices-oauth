package com.enrico.microservices.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.enrico.microservices.auth.entity.User;
import com.enrico.microservices.auth.repository.UserRepository;

@Service(value = "userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String input) {
		Optional<User> user = null;

		if (input.contains("@"))
			user = userRepository.findByEmail(input);
		else
			user = userRepository.findByUsername(input);

		if (!user.isPresent())
			throw new BadCredentialsException("Bad credentials");

		new AccountStatusUserDetailsChecker().check(user.get());

		return user.get();
	}

}
