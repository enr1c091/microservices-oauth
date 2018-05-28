package com.enrico.microservices.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.enrico.microservices.auth.entity.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

}
