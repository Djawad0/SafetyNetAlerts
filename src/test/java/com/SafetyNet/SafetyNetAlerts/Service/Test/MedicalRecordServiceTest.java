package com.SafetyNet.SafetyNetAlerts.Service.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


import java.io.IOException;
import java.util.Arrays;

import com.SafetyNet.SafetyNetAlerts.dto.MedicalRecordDTO;
import com.SafetyNet.SafetyNetAlerts.model.MedicalRecords;
import com.SafetyNet.SafetyNetAlerts.repository.InformationRepository;
import com.SafetyNet.SafetyNetAlerts.repository.MedicalRecordsRepository;
import com.SafetyNet.SafetyNetAlerts.service.MedicalRecordService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class MedicalRecordServiceTest {
	
	 @Mock
	    private InformationRepository informationRepository;
	    @Mock
	    private MedicalRecordsRepository medicalRecordsRepository;
	    
	    @InjectMocks
	    private MedicalRecordService medicalRecordService;
	    
	    @BeforeEach
	    public void setUp() {
	        MockitoAnnotations.initMocks(this);
	    }

	    @Test
	    public void testGetAllMedicalRecords() throws IOException {
	        
	        when(informationRepository.readMedicalRecords()).thenReturn(Arrays.asList(new MedicalRecords("John", "Doe", "01/01/1990", Arrays.asList("medication1"), Arrays.asList("allergy1"))));
	        
	       
	        Object result = medicalRecordService.getAllMedicalRecords();
	        
	        
	        assertNotNull(result);
	        verify(informationRepository).readMedicalRecords();
	    }
	    
	    @Test
	    public void testAddMedicalRecord() throws IOException {
	        
	        MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO("John", "Doe", "01/01/1990", Arrays.asList("medication1"), Arrays.asList("allergy1"));
	        MedicalRecords record = new MedicalRecords(medicalRecordDTO.getFirstName(), medicalRecordDTO.getLastName(),
	                medicalRecordDTO.getBirthdate(), medicalRecordDTO.getMedications(), medicalRecordDTO.getAllergies());
	        
	       
	        medicalRecordsRepository.addMedicalRecords(record);
	        medicalRecordService.addMedicalRecord(medicalRecordDTO);
	        
	        
	        verify(medicalRecordsRepository).addMedicalRecords(record);
	    }

	    @Test
	    public void testUpdateMedicalRecord() throws IOException {
	        
	        MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO("John", "Doe", "01/01/1990", Arrays.asList("medication1"), Arrays.asList("allergy1"));
	        
	       
	        medicalRecordService.updateMedicalRecord("John", "Doe", medicalRecordDTO);
	        
	        
	        verify(medicalRecordsRepository).updateMedicalrecords("John", "Doe", medicalRecordDTO);
	    }

	    @Test
	    public void testDeleteMedicalRecord() throws IOException {
	        
	        String firstName = "John";
	        String lastName = "Doe";
	        
	      
	        medicalRecordService.deleteMedicalRecord(firstName, lastName);
	        
	        
	        verify(medicalRecordsRepository).deleteMedicalrecords(firstName, lastName);
	    }
}
