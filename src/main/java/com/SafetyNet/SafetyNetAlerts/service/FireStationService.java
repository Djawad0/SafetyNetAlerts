package com.SafetyNet.SafetyNetAlerts.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SafetyNet.SafetyNetAlerts.dto.FireStationDTO;
import com.SafetyNet.SafetyNetAlerts.model.FireStations;
import com.SafetyNet.SafetyNetAlerts.repository.FireStationRepository;
import com.SafetyNet.SafetyNetAlerts.repository.InformationRepository;

@Service
public class FireStationService {
	
	 @Autowired
	    private FireStationRepository fireStationRepository;
	 @Autowired
	    private InformationRepository informationRepository;
	 	
	 	public Object getAllFirestations() throws IOException {
         return informationRepository.readFirestations();
	 	}

	    public void addFireStation(FireStationDTO fireStationDTO) throws IOException {
	        FireStations fireStation = new FireStations(fireStationDTO.getAddress(), fireStationDTO.getStation());
	        fireStationRepository.addFirestations(fireStation);
	    }

	    public void updateFireStation(String address, FireStationDTO fireStationDTO) throws IOException {
	    	fireStationRepository.updateFirestations(address, fireStationDTO);
	    }

	    public void deleteFireStation(String address) throws IOException {
	    	fireStationRepository.deleteFirestations(address);
	    }
	    
	    public Object test7(List<String> station) throws IOException {
			return fireStationRepository.test7(station);
	    	
	    }
	    
	    public Object test8(String address) throws IOException {
			return fireStationRepository.test8(address);
	    	
	    }
	    
	    public Object test9(String station) throws IOException {
			return fireStationRepository.test9(station);
	    	
	    }
	    
	    public Object test10(String address) throws IOException {
			return fireStationRepository.test10(address);
	    	
	    }
	    
	    public Object test11(String station) throws IOException {
			return fireStationRepository.test11(station);
	    	
	    }
}
