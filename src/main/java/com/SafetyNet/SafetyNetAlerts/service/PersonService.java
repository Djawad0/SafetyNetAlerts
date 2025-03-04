package com.SafetyNet.SafetyNetAlerts.service;


import java.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SafetyNet.SafetyNetAlerts.dto.PersonDTO;
import com.SafetyNet.SafetyNetAlerts.model.Persons;
import com.SafetyNet.SafetyNetAlerts.repository.InformationRepository;
import com.SafetyNet.SafetyNetAlerts.repository.PersonsRepository;

@Service
public class PersonService {
	
	 @Autowired
	    private InformationRepository informationRepository; 
	    @Autowired
	    private PersonsRepository personsRepository;

	    public Object getAllPersons() throws IOException {
	    	return informationRepository.readPersons();
	    }

	    public void addPerson(PersonDTO personDTO) throws IOException {
	        Persons person = new Persons(personDTO.getFirstName(), personDTO.getLastName(),
	                personDTO.getAddress(), personDTO.getCity(), personDTO.getZip(), personDTO.getPhone(), personDTO.getEmail());
	        personsRepository.addPersons(person);
	    }

	    public void updatePersons(String firstName, String lastName, PersonDTO personDTO) throws IOException {
	    	personsRepository.updatePersons(firstName, lastName, personDTO);
	    }

	    public void deletePerson(String firstName, String lastName) throws IOException {
	    	personsRepository.deletePersons(firstName, lastName);
	    }
	    
	    public Object getEmailOfAllCityResidents(String city) throws IOException {
			return personsRepository.getEmailOfAllCityResidents(city);    	
	    }
	    
	    public Object getPersonInfo(String lastName) throws IOException {
			return personsRepository.getPersonInfo(lastName);
	    	
	    }
}
