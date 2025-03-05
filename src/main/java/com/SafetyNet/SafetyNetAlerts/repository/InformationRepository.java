package com.SafetyNet.SafetyNetAlerts.repository;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;



@Repository
public class InformationRepository {

	private static final Logger logger = LogManager.getLogger(InformationRepository.class);

	private File jsonFile = new File("data.json");  
	private ObjectMapper objectMapper = new ObjectMapper();

	/*
	 * This method reads a Json file.
	 */

	public JsonNode readFile() {
		try {
			logger.debug("Reading the file: {}", jsonFile.getAbsolutePath());
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			JsonNode root = objectMapper.readTree(jsonFile);
			logger.info("File read successfully: {}", jsonFile.getAbsolutePath());
			return root;
		} catch (IOException e) {
			logger.error("Error reading file: {}", jsonFile.getAbsolutePath(), e);
			return null;
		}

	}

	/*
	 * This method allows you to write to a Json file.
	 */

	public void writeFile(JsonNode root) {

		try {
			logger.debug("Writing data to file: {}", jsonFile.getAbsolutePath());
			objectMapper.writeValue(jsonFile, root);
			logger.info("Data written successfully to file: {}", jsonFile.getAbsolutePath());
		} catch (IOException e) {
			logger.error("Error writing to file: {}", jsonFile.getAbsolutePath(), e);
		}

	}

	/*
	 *  This method reads the content of persons in the Json file.
	 */

	public Object readPersons() {
		try {
			logger.debug("Reading 'persons' from file: {}", jsonFile.getAbsolutePath());
			Map<String, Object> jsonMap = objectMapper.readValue(jsonFile, new TypeReference<Map<String,Object>>(){});
			Object data = jsonMap.get("persons");
			logger.info("'persons' data read successfully from file.");
			return data;
		} catch (IOException e) {
			logger.error("Error reading 'persons' from file: {}", jsonFile.getAbsolutePath(), e);
			return null;
		}
	}

	/*
	 *  This method reads the contents of medical records in the Json file.
	 */

	public Object readMedicalRecords() {	
		try {
			logger.debug("Reading 'medicalrecords' from file: {}", jsonFile.getAbsolutePath());
			Map<String, Object> jsonMap = objectMapper.readValue(jsonFile, new TypeReference<Map<String,Object>>(){});
			Object data = jsonMap.get("medicalrecords");
			logger.info("'medicalrecords' data read successfully from file.");
			return data;
		} catch (IOException e) {
			logger.error("Error reading 'medicalrecords' from file: {}", jsonFile.getAbsolutePath(), e);
			return null;
		}
	}

	/*
	 *  This method reads the fire station content from the Json file.
	 */

	public Object readFirestations() {	
		try {
			logger.debug("Reading 'firestations' from file: {}", jsonFile.getAbsolutePath());
			Map<String, Object> jsonMap = objectMapper.readValue(jsonFile, new TypeReference<Map<String,Object>>(){});
			Object data = jsonMap.get("firestations");
			logger.info("'firestations' data read successfully from file.");
			return data;
		} catch (IOException e) {
			logger.error("Error reading 'firestations' from file: {}", jsonFile.getAbsolutePath(), e);
			return null;
		}
	}

}
