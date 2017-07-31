package com.example.projection;

import com.example.entity.Address;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "AddressExcerptProjection", types = {Address.class})
public interface AddressExcerptProjection {

	String getName();
}
