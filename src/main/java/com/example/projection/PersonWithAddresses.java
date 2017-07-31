package com.example.projection;

import com.example.entity.Address;
import com.example.entity.Person;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

@Projection(types = {Person.class}, name = "personWithAddresses")
public interface PersonWithAddresses {

	String getName();

	List<Address> getAddresses();
}
