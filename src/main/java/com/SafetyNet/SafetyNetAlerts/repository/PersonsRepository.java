package com.SafetyNet.SafetyNetAlerts.repository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.SafetyNet.SafetyNetAlerts.dto.PersonDTO;
import com.SafetyNet.SafetyNetAlerts.model.Persons;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Repository
public class PersonsRepository {

	private static final Logger logger = LogManager.getLogger(PersonsRepository.class);

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private InformationRepository informationRepository;

	/*
	 * This method lets you add a person.
	 */

	public void addPersons(Persons persons) throws IOException {

		logger.debug("Attempt to add a new person : {}", persons);
		try {
			JsonNode root = informationRepository.readFile();
			ArrayNode personsArray;

			if (root.has("persons") && root.get("persons").isArray()) {
				personsArray = (ArrayNode) root.get("persons");

				JsonNode newRecord = objectMapper.valueToTree(persons);
				personsArray.add(newRecord);

				((ObjectNode) root).set("persons", personsArray);   
			}

			informationRepository.writeFile(root);
			logger.info("Person successfully added : {}", persons);
		} catch (Exception e) {
			logger.error("Error adding person : {}", persons, e);
			throw e;
		}
	}


	/*
	 * This method is used to update a person.
	 */

	public void updatePersons(String firstName, String lastName, PersonDTO personDTO) throws IOException {

		logger.debug("Attempt to update personal information : {} {}", firstName, lastName);

		try {
			JsonNode root = informationRepository.readFile();  
			JsonNode personsNode = root.get("persons");

			for (JsonNode record : personsNode) {
				if (record.get("firstName").asText().equals(firstName) && record.get("lastName").asText().equals(lastName)) {      
					((ObjectNode) record).put("address", personDTO.getAddress());
					((ObjectNode) record).put("city", personDTO.getCity());
					((ObjectNode) record).put("zip", personDTO.getZip());
					((ObjectNode) record).put("phone", personDTO.getPhone());
					((ObjectNode) record).put("email", personDTO.getEmail());   
				}
			}

			informationRepository.writeFile(root);
			logger.info("Updated personal information : {} {}", firstName, lastName);
		} catch (Exception e) {
			logger.error("Error updating personal information : {} {}", firstName, lastName, e);
			throw e;
		}
	}


	/*
	 * This method allows you to delete a person.
	 */

	public void deletePersons(String firstName, String lastName) throws IOException {

		logger.debug("Attempt to suppress the person : {} {}", firstName, lastName);
		try {
			JsonNode root = informationRepository.readFile();
			JsonNode personsNode = root.get("persons");

			Iterator<JsonNode> iterator = personsNode.iterator();
			while (iterator.hasNext()) {
				JsonNode record = iterator.next();
				if (record.get("firstName").asText().equals(firstName) && record.get("lastName").asText().equals(lastName)) {
					iterator.remove();                
				}
			}

			informationRepository.writeFile(root);
			logger.info("Deleted person : {} {}", firstName, lastName);
		} catch (Exception e) {
			logger.error("Error when deleting a person : {} {}", firstName, lastName, e);
			throw e;
		}
	}

	/*
	 *  This method returns a list of the e-mail addresses of all the town's inhabitants.
	 */

	public Object getEmailOfAllCityResidents(String city) throws IOException {
		logger.debug("Search for people in the city : {}", city);
		try {
			JsonNode root = informationRepository.readFile();  
			JsonNode personsNode = root.get("persons");
			List<Map<String, JsonNode>> result = new ArrayList<>();

			for (JsonNode record : personsNode) {
				if (record.get("city").asText().equals(city)) {    
					Map<String, JsonNode> persons = new LinkedHashMap<>();
					persons.put("email", record.get("email"));   
					result.add(persons);
				}
			}
			logger.info("Search results for people in the city : {}", city);
			return result;
		} catch (Exception e) {
			logger.error("Error searching for people in the city : {}", city, e);
			throw e;
		}
	}

	/*
	 * This method returns the name, address, age, e-mail address and medical history of each resident.
	 * (medication, dosage and allergies) of each resident.
	 */

	public Object getPersonInfo(String lastName) throws IOException {

		logger.debug("Medical information search for surname : {}", lastName);
		try {
			JsonNode root = informationRepository.readFile();
			JsonNode personsNode = root.get("persons");
			JsonNode medicalRecordsNode = root.get("medicalrecords");

			if (personsNode == null || medicalRecordsNode == null) {
				logger.error("Nodes personsNode or medicalRecordsNode are null.");
				return Collections.emptyList(); 
			}

			List<Map<String, Object>> result = new ArrayList<>();

			for (JsonNode person : personsNode) {

				if (person.has("lastName") && person.get("lastName").asText().equals(lastName)) {
					String address = person.get("address").asText();
					String firstName = person.get("firstName").asText();
					String email = person.get("email").asText();

					for (JsonNode record : medicalRecordsNode) {

						if (record.has("lastName") && record.has("firstName") 
								&& record.get("lastName").asText().equals(lastName) 
								&& record.get("firstName").asText().equals(firstName)) {

							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
							LocalDate dateNaissance = LocalDate.parse(record.get("birthdate").asText(), formatter);

							LocalDate today = LocalDate.now();

							Period period = Period.between(dateNaissance, today);

							Map<String, Object> medicalRecord = new LinkedHashMap<>();			
							medicalRecord.put("lastName", lastName);
							medicalRecord.put("address", address);
							medicalRecord.put("age", period.getYears());
							medicalRecord.put("email", email);
							medicalRecord.put("medications", record.get("medications"));
							medicalRecord.put("allergies", record.get("allergies"));

							result.add(medicalRecord);
						}
					}
				}
			}
			logger.info("Result of medical information search for surname : {}", lastName);
			return result;
		} catch (Exception e) {
			logger.error("Error searching for medical information for surname : {}", lastName, e);
			throw e;
		}
	}
}
