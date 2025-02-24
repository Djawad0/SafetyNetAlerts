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
import org.springframework.web.bind.annotation.RestController;

import com.SafetyNet.SafetyNetAlerts.dto.MedicalRecordDTO;
import com.SafetyNet.SafetyNetAlerts.service.MedicalRecordService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@RestController
public class MedicalRecordController {
	
	 private static final Logger logger = LogManager.getLogger(MedicalRecordController.class);
	
	 @Autowired
	    private MedicalRecordService medicalRecordService;
	 @Autowired
	    private ObjectMapper objectMapper;

	    @GetMapping("/allMedicalRecords")
	    public ResponseEntity<Object> getAllMedicalRecords() throws IOException {
	    	logger.debug("Attempting to fetch all medical records");
	        try {
	            Object records = medicalRecordService.getAllMedicalRecords();
	            logger.info("Successfully fetched all medical records"); 
	            return ResponseEntity.ok(records); 
	        } catch (IOException e) {
	            logger.error("Error fetching medical records", e); 
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching medical records"); 
	        }
	    }

	    @PostMapping("/medicalrecords")
	    public ResponseEntity<String> addMedicalRecord(@RequestBody MedicalRecordDTO medicalRecordDTO) throws IOException {
	    	 logger.debug("Attempting to add new medical record for: {}", medicalRecordDTO);
	         try {
	        	 JsonNode root = objectMapper.readTree(new File("data.json"));
	             JsonNode medicalRecordNode = root.get("medicalrecords");
	             for (JsonNode medicalRecord : medicalRecordNode) {
	             	boolean firstNameMedicalRecord = medicalRecord.get("firstName").asText().equals(medicalRecordDTO.getFirstName());
	     			boolean lastNameMedicalRecord = medicalRecord.get("lastName").asText().equals(medicalRecordDTO.getLastName());

	     			
	     			if (firstNameMedicalRecord && lastNameMedicalRecord) {
	     				logger.error("Error while adding medical record: This medical record already exists");
	     				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This medical record already exists.");
	     			}
	     			if (medicalRecordDTO.getFirstName() == null || medicalRecordDTO.getLastName() == null || medicalRecordDTO.getBirthdate() == null
	     					|| medicalRecordDTO.getMedications() == null || medicalRecordDTO.getAllergies() == null) {
	     				logger.error("Error while adding medical record: first Name = null or last Name = null or birthdate = null or medications = null"
	     						+ " or allergies = null");
	     				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("first Name = null or last Name = null or birthdate = null or medications = null"
	     						+ " or allergies = null.");
	     			}
	             }
	             
	             medicalRecordService.addMedicalRecord(medicalRecordDTO);
	             logger.info("Medical record created successfully for: {}", medicalRecordDTO); 
	             return ResponseEntity.status(HttpStatus.CREATED).body("Medical record created successfully"); 
	         } catch (IOException e) {
	             logger.error("Error adding medical record for: {}", medicalRecordDTO, e); 
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding medical record"); 
	         }
	    }

	    @PutMapping("/medicalrecords/{firstName}/{lastName}")
	    public ResponseEntity<String> updateMedicalRecord(@PathVariable String firstName, @PathVariable String lastName, @RequestBody MedicalRecordDTO medicalRecordDTO) throws IOException {
	    	logger.debug("Attempting to update medical record for: {} {}", firstName, lastName);
	        try {
	        	 String errorMessage = null;
	             JsonNode root = objectMapper.readTree(new File("data.json"));
	             JsonNode medicalRecordNode = root.get("medicalrecords");
	             	for (JsonNode medicalRecord : medicalRecordNode) {            	
	             		boolean firstNameMedicalRecord = medicalRecord.get("firstName").asText().equals(firstName);
		     			boolean lastNameMedicalRecord = medicalRecord.get("lastName").asText().equals(lastName);  
		     			boolean birthdateMedicalRecord = medicalRecord.get("birthdate").asText().equals(medicalRecordDTO.getBirthdate());  
		     			ArrayNode medicationsMedicalRecord = (ArrayNode) medicalRecord.get("medications");
		                ArrayNode allergiesMedicalRecord = (ArrayNode) medicalRecord.get("allergies");

		
		                boolean medicationsModified = !medicationsMedicalRecord.equals(objectMapper.valueToTree(medicalRecordDTO.getMedications()));
		                boolean allergiesModified = !allergiesMedicalRecord.equals(objectMapper.valueToTree(medicalRecordDTO.getAllergies()));
		     			
	        			if (!firstNameMedicalRecord && !lastNameMedicalRecord) {
	        				errorMessage = "This person not exists.";			
	        			}
	        			else if (!medicationsModified && !allergiesModified && birthdateMedicalRecord) {
	        				logger.error("Error while update medical record: No modifications detected.");
	       	        	 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No modifications detected"); 
	        			}
	        			else if (firstNameMedicalRecord && lastNameMedicalRecord) {
	        				medicalRecordService.updateMedicalRecord(firstName, lastName, medicalRecordDTO);
	        	            logger.info("Medical record updated successfully for: {} {}", firstName, lastName); 
	        	            return ResponseEntity.status(HttpStatus.OK).body("Medical record updated successfully");   
	        			} 	            
	        	 }
	             
	             logger.error("Error while update medical record: This first Name and last Name not exists.");
	        	 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);    
	        } catch (IOException e) {
	            logger.error("Error updating medical record for: {} {}", firstName, lastName, e); 
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating medical record"); 
	        }
	    }

	    @DeleteMapping("/medicalrecords/{firstName}/{lastName}")
	    public ResponseEntity<String> deleteMedicalRecord(@PathVariable String firstName, @PathVariable String lastName) throws IOException {
	    	logger.debug("Attempting to delete medical record for: {} {}", firstName, lastName);
	        try {
	        	 String errorMessage = null;
	        	 JsonNode root = objectMapper.readTree(new File("data.json"));
	        	 JsonNode medicalRecordNode = root.get("medicalrecords");
	        	 for (JsonNode medicalRecord : medicalRecordNode) {
		            	boolean firstNameMedicalRecord = medicalRecord.get("firstName").asText().equals(firstName);
	        			boolean lastNameMedicalRecord = medicalRecord.get("lastName").asText().equals(lastName);
	        			if (firstNameMedicalRecord && lastNameMedicalRecord) {
	        				 medicalRecordService.deleteMedicalRecord(firstName, lastName);
	        		            logger.info("Medical record deleted successfully for: {} {}", firstName, lastName); 
	        		            return ResponseEntity.status(HttpStatus.OK).body("Medical record deleted successfully");
	        			} 
	        			else if (!firstNameMedicalRecord && !lastNameMedicalRecord || firstNameMedicalRecord && !lastNameMedicalRecord || !firstNameMedicalRecord && lastNameMedicalRecord) {
	        				errorMessage = "This medical record not exists.";			
	        			}	            
	        	 }
	        	 logger.error("Error while delete medical record: This medical record not exists.");
	        	 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
       	
	        } catch (IOException e) {
	            logger.error("Error deleting medical record for: {} {}", firstName, lastName, e); 
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting medical record"); 
	        }
	    }
}
