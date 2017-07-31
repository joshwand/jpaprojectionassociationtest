package com.example.projection;

import com.example.entity.Person;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {Person.class}, name = "personSparseProjection")
public interface PersonSparseProjection {

	String getName();

}
