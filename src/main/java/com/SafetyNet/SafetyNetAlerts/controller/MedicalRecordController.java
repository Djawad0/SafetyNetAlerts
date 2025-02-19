package com.SafetyNet.SafetyNetAlerts.controller;

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

@RestController
public class MedicalRecordController {
	
	 private static final Logger logger = LogManager.getLogger(MedicalRecordController.class);
	
	 @Autowired
	    private MedicalRecordService medicalRecordService;

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
	            medicalRecordService.updateMedicalRecord(firstName, lastName, medicalRecordDTO);
	            logger.info("Medical record updated successfully for: {} {}", firstName, lastName); 
	            return ResponseEntity.status(HttpStatus.OK).body("Medical record updated successfully"); 
	        } catch (IOException e) {
	            logger.error("Error updating medical record for: {} {}", firstName, lastName, e); 
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating medical record"); 
	        }
	    }

	    @DeleteMapping("/medicalrecords/{firstName}/{lastName}")
	    public ResponseEntity<String> deleteMedicalRecord(@PathVariable String firstName, @PathVariable String lastName) throws IOException {
	    	logger.debug("Attempting to delete medical record for: {} {}", firstName, lastName);
	        try {
	            medicalRecordService.deleteMedicalRecord(firstName, lastName);
	            logger.info("Medical record deleted successfully for: {} {}", firstName, lastName); 
	            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Medical record deleted successfully"); 
	        } catch (IOException e) {
	            logger.error("Error deleting medical record for: {} {}", firstName, lastName, e); 
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting medical record"); 
	        }
	    }
}
