package com.enrico.microservices.got.service;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enrico.microservices.got.controller.HouseController;
import com.enrico.microservices.got.controller.PersonController;
import com.enrico.microservices.got.dto.Person;
import com.enrico.microservices.got.entity.HouseEntity;
import com.enrico.microservices.got.entity.PersonEntity;
import com.enrico.microservices.got.exception.BadRequestException;
import com.enrico.microservices.got.exception.ConflictException;
import com.enrico.microservices.got.exception.InternalServerException;
import com.enrico.microservices.got.exception.ResourceNotFoundException;
import com.enrico.microservices.got.repository.HouseEntityRepository;
import com.enrico.microservices.got.repository.PersonEntityRepository;

import rocks.spiffy.spring.hateoas.utils.uri.resolver.ControllerUriResolver;

@Service
public class PersonService {

	private static Logger LOGGER = LoggerFactory.getLogger(PersonService.class); 
	
	@Autowired
	private PersonEntityRepository personRepository;

	@Autowired
	private HouseEntityRepository houseRepository;
	
	@Autowired
    private Validator validator;
	
	@PostConstruct
	public void populate() {
		LOGGER.info("People populated");
	}
	
	@Transactional(readOnly = true)
	public List<Person> listAll() {
		try (Stream<PersonEntity> stream = personRepository.streamAll()) {
			return stream.map(this::toDto).collect(Collectors.toList());
		} catch (Exception ex) {
			LOGGER.error("Unable to get all persons", ex);
			throw new InternalServerException();			
		}
	}

	@Transactional(readOnly = true)
	public List<Person> listByHouseId(Long houseId) {
		try (Stream<PersonEntity> stream = personRepository.findByHouseId(houseId).stream()) {
			return stream.map(this::toDto).collect(Collectors.toList());
		} catch (Exception ex) {
			LOGGER.error("Unable to get persons from house", ex);
			throw new InternalServerException();			
		}
	}
	
	@Transactional(readOnly = true)
	public Person findById(Long id) {
		Optional<PersonEntity> entity = null;
		try {
			entity = personRepository.findById(id);
		} catch (Exception ex) {
			LOGGER.error("Unable to get house by id", ex);
			throw new InternalServerException();
		}
		if (entity.isPresent()) {
			return toDto(entity.get());
		} else {
			throw new ResourceNotFoundException();
		}		
	}
	
	@Transactional(readOnly = false)
	public Person addNew(Person dto) {
		
		Set<ConstraintViolation<Person>> violations = validator.validate(dto);
		if (violations.size() > 0) {
			throw new BadRequestException();
		}
		
		if (!dto.hasLink("house") || dto.getLink("house").getHref() == null) {
			throw new BadRequestException();
		}
		
		ControllerUriResolver cr = ControllerUriResolver.on(methodOn(HouseController.class).getOne(null));
		String houseId = cr.resolve( dto.getLink("house").getHref(), "house")
				.orElseThrow(BadRequestException::new);
		
		boolean personExists = false;
		Optional<HouseEntity> houseEntity = null;
		try {
			personExists = personRepository.existsByName(dto.getName());
			houseEntity = houseRepository.findById(Long.parseLong(houseId));
		} catch (Exception ex) {
			LOGGER.error("Unable to check if person exists", ex);
			throw new InternalServerException();
		}

		if (!houseEntity.isPresent()) {
			throw new BadRequestException();
		}
		
		if (personExists) {
			throw new ConflictException();
		}
		
		try {
			PersonEntity person = personRepository.save(new PersonEntity(Encode.forHtml(dto.getName()), houseEntity.get()));
			return toDto(person);
		} catch (Exception ex) {
			LOGGER.error("Unable to add new person", ex);
			throw new InternalServerException();			
		}
	}
	
	public Person toDto(PersonEntity entity) {
		return new Person(entity.getName())
			.withLink(linkTo(methodOn(PersonController.class).getOne((entity.getId()))).withSelfRel())
			.withLink(linkTo(methodOn(HouseController.class).getOne(entity.getHouse().getId())).withRel("house"));
	}
}