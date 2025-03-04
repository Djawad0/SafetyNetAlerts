package com.SafetyNet.SafetyNetAlerts.controller.test;

import com.SafetyNet.SafetyNetAlerts.controller.MedicalRecordController;
import com.SafetyNet.SafetyNetAlerts.dto.MedicalRecordDTO;
import com.SafetyNet.SafetyNetAlerts.service.MedicalRecordService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MedicalRecordControllerTest {
	
	
	 @Mock
	    private MedicalRecordService medicalRecordService;

	    @Mock
	    private ObjectMapper objectMapper;

	    @InjectMocks
	    private MedicalRecordController medicalRecordController;

	    private MedicalRecordDTO medicalRecordDTO;
	    private JsonNode mockRoot;
	    private JsonNode root;
	    private JsonNode medicalRecordNode;

	    @BeforeEach
	    public void setUp() throws IOException {
	        MockitoAnnotations.openMocks(this);
	        medicalRecordDTO = new MedicalRecordDTO("John", "Doe", "01-01-1990", List.of("[]"), List.of("[]"));
	        root = mock(JsonNode.class);
	        medicalRecordNode = mock(JsonNode.class);
	        // Création d'un vrai JsonNode pour éviter les problèmes de null
	        ObjectMapper realObjectMapper = new ObjectMapper();
	        String json = "{ \"medicalrecords\": [] }";
	        mockRoot = realObjectMapper.readTree(json);
	        when(objectMapper.readTree(any(File.class))).thenReturn(mockRoot);
	    }

	    @Test
	    public void testGetAllMedicalRecords() throws IOException {
	        when(medicalRecordService.getAllMedicalRecords()).thenReturn("mockedData");

	        ResponseEntity<Object> response = medicalRecordController.getAllMedicalRecords();

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals("mockedData", response.getBody());
	        verify(medicalRecordService, times(1)).getAllMedicalRecords();
	    }

	    @Test
	    public void testAddMedicalRecord() throws IOException {
	        doNothing().when(medicalRecordService).addMedicalRecord(any(MedicalRecordDTO.class));

	        ResponseEntity<String> response = medicalRecordController.addMedicalRecord(medicalRecordDTO);

	        assertEquals(HttpStatus.CREATED, response.getStatusCode());
	        assertEquals("Medical record created successfully", response.getBody());
	        verify(medicalRecordService, times(1)).addMedicalRecord(any(MedicalRecordDTO.class));
	    }

	    @Test
	    public void testAddMedicalRecord_AlreadyExists() throws IOException {
	        // Arrange
	        ObjectMapper realObjectMapper = new ObjectMapper();
	        String json = "{ \"medicalrecords\": [{\"firstName\": \"John\", \"lastName\": \"Doe\"}] }";
	        JsonNode root = realObjectMapper.readTree(json);

	        when(objectMapper.readTree(any(File.class))).thenReturn(root);

	        // Act
	        ResponseEntity<String> response = medicalRecordController.addMedicalRecord(medicalRecordDTO);

	        // Assert
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        assertEquals("This medical record already exists.", response.getBody());
	    }

	    @Test
	    public void testUpdateMedicalRecord() throws IOException {
	       
	        ObjectMapper realObjectMapper = new ObjectMapper();
	        String json = "{ \"medicalrecords\": [{\"firstName\": \"John\",\"lastName\": \"Doe\", \"birthdate\": \"03/06/1984\", \"medications\": [\"aznol:350mg\","
	        		+ " \"hydrapermazol:100mg\"], \"allergies\": [\"nillacilan\"] }] }";
	        JsonNode root = realObjectMapper.readTree(json);

	        when(objectMapper.readTree(any(File.class))).thenReturn(root);
	        doNothing().when(medicalRecordService).updateMedicalRecord(anyString(), anyString(), any(MedicalRecordDTO.class));

	        ResponseEntity<String> response = medicalRecordController.updateMedicalRecord("John", "Doe", medicalRecordDTO);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals("Medical record updated successfully", response.getBody());
	        verify(medicalRecordService, times(1)).updateMedicalRecord("John", "Doe", medicalRecordDTO);
	    }

	    @Test
	    public void testUpdateMedicalRecord_NotFound() throws IOException {
	        // Simulating no existing record
	        ObjectMapper realObjectMapper = new ObjectMapper();
	        String json = "{ \"medicalrecords\": [{\"firstName\": \"John\",\"lastName\": \"Doe\", \"birthdate\": \"03/06/1984\", \"medications\": [\"aznol:350mg\","
	        		+ " \"hydrapermazol:100mg\"], \"allergies\": [\"nillacilan\"] }] }";
	        JsonNode root = realObjectMapper.readTree(json);

	        when(objectMapper.readTree(any(File.class))).thenReturn(root);

	        ResponseEntity<String> response = medicalRecordController.updateMedicalRecord("Jane", "Doe", medicalRecordDTO);

	        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	       // assertEquals("This person not exists.", response.getBody());
	        verify(medicalRecordService, never()).updateMedicalRecord(anyString(), anyString(), any(MedicalRecordDTO.class));
	    }

	    @Test
	    public void testDeleteMedicalRecord() throws IOException {
	        ObjectMapper realObjectMapper = new ObjectMapper();
	        String json = "{ \"medicalrecords\": [{ \"firstName\": \"John\", \"lastName\": \"Doe\" }] }";
	        JsonNode root = realObjectMapper.readTree(json);

	        when(objectMapper.readTree(any(File.class))).thenReturn(root);
	        doNothing().when(medicalRecordService).deleteMedicalRecord("John", "Doe");

	        ResponseEntity<String> response = medicalRecordController.deleteMedicalRecord("John", "Doe");

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals("Medical record deleted successfully", response.getBody());
	        verify(medicalRecordService, times(1)).deleteMedicalRecord("John", "Doe");
	    }

	   
	    @Test
	    public void testAddMedicalRecord_IOException() throws IOException {
	        doThrow(new IOException("Database unavailable"))
	            .when(medicalRecordService).addMedicalRecord(any(MedicalRecordDTO.class));

	        ResponseEntity<String> response = medicalRecordController.addMedicalRecord(medicalRecordDTO);

	        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	        assertEquals("Error adding medical record", response.getBody());
	        verify(medicalRecordService, times(1)).addMedicalRecord(any(MedicalRecordDTO.class));
	    }
}
