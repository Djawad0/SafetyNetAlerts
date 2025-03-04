package com.SafetyNet.SafetyNetAlerts.controller.test;

import com.SafetyNet.SafetyNetAlerts.controller.FireStationController;
import com.SafetyNet.SafetyNetAlerts.dto.FireStationDTO;
import com.SafetyNet.SafetyNetAlerts.service.FireStationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class FireStationControllerTest {
	 @Mock
	    private FireStationService fireStationService;

	    @Mock
	    private ObjectMapper objectMapper;
	    private MockMvc mockMvc;

	    @InjectMocks
	    private FireStationController fireStationController;

	    @BeforeEach
	    public void setUp() throws IOException {
	    	MockitoAnnotations.openMocks(this);

	        mockMvc = MockMvcBuilders.standaloneSetup(fireStationController).build();
	        String json = "{ \"firestations\": [{ \"address\": \"123 Main St\", \"station\": \"1\" }] }";
	        JsonNode mockNode = new ObjectMapper().readTree(json);

	        when(objectMapper.readTree(any(File.class))).thenReturn(mockNode);
	    }

	    @Test
	    public void testGetAllFirestations() throws IOException {
	        
	        when(fireStationService.getAllFirestations()).thenReturn(Arrays.asList("FireStation1", "FireStation2"));

	       
	        ResponseEntity<Object> response = fireStationController.getAllFirestations();

	      
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertNotNull(response.getBody());
	        verify(fireStationService, times(1)).getAllFirestations();
	    }

	    @Test
	    public void testAddFireStation() throws IOException {
	    	
	        
	        FireStationDTO fireStationDTO = new FireStationDTO("123 Main St", "11");
	        doNothing().when(fireStationService).addFireStation(fireStationDTO);

	        
	        ResponseEntity<String> response = fireStationController.addFireStation(fireStationDTO);

	        
	        assertEquals(HttpStatus.CREATED, response.getStatusCode()); // Ensure expected response
	        assertEquals("Fire station successfully added", response.getBody());
	        verify(fireStationService, times(1)).addFireStation(any(FireStationDTO.class));
	    }

	    @Test
	    public void testUpdateFireStation() throws IOException {
	        
	        String address = "123 Main St";
	        FireStationDTO fireStationDTO = new FireStationDTO(address, "2");
	        doNothing().when(fireStationService).updateFireStation(address, fireStationDTO);

	      
	        ResponseEntity<String> response = fireStationController.updateFireStation(address, fireStationDTO);

	     
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals("Fire station successfully updated", response.getBody());
	        verify(fireStationService, times(1)).updateFireStation(address, fireStationDTO);
	    }

	    @Test
	    public void testDeleteFireStation() throws IOException {
	       
	        String address = "123 Main St";
	        doNothing().when(fireStationService).deleteFireStation(address);

	       
	        ResponseEntity<String> response = fireStationController.deleteFireStation(address);

	     
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals("Fire station successfully removed", response.getBody());
	        verify(fireStationService, times(1)).deleteFireStation(address);
	    }

	    @Test
	    public void testGetTelephoneNumbersOfResidentsServedByTheFireStation() throws IOException {
	       
	        String station = "1";
	        when(fireStationService.getTelephoneNumbersOfResidentsServedByTheFireStation(station)).thenReturn(Arrays.asList("123-456-7890", "987-654-3210"));

	       
	        ResponseEntity<Object> response = fireStationController.getTelephoneNumbersOfResidentsServedByTheFireStation(station);

	       
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertNotNull(response.getBody());
	        verify(fireStationService, times(1)).getTelephoneNumbersOfResidentsServedByTheFireStation(station);
	    }

	    @Test
	    public void testErrorOnGetFirestations() throws IOException {
	      
	        when(fireStationService.getAllFirestations()).thenThrow(new IOException("File not found"));

	     
	        ResponseEntity<Object> response = fireStationController.getAllFirestations();

	      
	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	        assertEquals("Internal server error", response.getBody());
	    }

	    
	    @Test
	    public void testGetPersonsCoveredByTheFireStation() throws IOException {
	        String station = "1";
	        when(fireStationService.getPersonsCoveredByTheFireStation(station)).thenReturn(Arrays.asList("Person1", "Person2"));

	        ResponseEntity<Object> response = fireStationController.getPersonsCoveredByTheFireStation(station);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertNotNull(response.getBody());
	        verify(fireStationService, times(1)).getPersonsCoveredByTheFireStation(station);
	    }

	    @Test
	    public void testGetChildrenLivingAtThisAddress() throws IOException {
	        String address = "123 Main St";
	        when(fireStationService.getChildrenLivingAtThisAddress(address)).thenReturn(Arrays.asList("Child1", "Child2"));

	        ResponseEntity<Object> response = fireStationController.getChildrenLivingAtThisAddress(address);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertNotNull(response.getBody());
	        verify(fireStationService, times(1)).getChildrenLivingAtThisAddress(address);
	    }

	    @Test
	    public void testGetResidentsAndTheNumberOfTheFireStationAtTheAddress() throws IOException {
	        String address = "123 Main St";
	        when(fireStationService.getResidentsAndTheNumberOfTheFireStationAtTheAddress(address)).thenReturn(Arrays.asList("Resident1", "Resident2"));

	        ResponseEntity<Object> response = fireStationController.getResidentsAndTheNumberOfTheFireStationAtTheAddress(address);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertNotNull(response.getBody());
	        verify(fireStationService, times(1)).getResidentsAndTheNumberOfTheFireStationAtTheAddress(address);
	    }

	    @Test
	    public void testGetAllHomesServedByStation() throws IOException {
	        List<String> stations = Arrays.asList("1", "2");
	        when(fireStationService.getAllHomesServedByStation(stations)).thenReturn(Arrays.asList("Household1", "Household2"));

	        ResponseEntity<Object> response = fireStationController.getAllHomesServedByStation(stations);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertNotNull(response.getBody());
	        verify(fireStationService, times(1)).getAllHomesServedByStation(stations);
	    }

	    @Test
	    public void getResidentsAndTheNumberOfTheFireStationAtTheAddress_InternalServerError() throws Exception {	
	    	 when(fireStationService.getResidentsAndTheNumberOfTheFireStationAtTheAddress(null)).thenThrow(new IOException());

		        ResponseEntity<Object> response = fireStationController.getResidentsAndTheNumberOfTheFireStationAtTheAddress(null);

		        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		        assertEquals("Internal server error",response.getBody());
		        verify(fireStationService, times(1)).getResidentsAndTheNumberOfTheFireStationAtTheAddress(null);
	    }
	    
	    @Test
	    public void getAllHomesServedByStation_InternalServerError() throws Exception {	
	    	 when(fireStationService.getAllHomesServedByStation(null)).thenThrow(new IOException());

		        ResponseEntity<Object> response = fireStationController.getAllHomesServedByStation(null);

		        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		        assertEquals("Internal server error",response.getBody());
		        verify(fireStationService, times(1)).getAllHomesServedByStation(null);
	    }
	    
	    @Test
	    public void testGetChildrenLivingAtThisAddress_InternalServerError() throws Exception {	
	    	 when(fireStationService.getChildrenLivingAtThisAddress(null)).thenThrow(new IOException());

		        ResponseEntity<Object> response = fireStationController.getChildrenLivingAtThisAddress(null);

		        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		        assertEquals("Internal server error",response.getBody());
		        verify(fireStationService, times(1)).getChildrenLivingAtThisAddress(null);
	    }

	    @Test
	    public void testGetPersonsCoveredByTheFireStation_InternalServerError() throws Exception {	
	    	 when(fireStationService.getPersonsCoveredByTheFireStation(null)).thenThrow(new IOException());

		        ResponseEntity<Object> response = fireStationController.getPersonsCoveredByTheFireStation(null);

		        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		        assertEquals("Internal server error",response.getBody());
		        verify(fireStationService, times(1)).getPersonsCoveredByTheFireStation(null);
	    }
	    
	    @Test
	    public void getTelephoneNumbersOfResidentsServedByTheFireStation_InternalServerError() throws Exception {	
	    	 when(fireStationService.getTelephoneNumbersOfResidentsServedByTheFireStation(null)).thenThrow(new IOException());

		        ResponseEntity<Object> response = fireStationController.getTelephoneNumbersOfResidentsServedByTheFireStation(null);

		        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		        assertEquals("Internal server error",response.getBody());
		        verify(fireStationService, times(1)).getTelephoneNumbersOfResidentsServedByTheFireStation(null);
	    }

	    
}
