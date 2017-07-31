package com.example;

import com.example.entity.Address;
import com.example.entity.Person;
import com.example.repository.AddressRepository;
import com.example.repository.PersonRepository;
import io.restassured.RestAssured;
import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.Slf4JLoggingSystem;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@Log
public class JpaProjectionAssociationIntegrationTest {

	@Autowired
	AddressRepository addressRepository;
	@Autowired
	PersonRepository personRepository;
	Address addressOne, addressTwo;
	Person person;
	@Value("${local.server.port}")
	int port;
	@Autowired
	private Slf4JLoggingSystem loggingSystem;

	@Before
	public void setUp() {

		RestAssured.port = port;

		this.person = personRepository.save(Person.builder().name("Joe Blow").build());
		this.addressOne = addressRepository.save(Address.builder().name("address1").person(person).build());
		this.addressTwo = addressRepository.save(Address.builder().name("address 2").person(person).build());

		personRepository.flush();
	}

	/**
	 * this should only show 1 sql statement -- to get addresses (select person0_.id as id1_1_0_, person0_.name as name2_1_0_ from person person0_ where person0_.id=?)
	 * actual: does 2:
	 * <p>
	 * select person0_.id as id1_1_0_, person0_.name as name2_1_0_ from person person0_ where person0_.id=?
	 * select addresses0_.person_id as person_i1_2_0_, addresses0_.addresses_id as addresse2_2_0_, address1_.id as id1_0_1_, address1_.name as name2_0_1_, address1_.person_id as person_i3_0_1_, person2_.id as id1_1_2_, person2_.name as name2_1_2_ from person_addresses addresses0_ inner join address address1_ on addresses0_.addresses_id=address1_.id left outer join person person2_ on address1_.person_id=person2_.id where addresses0_.person_id=?
	 */
	@Test
	public void testDataRestSparseProjection() {

		turnOnSqlLogging();

		given().get("/persons/" + person.getId() + "?projection=personSparseProjection").then()
				.log().body().and()
				.assertThat().body("embedded.addresses", isEmptyOrNullString());

		turnOffSqlLogging();

	}

	/**
	 * expected: this should only show 1 sql statement -- with a join to get addresses in one query.
	 * Also, it should show the addresses inline as requested in {@link com.example.projection.PersonWithAddresses}
	 * actual: does 2 queries, and doesn't show the inline addresses as requested
	 */
	@Test
	public void testDataRestRichProjection() {

		turnOnSqlLogging();

		given().get("/persons/" + person.getId() + "?projection=personWithAddresses").then()
				.log().body().and()
				.assertThat().body("addresses", hasSize(2));

		turnOffSqlLogging();

	}

	private void turnOnSqlLogging() {
		loggingSystem.setLogLevel("org.hibernate.stat", LogLevel.DEBUG);
		loggingSystem.setLogLevel("org.hibernate.SQL", LogLevel.DEBUG);
		loggingSystem.setLogLevel("org.hibernate", LogLevel.INFO);
		loggingSystem.setLogLevel("org.hibernate.type.descriptor.sql.basicbinder", LogLevel.TRACE);
	}

	private void turnOffSqlLogging() {
		loggingSystem.setLogLevel("org.hibernate.stat", LogLevel.ERROR);
		loggingSystem.setLogLevel("org.hibernate.SQL", LogLevel.ERROR);
		loggingSystem.setLogLevel("org.hibernate", LogLevel.ERROR);
		loggingSystem.setLogLevel("org.hibernate.type.descriptor.sql.basicbinder", LogLevel.ERROR);

	}

	@Configuration
	@EnableAutoConfiguration
	static class Config {
	}
}
