package com.enrico.microservices.got.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;

    public ConflictException() {
        super("The request resource has conflicted with an existing resoure");
    }
	
    public ConflictException(String message) {
        super(message);
    }
	
	public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }

}