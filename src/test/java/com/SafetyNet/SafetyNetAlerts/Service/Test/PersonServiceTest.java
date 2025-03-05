package com.SafetyNet.SafetyNetAlerts.Service.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


import java.io.IOException;
import java.util.Arrays;

import com.SafetyNet.SafetyNetAlerts.dto.PersonDTO;
import com.SafetyNet.SafetyNetAlerts.model.Persons;
import com.SafetyNet.SafetyNetAlerts.repository.InformationRepository;
import com.SafetyNet.SafetyNetAlerts.repository.PersonsRepository;
import com.SafetyNet.SafetyNetAlerts.service.PersonService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PersonServiceTest {
	@Mock
	private InformationRepository informationRepository;
	@Mock
	private PersonsRepository personsRepository;

	@InjectMocks
	private PersonService personService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetAllPersons() throws IOException {

		when(informationRepository.readPersons()).thenReturn(Arrays.asList(new Persons("John", "Doe", "123 Main St", "City", "12345", "123-456-7890", "john.doe@example.com")));


		Object result = personService.getAllPersons();


		assertNotNull(result);
		verify(informationRepository).readPersons();
	}

	@Test
	public void testAddPerson() throws IOException {

		PersonDTO personDTO = new PersonDTO("John", "Doe", "123 Main St", "City", "12345", "123-456-7890", "john.doe@example.com");
		Persons person = new Persons(personDTO.getFirstName(), personDTO.getLastName(), personDTO.getAddress(), personDTO.getCity(), personDTO.getZip(), personDTO.getPhone(), personDTO.getEmail());


		personsRepository.addPersons(person);
		personService.addPerson(personDTO);


		verify(personsRepository).addPersons(person);
	}

	@Test
	public void testUpdatePersons() throws IOException {

		PersonDTO personDTO = new PersonDTO("John", "Doe", "123 Main St", "City", "12345", "123-456-7890", "john.doe@example.com");


		personService.updatePersons("John", "Doe", personDTO);


		verify(personsRepository).updatePersons("John", "Doe", personDTO);
	}

	@Test
	public void testDeletePerson() throws IOException {

		String firstName = "John";
		String lastName = "Doe";


		personService.deletePerson(firstName, lastName);


		verify(personsRepository).deletePersons(firstName, lastName);
	}

	@Test
	public void testGetEmailOfAllCityResidents() throws IOException {

		String city = "City";


		personService.getEmailOfAllCityResidents(city);


		verify(personsRepository).getEmailOfAllCityResidents(city);
	}

	@Test
	public void testGetPersonInfo() throws IOException {

		String lastName = "Doe";


		personService.getPersonInfo(lastName);


		verify(personsRepository).getPersonInfo(lastName);
	}
}
