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

import com.SafetyNet.SafetyNetAlerts.dto.FireStationDTO;
import com.SafetyNet.SafetyNetAlerts.model.FireStations;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Repository
public class FireStationRepository {
	
	 private static final Logger logger = LogManager.getLogger(FireStationRepository.class);
	
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
    private InformationRepository informationRepository;
	
	public void addFirestations(FireStations fireStations) throws IOException {
		 try {
		logger.debug("Attempting to add a new fire station: {}", fireStations);

		JsonNode root = informationRepository.readFile();
		ArrayNode fireStationsArray;

		if (root.has("firestations") && root.get("firestations").isArray()) {
		    fireStationsArray = (ArrayNode) root.get("firestations");

		    JsonNode newRecord = objectMapper.valueToTree(fireStations);
		    fireStationsArray.add(newRecord);

		    ((ObjectNode) root).set("firestations", fireStationsArray);
		    informationRepository.writeFile(root);

		    logger.info("Fire station added successfully: {}", fireStations);
		}
	} catch (Exception e) {
        logger.error("Error while adding fire station: {}", fireStations, e);
        throw e;
    }
	}
	
	public void updateFirestations(String address, FireStationDTO fireStationDTO) throws IOException {
		try {
		 logger.debug("Attempting to update fire station at address: {}", address);

		JsonNode root = informationRepository.readFile();
		JsonNode fireStationsNode = root.get("firestations");

		for (JsonNode record : fireStationsNode) {
		    if (record.get("address").asText().equals(address)) {
		        ((ObjectNode) record).put("station", fireStationDTO.getStation());
		        logger.info("Fire station updated at address: {}", address);
		        
		    }
		}

			informationRepository.writeFile(root);
		}catch(Exception e) {
			logger.error("Error while updating fire station at address: {}", address, e);
			throw e;
		}
	}
	
	public void deleteFirestations(String address) throws IOException {

		try {
            logger.debug("Attempting to delete fire station at address: {}", address);

            JsonNode root = informationRepository.readFile();
            JsonNode fireStationsNode = root.get("firestations");

            Iterator<JsonNode> iterator = fireStationsNode.iterator();
            while (iterator.hasNext()) {
                JsonNode record = iterator.next();
                if (record.get("address").asText().equals(address)) {
                    iterator.remove();
                    logger.info("Fire station deleted at address: {}", address);
                    break;
                }
            }

            informationRepository.writeFile(root);

        } catch (Exception e) {
            logger.error("Error while deleting fire station at address: {}", address, e);
            throw e;
        }
	}
	
	public Object test7(List<String> station) throws IOException {
		
		logger.debug("Fetching data for fire stations: {}", station);
		
		JsonNode root = informationRepository.readFile();
		JsonNode personsNode = root.get("persons");
		JsonNode medicalRecordsNode = root.get("medicalrecords");
		JsonNode fireStationsNode = root.get("firestations");

		List<Map<String, Object>> result = new ArrayList<>();

		for (JsonNode fireStation : fireStationsNode) {
			if (fireStation.has("station") && station.contains(fireStation.get("station").asText())) {
				String address = fireStation.get("address").asText();
				Map<String, Object> fireStations = new LinkedHashMap<>();
				fireStations.put("address", address);
				result.add(fireStations);

				for (JsonNode person : personsNode) {

					if (person.has("address") && person.get("address").asText().equals(address)) {

						String lastName = person.get("lastName").asText();
						String firstName = person.get("firstName").asText();
						String phone = person.get("phone").asText();

						Map<String, Object> persons = new LinkedHashMap<>();
						persons.put("lastName", lastName);
						persons.put("phone", phone);
						result.add(persons);

						for (JsonNode record : medicalRecordsNode) {

							if (record.has("lastName") && record.get("lastName").asText().equals(lastName) &&
									record.has("firstName") && record.get("firstName").asText().equals(firstName)) {

								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
								LocalDate dateNaissance = LocalDate.parse(record.get("birthdate").asText(), formatter);

								LocalDate today = LocalDate.now();

								Period period = Period.between(dateNaissance, today);

								Map<String, Object> medicalRecord = new LinkedHashMap<>();      
								medicalRecord.put("age", period.getYears());
								medicalRecord.put("medications", record.get("medications"));
								medicalRecord.put("allergies", record.get("allergies"));

								persons.putAll(medicalRecord);
							}
						}
					}
				}
			}
		}
		 logger.info("Fetched data for fire stations: {}", station);
		return result;
	}

	public Object test8(String address) throws IOException {
		
		 logger.debug("Fetching data for fire station at address: {}", address);
		
		JsonNode root = informationRepository.readFile();
		JsonNode personsNode = root.get("persons");
		JsonNode medicalRecordsNode = root.get("medicalrecords");
		JsonNode fireStationsNode = root.get("firestations");

		List<Map<String, Object>> result = new ArrayList<>();

		for (JsonNode fireStation : fireStationsNode) {
			if (fireStation.has("address") && fireStation.get("address").asText().equals(address)) {
				String station = fireStation.get("station").asText();
				Map<String, Object> fireStations = new LinkedHashMap<>();
				fireStations.put("station", station);
				result.add(fireStations);

				for (JsonNode person : personsNode) {

					if (person.has("address") && person.get("address").asText().equals(address)) {

						String lastName = person.get("lastName").asText();
						String firstName = person.get("firstName").asText();
						String phone = person.get("phone").asText();

						Map<String, Object> persons = new LinkedHashMap<>();
						persons.put("lastName", lastName);
						persons.put("phone", phone);
						result.add(persons);

						for (JsonNode record : medicalRecordsNode) {

							if (record.has("lastName") && record.get("lastName").asText().equals(lastName) &&
									record.has("firstName") && record.get("firstName").asText().equals(firstName)) {

								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
								LocalDate dateNaissance = LocalDate.parse(record.get("birthdate").asText(), formatter);

								LocalDate today = LocalDate.now();

								Period period = Period.between(dateNaissance, today);

								Map<String, Object> medicalRecord = new LinkedHashMap<>();      
								medicalRecord.put("age", period.getYears());
								medicalRecord.put("medications", record.get("medications"));
								medicalRecord.put("allergies", record.get("allergies"));

								persons.putAll(medicalRecord);
							}
						}
					}
				}
			}
		}
		logger.info("Fetched data for fire station at address: {}", address);
		return result;
	}

	public Object test9(String station) throws IOException {
		
		logger.debug("Fetching data for fire stations: {}", station);
		
		JsonNode root = informationRepository.readFile();
		JsonNode personsNode = root.get("persons");
		JsonNode fireStationsNode = root.get("firestations");

		List<Map<String, Object>> result = new ArrayList<>();

		for (JsonNode fireStation : fireStationsNode) {
			if (fireStation.has("station") && fireStation.get("station").asText().equals(station)) {
				String address = fireStation.get("address").asText();

				for (JsonNode person : personsNode) {

					if (person.has("address") && person.get("address").asText().equals(address)) {
						String phone = person.get("phone").asText();

						Map<String, Object> persons = new LinkedHashMap<>();
						persons.put("phone", phone);
						result.add(persons);

					}
				}
			}
		}
		logger.info("Fetched data for fire stations: {}", station);
		return result;
	}

	public Object test10(String address) throws IOException {
		
		logger.debug("Fetching data for fire station at address: {}", address);
		
		JsonNode root = informationRepository.readFile();
		JsonNode personsNode = root.get("persons");
		JsonNode medicalRecordsNode = root.get("medicalrecords");

		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> householdMembers = new LinkedHashMap<>();
		List<Map<String, Object>> adultMembers = new ArrayList<>();


		for (JsonNode person : personsNode) {

			if (person.has("address") && person.get("address").asText().equals(address)) {

				String lastName = person.get("lastName").asText();
				String firstName = person.get("firstName").asText();

				Map<String, Object> personDetails = new LinkedHashMap<>();
				personDetails.put("firstName", firstName);
				personDetails.put("lastName", lastName);

				for (JsonNode record : medicalRecordsNode) {

					if (record.has("firstName") && record.get("firstName").asText().equals(firstName) &&
							record.has("lastName") && record.get("lastName").asText().equals(lastName)) {

						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
						LocalDate dateNaissance = LocalDate.parse(record.get("birthdate").asText(), formatter);
						LocalDate today = LocalDate.now();
						Period period = Period.between(dateNaissance, today);

						if(period.getYears() <= 18) {
							personDetails.put("age", period.getYears());
							result.add(personDetails);
						}
						if(period.getYears() > 18) {
							Map<String, Object> adult = new LinkedHashMap<>();
							adult.put("firstName", firstName);
							adult.put("lastName", lastName);
							adultMembers.add(adult);					
						}
					}
				}
			}
		}	
		if (!adultMembers.isEmpty()) {
			householdMembers.put("membres du foyer", adultMembers);
			result.add(householdMembers);

		}
		logger.info("Fetched data for fire station at address: {}", address);
		return result;
	}

	public Object test11(String station) throws IOException {
		
		logger.debug("Fetching data for fire stations: {}", station);
		
		JsonNode root = informationRepository.readFile();
		JsonNode personsNode = root.get("persons");
		JsonNode medicalRecordsNode = root.get("medicalrecords");
		JsonNode fireStationsNode = root.get("firestations");

		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> medicalRecord = new LinkedHashMap<>();  
		int nomberAdult = 0;
		int nomberChild = 0;

		for (JsonNode fireStation : fireStationsNode) {
			if (fireStation.has("station") && fireStation.get("station").asText().equals(station)) {
				String address = fireStation.get("address").asText();

				for (JsonNode person : personsNode) {

					if (person.has("address") && person.get("address").asText().equals(address)) {

						String lastName = person.get("lastName").asText();
						String firstName = person.get("firstName").asText();
						String phone = person.get("phone").asText();
						String addressPerson = person.get("address").asText();

						Map<String, Object> persons = new LinkedHashMap<>();
						persons.put("firstName", firstName);
						persons.put("lastName", lastName);
						persons.put("address", addressPerson);
						persons.put("phone", phone);
						result.add(persons);

						for (JsonNode record : medicalRecordsNode) {

							if (record.has("lastName") && record.get("lastName").asText().equals(lastName) &&
									record.has("firstName") && record.get("firstName").asText().equals(firstName)) {

								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
								LocalDate dateNaissance = LocalDate.parse(record.get("birthdate").asText(), formatter);

								LocalDate today = LocalDate.now();

								Period period = Period.between(dateNaissance, today);

								if(period.getYears() <= 18) {
									nomberChild++;
								}

								if (period.getYears() > 18) {
									nomberAdult++;
								}
							}
						}
					}
				}
			}
		}

		medicalRecord.put("nomber Adult", nomberAdult);
		medicalRecord.put("nomber Child", nomberChild);
		result.add(medicalRecord);
		logger.info("Fetched data for fire stations: {}", station);
		return result;
	}
}
