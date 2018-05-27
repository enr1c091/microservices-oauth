package com.enrico.microservices.got.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enrico.microservices.got.aop.LogExecution;
import com.enrico.microservices.got.aop.LogRequest;
import com.enrico.microservices.got.dto.Person;
import com.enrico.microservices.got.service.PersonService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api
@RestController
@RequestMapping(path = "/people", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class PersonController {

	@Autowired
	private PersonService personService;

	@LogExecution
	@LogRequest
	@GetMapping
	@ApiOperation(value = "View a list of all available person", response = Iterable.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully retrieved list"),
	        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	})
	public List<Person> getAll() {
		return personService.listAll();
	}

	@LogExecution
	@LogRequest
	@PreAuthorize("hasAuthority('role_admin')")
	@GetMapping(path = "/{id}")
	@ApiOperation(value = "Search a person with an ID", response = Person.class)
	public Person getOne(@PathVariable("id") Long id) {
		return personService.findById(id);
	}
	
}