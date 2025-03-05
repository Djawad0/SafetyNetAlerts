package com.SafetyNet.SafetyNetAlerts.controller.test;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.io.IOException;


import com.SafetyNet.SafetyNetAlerts.controller.PersonController;

import com.SafetyNet.SafetyNetAlerts.dto.PersonDTO;
import com.SafetyNet.SafetyNetAlerts.service.PersonService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class PersonControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private PersonService personService;

	@InjectMocks
	PersonController personController;

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() throws IOException {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void testGetAllPersons() throws Exception {
		when(personService.getAllPersons()).thenReturn("[]");

		mockMvc.perform(get("/allPersons"))
		.andExpect(status().isOk())
		.andExpect(content().string("[]"));

		verify(personService, times(1)).getAllPersons();
	} 

	@Test
	public void testAddPerson_MissingRequiredFields() throws Exception {

		PersonDTO incompletePersonDTO = new PersonDTO("Jane", "Doe", "456 Avenue", "City", "67890", null, "jane.doe@example.com");

		mockMvc.perform(MockMvcRequestBuilders.post("/persons")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(incompletePersonDTO)))
		.andExpect(MockMvcResultMatchers.status().isBadRequest())
		.andExpect(MockMvcResultMatchers.content().string("first Name = null or last Name = null or address = null or city = null or zip = null or phone = null or email = null."));
	}

	@Test
	public void testAddPerson_MedicalRecordNotFound() throws Exception {

		PersonDTO personDTO = new PersonDTO("Nonexistent", "Person", "789 Road", "City", "99999", "987-654-3210", "nonexistent.person@example.com");

		mockMvc.perform(MockMvcRequestBuilders.post("/persons")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(personDTO)))
		.andExpect(MockMvcResultMatchers.status().isBadRequest())
		.andExpect(MockMvcResultMatchers.content().string("FirstName, LastName not found in medical records and Address not found in fire stations."));
	}

	@Test
	public void testUpdatePerson_NotFound() throws Exception {
		PersonDTO updatedPersonDTO = new PersonDTO("John", "Doe", "456 New St", "New City", "67890", "987-654-3210", "john.doe2@example.com");

		doThrow(new RuntimeException("This person does not exist."))
		.when(personService).updatePersons(eq("John"), eq("Doe"), any(PersonDTO.class));

		mockMvc.perform(put("/persons/John/Doe")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(updatedPersonDTO)))
		.andExpect(status().isNotFound())
		.andExpect(content().string("This person not exists."));
	}

	@Test
	public void testDeletePerson_NotFound() throws Exception {
		doThrow(new RuntimeException("This person does not exist."))
		.when(personService).deletePerson(eq("John"), eq("Doe"));

		mockMvc.perform(delete("/persons/John/Doe"))
		.andExpect(status().isNotFound())
		.andExpect(content().string("This person not exists."));
	}

	@Test
	public void testGetEmailOfAllCityResidents() throws Exception {
		when(personService.getEmailOfAllCityResidents("City")).thenReturn("[\"email1@example.com\", \"email2@example.com\"]");

		mockMvc.perform(get("/communityEmail").param("city", "City"))
		.andExpect(status().isOk())
		.andExpect(content().string("[\"email1@example.com\", \"email2@example.com\"]"));

		verify(personService, times(1)).getEmailOfAllCityResidents("City");
	}

	@Test
	public void testGetPersonInfo() throws Exception {
		when(personService.getPersonInfo("Doe")).thenReturn("{\"firstName\": \"John\", \"lastName\": \"Doe\"}");

		mockMvc.perform(get("/personInfo").param("lastName", "Doe"))
		.andExpect(status().isOk())
		.andExpect(content().json("{\"firstName\": \"John\", \"lastName\": \"Doe\"}"));

		verify(personService, times(1)).getPersonInfo("Doe");
	}

	@Test
	public void testGetPersonInfo_InternalServerError() throws Exception {	
		when(personService.getPersonInfo(any())).thenThrow(new IOException()); 

		mockMvc.perform(get("/personInfo").param("lastName", "Doe"))
		.andExpect(status().isInternalServerError()); 

		verify(personService, times(1)).getPersonInfo("Doe");

	}

	@Test
	public void testGetEmailOfAllCityResidents_InternalServerError() throws Exception {	
		when(personService.getEmailOfAllCityResidents(any())).thenThrow(new IOException()); 

		mockMvc.perform(get("/communityEmail").param("city", "Culver"))
		.andExpect(status().isInternalServerError()); 

		verify(personService, times(1)).getEmailOfAllCityResidents("Culver");

	}

	@Test
	public void testGetAllPersons_InternalServerError() throws Exception {	
		when(personService.getAllPersons()).thenThrow(new IOException()); 

		mockMvc.perform(get("/allPersons"))
		.andExpect(status().isInternalServerError()); 

		verify(personService, times(1)).getAllPersons();

	}
}
