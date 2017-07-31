package com.example.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

	@Id
	@GeneratedValue
	private long id;

	private String name;

	@OneToMany
	@Singular
	private List<Address> addresses;


}
