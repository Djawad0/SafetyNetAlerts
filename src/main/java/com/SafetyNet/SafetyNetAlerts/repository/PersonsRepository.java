package com.SafetyNet.SafetyNetAlerts.repository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
	
	public void addPersons(Persons persons) throws IOException {
		
		 logger.debug("Tentative d'ajout d'une nouvelle personne : {}", persons);
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
		logger.info("Personne ajoutée avec succès : {}", persons);
	        } catch (Exception e) {
	            logger.error("Erreur lors de l'ajout de la personne : {}", persons, e);
	            throw e;
	        }
}
		
	
	public void updatePersons(String firstName, String lastName, PersonDTO personDTO) throws IOException {
		
		logger.debug("Tentative de mise à jour des informations de la personne : {} {}", firstName, lastName);

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
		 logger.info("Informations de la personne mises à jour : {} {}", firstName, lastName);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour des informations de la personne : {} {}", firstName, lastName, e);
            throw e;
        }
	}
	
	public void deletePersons(String firstName, String lastName) throws IOException {
		
		logger.debug("Tentative de suppression de la personne : {} {}", firstName, lastName);
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
		logger.info("Personne supprimée : {} {}", firstName, lastName);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la personne : {} {}", firstName, lastName, e);
            throw e;
        }
	}
	
	public Object test5(String city) throws IOException {
		logger.debug("Recherche des personnes dans la ville : {}", city);
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
		logger.info("Résultat de la recherche des personnes dans la ville : {}", city);
		return result;
		 } catch (Exception e) {
	            logger.error("Erreur lors de la recherche des personnes dans la ville : {}", city, e);
	            throw e;
	        }
	}

	public Object test6(String lastName) throws IOException {
		
		logger.debug("Recherche des informations médicales pour le nom de famille : {}", lastName);
		try {
		JsonNode root = informationRepository.readFile();
		JsonNode personsNode = root.get("persons");
		JsonNode medicalRecordsNode = root.get("medicalrecords");

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
		logger.info("Résultat de la recherche des informations médicales pour le nom de famille : {}", lastName);
		return result;
		} catch (Exception e) {
            logger.error("Erreur lors de la recherche des informations médicales pour le nom de famille : {}", lastName, e);
            throw e;
        }
	}
}
