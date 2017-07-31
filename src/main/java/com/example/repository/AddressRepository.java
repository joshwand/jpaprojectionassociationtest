package com.example.repository;

import com.example.entity.Address;
import com.example.projection.AddressExcerptProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Having the excerpt projection defined causes the association Person#addresses to be queried no matter what, even if
 * the projection doesn't need it.
 * <p>
 * If you comment this out, asking for a projection with addresses only does one query to retrieve the addresses
 */
@RepositoryRestResource(excerptProjection = AddressExcerptProjection.class)
public interface AddressRepository extends JpaRepository<Address, Long> {
}
