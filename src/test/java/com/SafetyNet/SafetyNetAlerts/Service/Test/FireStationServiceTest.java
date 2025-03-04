package com.SafetyNet.SafetyNetAlerts.Service.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.SafetyNet.SafetyNetAlerts.dto.FireStationDTO;
import com.SafetyNet.SafetyNetAlerts.model.FireStations;
import com.SafetyNet.SafetyNetAlerts.repository.FireStationRepository;
import com.SafetyNet.SafetyNetAlerts.repository.InformationRepository;
import com.SafetyNet.SafetyNetAlerts.service.FireStationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class FireStationServiceTest {
	@Mock
    private FireStationRepository fireStationRepository;
    @Mock
    private InformationRepository informationRepository;
    
    @InjectMocks
    private FireStationService fireStationService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllFirestations() throws IOException {
       
        when(informationRepository.readFirestations()).thenReturn(Arrays.asList(new FireStations("123 Main St", "1")));
        
      
        Object result = fireStationService.getAllFirestations();
        
       
        assertNotNull(result);
        verify(informationRepository).readFirestations();
    }
    
    @Test
    public void testAddFireStation() throws IOException {
       
        FireStationDTO fireStationDTO = new FireStationDTO("123 Main St", "1");
        FireStations fireStation = new FireStations(fireStationDTO.getAddress(), fireStationDTO.getStation());
        
       
        fireStationRepository.addFirestations(fireStation);
        fireStationService.addFireStation(fireStationDTO);
        
       
        verify(fireStationRepository).addFirestations(fireStation);
    }

    @Test
    public void testUpdateFireStation() throws IOException {
       
        FireStationDTO fireStationDTO = new FireStationDTO("123 Main St", "2");
        
      
        fireStationService.updateFireStation("123 Main St", fireStationDTO);
        
       
        verify(fireStationRepository).updateFirestations("123 Main St", fireStationDTO);
    }

    @Test
    public void testDeleteFireStation() throws IOException {
       
        String address = "123 Main St";
        
      
        fireStationService.deleteFireStation(address);
        
      
        verify(fireStationRepository).deleteFirestations(address);
    }

    @Test
    public void testGetAllHomesServedByStation() throws IOException {
       
        List<String> stations = Arrays.asList("1", "2");
        
      
        fireStationService.getAllHomesServedByStation(stations);
        
       
        verify(fireStationRepository).getAllHomesServedByStation(stations);
    }

    @Test
    public void testGetResidentsAndTheNumberOfTheFireStationAtTheAddress() throws IOException {
        
        String address = "123 Main St";
        
        
        fireStationService.getResidentsAndTheNumberOfTheFireStationAtTheAddress(address);
        
       
        verify(fireStationRepository).getResidentsAndTheNumberOfTheFireStationAtTheAddress(address);
    }

    @Test
    public void testGetTelephoneNumbersOfResidentsServedByTheFireStation() throws IOException {
       
        String station = "1";
        
       
        fireStationService.getTelephoneNumbersOfResidentsServedByTheFireStation(station);
        
      
        verify(fireStationRepository).getTelephoneNumbersOfResidentsServedByTheFireStation(station);
    }

    @Test
    public void testGetChildrenLivingAtThisAddress() throws IOException {
      
        String address = "123 Main St";
        
        
        fireStationService.getChildrenLivingAtThisAddress(address);
        
        
        verify(fireStationRepository).getChildrenLivingAtThisAddress(address);
    }

    @Test
    public void testGetPersonsCoveredByTheFireStation() throws IOException {
       
        String station = "1";
        
        
        fireStationService.getPersonsCoveredByTheFireStation(station);
        
        
        verify(fireStationRepository).getPersonsCoveredByTheFireStation(station);
    }
}
