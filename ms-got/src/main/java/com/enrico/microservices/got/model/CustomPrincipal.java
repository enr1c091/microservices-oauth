package com.enrico.microservices.got.model;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class CustomPrincipal implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private String email;

}
