package com.enrico.microservices.got.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;

    public ResourceNotFoundException() {
        super("The requested resource was not found");
    }
	
    public ResourceNotFoundException(String message) {
        super(message);
    }
	
	public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}