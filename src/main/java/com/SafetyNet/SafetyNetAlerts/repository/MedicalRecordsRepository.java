package com.SafetyNet.SafetyNetAlerts.repository;

import java.io.IOException;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.SafetyNet.SafetyNetAlerts.dto.MedicalRecordDTO;
import com.SafetyNet.SafetyNetAlerts.model.MedicalRecords;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Repository
public class MedicalRecordsRepository {
	
	private static final Logger logger = LogManager.getLogger(MedicalRecordsRepository.class);
	
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
    private InformationRepository informationRepository;

	public void addMedicalRecords(MedicalRecords medicalRecords) throws IOException {
		
		logger.debug("Tentative d'ajout d'un nouveau dossier médical : {}", medicalRecords);
		try {
		JsonNode root = informationRepository.readFile();
		
		

		ArrayNode medicalRecordsArray;
		
		 JsonNode medicalRecordsNode = root.get("medicalrecords");
		 
		    if (medicalRecordsNode == null || !medicalRecordsNode.isArray()) {
		        throw new IOException("No medical records node found");
		    }

		if (root.has("medicalrecords") && root.get("medicalrecords").isArray()) {
			medicalRecordsArray = (ArrayNode) root.get("medicalrecords");

			JsonNode newRecord = objectMapper.valueToTree(medicalRecords);
			medicalRecordsArray.add(newRecord);

			((ObjectNode) root).set("medicalrecords", medicalRecordsArray);

		}

		informationRepository.writeFile(root);
		logger.info("Dossier médical ajouté avec succès : {}", medicalRecords);
		} catch (Exception e) {
            logger.error("Erreur lors de l'ajout du dossier médical : {}", medicalRecords, e);
            throw e;
        }

	}
	
	public void updateMedicalrecords(String firstName, String lastName, MedicalRecordDTO medicalRecordDTO) throws IOException {

		 logger.debug("Tentative de mise à jour du dossier médical pour {} {}", firstName, lastName);
		 try {
		JsonNode root = informationRepository.readFile();

		JsonNode medicalRecordsNode = root.get("medicalrecords");

		for (JsonNode record : medicalRecordsNode) {
			if (record.get("firstName").asText().equals(firstName) && record.get("lastName").asText().equals(lastName)) {     
				((ObjectNode) record).put("birthdate", medicalRecordDTO.getBirthdate());
				ArrayNode medicationsArray = objectMapper.createArrayNode();
				for (String medication : medicalRecordDTO.getMedications()) {
					medicationsArray.add(medication);
				}
				((ObjectNode) record).set("medications", medicationsArray);

				ArrayNode allergiesArray = objectMapper.createArrayNode();
				for (String allergy : medicalRecordDTO.getAllergies()) {
					allergiesArray.add(allergy);
				}
				((ObjectNode) record).set("allergies", allergiesArray);
				 logger.info("Dossier médical mis à jour pour {} {}", firstName, lastName);

			}
		}

		informationRepository.writeFile(root);
		 } catch (Exception e) {
	            logger.error("Erreur lors de la mise à jour du dossier médical de {} {}", firstName, lastName, e);
	            throw e;
	        }
	}
	
	public void deleteMedicalrecords(String firstName, String lastName) throws IOException {
		
		 logger.debug("Tentative de suppression du dossier médical pour {} {}", firstName, lastName);
		 try {
		JsonNode root = informationRepository.readFile();  
		JsonNode medicalRecordsNode = root.get("medicalrecords");

		Iterator<JsonNode> iterator = medicalRecordsNode.iterator();
		while (iterator.hasNext()) {
			JsonNode record = iterator.next();
			if (record.get("firstName").asText().equals(firstName) && record.get("lastName").asText().equals(lastName)) {      
				iterator.remove(); 
				logger.info("Dossier médical supprimé pour {} {}", firstName, lastName);
			}
		}

		informationRepository.writeFile(root);
		 } catch (Exception e) {
	            logger.error("Erreur lors de la suppression du dossier médical de {} {}", firstName, lastName, e);
	            throw e;
	        }
	}

	
}
