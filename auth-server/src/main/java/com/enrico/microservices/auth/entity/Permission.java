package com.enrico.microservices.auth.entity;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseIdEntity {

	private static final long serialVersionUID = 1L;

	private String name;

}
