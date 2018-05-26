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
import com.enrico.microservices.got.dto.House;
import com.enrico.microservices.got.entity.HouseEntity;
import com.enrico.microservices.got.entity.PersonEntity;
import com.enrico.microservices.got.exception.BadRequestException;
import com.enrico.microservices.got.exception.ConflictException;
import com.enrico.microservices.got.exception.InternalServerException;
import com.enrico.microservices.got.exception.ResourceNotFoundException;
import com.enrico.microservices.got.repository.HouseEntityRepository;
import com.enrico.microservices.got.repository.PersonEntityRepository;

@Service
public class HouseService {

	private static Logger LOGGER = LoggerFactory.getLogger(HouseService.class); 

	@Autowired
	private HouseEntityRepository houseRepository;

	@Autowired
	private PersonEntityRepository personRepository;

	@Autowired
	private Validator validator;
	
	@PostConstruct
	public void populate() {

		HouseEntity starks = houseRepository.save(new HouseEntity("Stark"));
		HouseEntity lannisters = houseRepository.save(new HouseEntity("Lannister"));
		houseRepository.save(new HouseEntity("Targaryen"));
		houseRepository.save(new HouseEntity("Arryn"));
		houseRepository.save(new HouseEntity("Greyjoy"));
		houseRepository.save(new HouseEntity("Tully"));
		houseRepository.save(new HouseEntity("Frey"));
		houseRepository.save(new HouseEntity("Baratheon"));
		houseRepository.save(new HouseEntity("Martell"));
		houseRepository.save(new HouseEntity("Tyrell"));
		houseRepository.save(new HouseEntity("Bolton"));

		personRepository.save(new PersonEntity("Eddard Stark", starks));
		personRepository.save(new PersonEntity("Sansa Stark", starks));

		personRepository.save(new PersonEntity("Tyrion Lannister", lannisters));
		personRepository.save(new PersonEntity("Cersei Lannister", lannisters));
		personRepository.save(new PersonEntity("Jaime Stark", lannisters));

		LOGGER.info("Houses populated");
	}

	@Transactional(readOnly = true)
	public List<House> listAll() {
		try (Stream<HouseEntity> stream = houseRepository.streamAll()) {
			return stream.map(this::toDto).collect(Collectors.toList());
		} catch (Exception ex) {
			LOGGER.error("Unable to get all houses", ex);
			throw new InternalServerException();			
		}
	}

	@Transactional(readOnly = true)
	public House findById(Long id) {
		Optional<HouseEntity> entity = null;
		try {
			entity = houseRepository.findById(id);
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
	public House addNew(House dto) {

		Set<ConstraintViolation<House>> violations = validator.validate(dto);
		if (violations.size() > 0) {
			throw new BadRequestException();
		}

		boolean exists = false;
		try {
			exists = houseRepository.existsByName(dto.getName());
		} catch (Exception ex) {
			LOGGER.error("Unable to check if house exists", ex);
			throw new InternalServerException();
		}

		if (exists) {
			throw new ConflictException();
		}
		try {
			HouseEntity entity = houseRepository.save(new HouseEntity(Encode.forHtml(dto.getName())));
			return toDto(entity);
		} catch (Exception ex) {
			LOGGER.error("Unable to add new house", ex);
			throw new InternalServerException();			
		}
	}

	public House toDto(HouseEntity entity) {
		return new House(entity.getName())
			.withLink(linkTo(methodOn(HouseController.class).getOne((entity.getId()))).withSelfRel())
			.withLink(linkTo(methodOn(HouseController.class).getHouseMembers(entity.getId())).withRel("members"));
	}

}