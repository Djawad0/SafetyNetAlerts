package com.SafetyNet.SafetyNetAlerts.controller;

import java.io.File;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class FireStationController {

	private static final Logger logger = LogManager.getLogger(FireStationController.class);


	@Autowired
	private FireStationService fireStationService;
	@Autowired
	private ObjectMapper objectMapper;


	/*
	 * This endpoint returns a list of people covered by the corresponding
		fire station.
	 */

	@GetMapping("/firestation")
	public ResponseEntity<Object> getPersonsCoveredByTheFireStation(@RequestParam(value = "stationNumber") String station) throws IOException {
		try {
			logger.debug("Start call for list of people covered by fire station corresponding for the station: {}", station);
			Object result = fireStationService.getPersonsCoveredByTheFireStation(station);
			logger.info("Successful response for the station: {}", station);
			return ResponseEntity.ok(result);
		} catch (IOException e) {
			logger.error("Error retrieving station information: {}", station, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}	    	
	}

	/*
	 *  This endpoint returns a list of children (any individual aged 18 or under)
	 * living at this address. The list includes the first and last names of
	 * age and a list of other household members.
	 */

	@GetMapping("/childAlert")
	public ResponseEntity<Object> getChildrenLivingAtThisAddress(@RequestParam(value = "address") String address) throws IOException {
		try {
			logger.debug("Start call to retrieve resident list for address: {}", address);
			Object result = fireStationService.getChildrenLivingAtThisAddress(address);
			logger.info("Successful response for the address: {}", address);
			return ResponseEntity.ok(result); 
		} catch (IOException e) {
			logger.error("Error retrieving resident list for address: {}", address, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}	    	
	}

	/*
	 * This endpoint returns a list of telephone numbers of residents served
	 * by the fire station.
	 */

	@GetMapping("/phoneAlert")
	public ResponseEntity<Object> getTelephoneNumbersOfResidentsServedByTheFireStation(@RequestParam(value = "firestation") String station) throws IOException {
		try {
			logger.debug("Start call to retrieve phone list for station: {}", station);
			Object result = fireStationService.getTelephoneNumbersOfResidentsServedByTheFireStation(station);
			logger.info("Successful response for the station: {}", station);
			return ResponseEntity.ok(result); 
		} catch (IOException e) {
			logger.error("Error retrieving phone list for station: {}", station, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}	    	
	}

	/*
	 * This endpoint returns a list of residents living at the given address, along with the
	 * number of the fire station serving it.
	 */

	@GetMapping("/fire")
	public ResponseEntity<Object> getResidentsAndTheNumberOfTheFireStationAtTheAddress(@RequestParam(value = "address") String address) throws IOException {
		try {
			logger.debug("Start of call for list of residents and fire station number for address: {}", address);
			Object result = fireStationService.getResidentsAndTheNumberOfTheFireStationAtTheAddress(address);
			logger.info("Successful response for the address: {}", address);
			return ResponseEntity.ok(result); 
		} catch (IOException e) {
			logger.error("Error retrieving list of inhabitants and fire station number for address: {}", address, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}	    	
	}


	/*
	 * This endpoint returns a list of all households served by the fire station.
	 */

	@GetMapping("/flood/stations")
	public ResponseEntity<Object> getAllHomesServedByStation(@RequestParam(value = "stations") List<String> station) throws IOException {
		try {
			logger.debug("Start the call to retrieve the list of all households served by the fire station for the stations: {}", station);
			Object result = fireStationService.getAllHomesServedByStation(station);
			logger.info("Successful response for the stations: {}", station);
			return ResponseEntity.ok(result); 
		} catch (IOException e) {
			logger.error("Error when retrieving the list of all households served by the fire station: {}", station, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}	    	
	}

	/*
	 * This endpoint returns a list of all Fire stations.
	 */

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

	/*
	 * This endpoint is used to add a Fire station.
	 */

	@PostMapping("/firestations")
	public ResponseEntity<String> addFireStation(@RequestBody FireStationDTO fireStationDTO) throws IOException {
		try {
			logger.debug("Start of fire station addition: {}", fireStationDTO);
			JsonNode root = objectMapper.readTree(new File("data.json"));
			JsonNode firestationsNode = root.get("firestations");
			for (JsonNode firestation : firestationsNode) {
				boolean addressFireStation = firestation.get("address").asText().equals(fireStationDTO.getAddress());
				boolean stationFireStation = firestation.get("station").asText().equals(fireStationDTO.getStation());
				if (addressFireStation && stationFireStation) {
					logger.error("Error while adding fire station: This fire station already exists");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This fire station already exists.");
				}
				if (fireStationDTO.getAddress() == null || fireStationDTO.getStation() == null) {
					logger.error("Error while adding fire stations: address = null or station = null");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("address = null or station = null.");
				}
			}
			fireStationService.addFireStation(fireStationDTO);
			logger.info("Fire station successfully added: {}", fireStationDTO);
			return ResponseEntity.status(HttpStatus.CREATED).body("Fire station successfully added");  
		} catch (IOException e) {
			logger.error("Error adding fire station: {}", fireStationDTO, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	/*
	 * This endpoint is used to update a Fire station.
	 */

	@PutMapping("/firestations/{address}")
	public ResponseEntity<String> updateFireStation(@PathVariable String address, @RequestBody FireStationDTO fireStationDTO) throws IOException {
		logger.debug("Start of fire station update at address: {}", address);
		try {
			String errorMessage = null;
			JsonNode root = objectMapper.readTree(new File("data.json"));
			JsonNode fireStationNode = root.get("firestations");
			for (JsonNode fireStation : fireStationNode) {            	
				boolean addressVariable = fireStation.get("address").asText().equals(address);        
				boolean stationFireStation = fireStation.get("station").asText().equals(fireStationDTO.getStation());          	
				if (!addressVariable) {
					errorMessage = "This address not exists.";			
				}
				else if (stationFireStation) {
					logger.error("Error while update fire station: No modifications detected.");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No modifications detected"); 
				}
				else if (!stationFireStation) {
					fireStationService.updateFireStation(address, fireStationDTO);
					logger.info("Fire station successfully updated at: {}", address);
					return ResponseEntity.status(HttpStatus.OK).body("Fire station successfully updated");  
				} 	            
			}

			logger.error("Error while update fire station: This address not exists.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);      
		} catch (IOException e) {
			logger.error("Error updating fire station at address: {}", address, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}

	/*
	 *  This endpoint can be used to delete a Fire station.
	 */

	@DeleteMapping("/firestations/{address}")
	public ResponseEntity<String> deleteFireStation(@PathVariable String address) throws IOException {
		logger.debug("Start of removal of fire station at address: {}", address);
		try {
			String errorMessage = null;
			JsonNode root = objectMapper.readTree(new File("data.json"));
			JsonNode fireStationNode = root.get("firestations");
			for (JsonNode fireStation : fireStationNode) {
				boolean addressVariable = fireStation.get("address").asText().equals(address); 			
				if (addressVariable) {
					fireStationService.deleteFireStation(address);
					logger.info("Fire station successfully removed at address: {}", address);  				
					return ResponseEntity.status(HttpStatus.OK).body("Fire station successfully removed"); 
				} 
				else if (!addressVariable) {
					errorMessage = "This fire station not exists.";			
				}	            
			}
			logger.error("Error while delete fire station: This fire station not exists.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);


		} catch (IOException e) {
			logger.error("Error deleting fire station at address: {}", address, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
}
