package com.SafetyNet.SafetyNetAlerts.controller;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SafetyNet.SafetyNetAlerts.dto.PersonDTO;
import com.SafetyNet.SafetyNetAlerts.service.PersonService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
public class PersonController {

	private static final Logger logger = LogManager.getLogger(PersonController.class);

	@Autowired
	private PersonService personService;

	@Autowired
	private ObjectMapper objectMapper;

	/*
	 * This endpoint returns a list of all persons.
	 */

	@GetMapping("/allPersons")
	public ResponseEntity<Object> getAllPersons() throws IOException {
		logger.debug("Fetching all persons");
		try {
			Object persons = personService.getAllPersons();
			logger.info("Successfully fetched all persons.");
			return ResponseEntity.ok(persons); 
		} catch (IOException e) {
			logger.error("Error fetching persons: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching persons"); 
		}
	}

	/*
	 * This endpoint returns a list of the e-mail addresses of all the city's inhabitants.
	 */

	@GetMapping("/communityEmail")
	public ResponseEntity<Object> getEmailOfAllCityResidents(@RequestParam(value = "city") String city) throws IOException {
		logger.debug("Email recovery for the city: {}", city);
		try {
			Object emails = personService.getEmailOfAllCityResidents(city);
			logger.info("The city's email search has been successfully completed: {}", city);
			return ResponseEntity.ok(emails); 
		} catch (IOException e) {
			logger.error("Error in email recovery for the city: {}", city, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in email recovery"); 
		}    	
	}

	/*
	 * This endpoint returns the name, address, age, e-mail address and medical history
	 * (medication, dosage and allergies) of each resident.
	 */

	@GetMapping("/personInfo")
	public ResponseEntity<Object> getPersonInfo(@RequestParam(name = "lastName") String lastName) throws IOException {
		logger.debug("Fetching person info for lastName: {}", lastName);
		try {
			Object personInfo = personService.getPersonInfo(lastName);
			logger.info("Successfully fetched person info for lastName: {}", lastName);
			return ResponseEntity.ok(personInfo); 
		} catch (IOException e) {
			logger.error("Error fetching person info for lastName: {}", lastName, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching person info"); 
		}	    	

	}

	/*
	 * This endpoint is used to add a person.
	 */

	@PostMapping("/persons")
	public ResponseEntity<String> addPerson(@RequestBody PersonDTO personDTO) throws IOException {
		logger.debug("Attempting to add person: {}", personDTO);
		try {     
			JsonNode root = objectMapper.readTree(new File("data.json"));
			JsonNode medicalRecordsNode = root.get("medicalrecords");
			JsonNode fireStationsNode = root.get("firestations");
			JsonNode personsNode = root.get("persons");
			String errorMessage = null;
			for (JsonNode person : personsNode) {
				boolean firstNamePerson = person.get("firstName").asText().equals(personDTO.getFirstName());
				boolean lastNamePerson = person.get("lastName").asText().equals(personDTO.getLastName());
				if (firstNamePerson && lastNamePerson) {
					logger.error("Error while adding person: This person already exists");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This person already exists.");
				}
				if (personDTO.getFirstName() == null || personDTO.getLastName() == null || personDTO.getAddress() == null || personDTO.getCity() == null
						|| personDTO.getZip() == null || personDTO.getPhone() == null || personDTO.getEmail() == null) {
					logger.error("Error while adding medical record: first Name = null or last Name = null or address = null or city = null or zip = null"
							+ "or phone = null or email = null");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("first Name = null or last Name = null"
							+ " or address = null or city = null or zip = null or phone = null or email = null.");
				}
			}
			for (JsonNode record : medicalRecordsNode) {
				for (JsonNode records : fireStationsNode) {
					boolean firstName = record.get("firstName").asText().equals(personDTO.getFirstName());
					boolean lastName = record.get("lastName").asText().equals(personDTO.getLastName());
					boolean address = records.get("address").asText().equals(personDTO.getAddress());  	

					if (firstName && lastName && address) {
						personService.addPerson(personDTO);
						logger.info("Successfully added person: {}", personDTO);
						return ResponseEntity.status(HttpStatus.CREATED).body("Person added"); 
					} else if (!firstName && !lastName && !address) {
						errorMessage = "FirstName, LastName not found in medical records and Address not found in fire stations.";
					} else if (!firstName && !lastName && address) {
						errorMessage = "FirstName and LastName not found in medical records.";
					} else if (firstName && lastName && !address) {
						errorMessage = "Address not found in fire stations.";
					}
				}
			}

			logger.error("Error while adding person: {}", errorMessage);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage); 
		} catch (IOException e) {
			logger.error("Error adding person: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding person");
		}



	}

	/*
	 * This endpoint is used to update a person.
	 */

	@PutMapping("/persons/{firstName}/{lastName}")
	public ResponseEntity<String> updatePerson(@PathVariable String firstName, @PathVariable String lastName, @RequestBody PersonDTO personDTO) throws IOException {
		logger.debug("Attempting to update person with firstName: {} and lastName: {}", firstName, lastName);
		try {
			String errorMessage = null;
			JsonNode root = objectMapper.readTree(new File("data.json"));
			JsonNode personsNode = root.get("persons");
			for (JsonNode person : personsNode) {
				boolean firstNamePerson = person.get("firstName").asText().equals(firstName);
				boolean lastNamePerson = person.get("lastName").asText().equals(lastName);
				boolean firstNameDTO = person.get("firstName").asText().equals(personDTO.getFirstName());
				boolean lastNameDTO = person.get("lastName").asText().equals(personDTO.getLastName());
				boolean address = person.get("address").asText().equals(personDTO.getAddress());
				boolean city = person.get("city").asText().equals(personDTO.getCity());
				boolean zip = person.get("zip").asText().equals(personDTO.getZip());
				boolean phone = person.get("phone").asText().equals(personDTO.getPhone());
				boolean email = person.get("email").asText().equals(personDTO.getEmail());
				if (!firstNamePerson && !lastNamePerson || firstNamePerson && !lastNamePerson || !firstNamePerson && lastNamePerson) {
					errorMessage = "This person not exists.";			
				}
				else if (firstNameDTO && lastNameDTO && address && city && zip && phone && email) {
					logger.error("Error while update person: No modifications detected.");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No modifications detected"); 
				}
				else if (firstNamePerson && lastNamePerson) {
					personService.updatePersons(firstName, lastName, personDTO);
					logger.info("Successfully updated person: {} {}", firstName, lastName);
					return ResponseEntity.status(HttpStatus.OK).body("Person updated successfully"); 
				} 	            
			}
			logger.error("Error while update person: This person not exists.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);      
		} catch (IOException e) {
			logger.error("Error updating person: {} {}", firstName, lastName, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating person"); 
		}
	}

	/*
	 * This endpoint is used to delete a person.
	 */

	@DeleteMapping("/persons/{firstName}/{lastName}")
	public ResponseEntity<String> deletePerson(@PathVariable String firstName, @PathVariable String lastName) throws IOException {
		logger.debug("Attempting to delete person with firstName: {} and lastName: {}", firstName, lastName);
		try {
			String errorMessage = null;
			JsonNode root = objectMapper.readTree(new File("data.json"));
			JsonNode personsNode = root.get("persons");
			for (JsonNode person : personsNode) {
				boolean firstNamePerson = person.get("firstName").asText().equals(firstName);
				boolean lastNamePerson = person.get("lastName").asText().equals(lastName);
				if (firstNamePerson && lastNamePerson) {
					personService.deletePerson(firstName, lastName);	
					logger.info("Successfully deleted person: {} {}", firstName, lastName);   				
					return ResponseEntity.status(HttpStatus.OK).body("Person deleted successfully"); 
				} 
				else if (!firstNamePerson && !lastNamePerson || firstNamePerson && !lastNamePerson || !firstNamePerson && lastNamePerson) {
					errorMessage = "This person not exists.";			
				}	            
			}
			logger.error("Error while delete person: This person not exists.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);

		} catch (IOException e) {
			logger.error("Error deleting person: {} {}", firstName, lastName, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting person"); 
		}
	}
}
