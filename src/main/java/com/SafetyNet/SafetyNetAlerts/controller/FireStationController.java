package com.SafetyNet.SafetyNetAlerts.controller;

import java.io.IOException;
import java.util.List;

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

import com.SafetyNet.SafetyNetAlerts.dto.FireStationDTO;
import com.SafetyNet.SafetyNetAlerts.service.FireStationService;

@RestController
public class FireStationController {
	
	private static final Logger logger = LogManager.getLogger(FireStationController.class);

	
	@Autowired
    private FireStationService fireStationService;

	
	@GetMapping("/firestation")
    public ResponseEntity<Object> test11(@RequestParam(value = "stationNumber") String station) throws IOException {
		 try {
			 logger.debug("Start call for list of people covered by fire station corresponding for the station: {}", station);
			 Object result = fireStationService.test11(station);
	            logger.info("Successful response for the station: {}", station);
	            return ResponseEntity.ok(result);
	        } catch (IOException e) {
	            logger.error("Error retrieving station information: {}", station, e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
	        }	    	
    }
	
	@GetMapping("/childAlert")
    public ResponseEntity<Object> test10(@RequestParam(value = "address") String address) throws IOException {
		try {
            logger.debug("Start call to retrieve resident list for address: {}", address);
            Object result = fireStationService.test10(address);
            logger.info("Successful response for the address: {}", address);
            return ResponseEntity.ok(result); 
        } catch (IOException e) {
            logger.error("Error retrieving resident list for address: {}", address, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }	    	
    }
	
	@GetMapping("/phoneAlert")
    public ResponseEntity<Object> test9(@RequestParam(value = "firestation") String station) throws IOException {
		try {
            logger.debug("Start call to retrieve phone list for station: {}", station);
            Object result = fireStationService.test9(station);
            logger.info("Successful response for the station: {}", station);
            return ResponseEntity.ok(result); 
        } catch (IOException e) {
            logger.error("Error retrieving phone list for station: {}", station, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }	    	
    }
	
	@GetMapping("/fire")
    public ResponseEntity<Object> test8(@RequestParam(value = "address") String address) throws IOException {
		try {
            logger.debug("Start of call for list of residents and fire station number for address: {}", address);
            Object result = fireStationService.test8(address);
            logger.info("Successful response for the address: {}", address);
            return ResponseEntity.ok(result); 
        } catch (IOException e) {
            logger.error("Error retrieving list of inhabitants and fire station number for address: {}", address, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }	    	
    }
	
	@GetMapping("/flood/stations")
    public ResponseEntity<Object> test7(@RequestParam(value = "stations") List<String> station) throws IOException {
		 try {
	            logger.debug("Start the call to retrieve the list of all households served by the fire station for the stations: {}", station);
	            Object result = fireStationService.test7(station);
	            logger.info("Successful response for the stations: {}", station);
	            return ResponseEntity.ok(result); 
	        } catch (IOException e) {
	            logger.error("Error when retrieving the list of all households served by the fire station: {}", station, e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
	        }	    	
    }
	
	@GetMapping("/allFirestations")
    public ResponseEntity<Object> getAllFirestations() throws IOException {
		try {
            logger.debug("Start call to retrieve all fire stations");
            Object result = fireStationService.getAllFirestations();
            logger.info("Successful response to recovery of all fire stations");
            return ResponseEntity.ok(result);  
        } catch (IOException e) {
            logger.error("Error when retrieving fire stations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/firestations")
    public ResponseEntity<String> addFireStation(@RequestBody FireStationDTO fireStationDTO) throws IOException {
    	try {
            logger.debug("Start of fire station addition: {}", fireStationDTO);
            fireStationService.addFireStation(fireStationDTO);
            logger.info("Fire station successfully added: {}", fireStationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Fire station successfully added");  
        } catch (IOException e) {
            logger.error("Error adding fire station: {}", fireStationDTO, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PutMapping("/firestations/{address}")
    public ResponseEntity<String> updateFireStation(@PathVariable String address, @RequestBody FireStationDTO fireStationDTO) throws IOException {
    	try {
            logger.debug("Start of fire station update at address: {}", address);
            fireStationService.updateFireStation(address, fireStationDTO);
            logger.info("Fire station successfully updated at: {}", address);
            return ResponseEntity.status(HttpStatus.OK).body("Fire station successfully updated");  
        } catch (IOException e) {
            logger.error("Error updating fire station at address: {}", address, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @DeleteMapping("/firestations/{address}")
    public ResponseEntity<String> deleteFireStation(@PathVariable String address) throws IOException {
    	try {
            logger.debug("Start of removal of fire station at address: {}", address);
            fireStationService.deleteFireStation(address);
            logger.info("Fire station successfully removed at address: {}", address);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Fire station successfully removed"); 
        } catch (IOException e) {
            logger.error("Error deleting fire station at address: {}", address, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
