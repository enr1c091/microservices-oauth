package com.enrico.microservices.got.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;

    public BadRequestException() {
        super("The request has invalid parameters");
    }
	
    public BadRequestException(String message) {
        super(message);
    }
	
	public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}