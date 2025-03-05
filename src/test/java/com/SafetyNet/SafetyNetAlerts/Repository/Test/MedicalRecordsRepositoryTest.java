package com.SafetyNet.SafetyNetAlerts.Repository.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import com.SafetyNet.SafetyNetAlerts.dto.MedicalRecordDTO;
import com.SafetyNet.SafetyNetAlerts.model.MedicalRecords;
import com.SafetyNet.SafetyNetAlerts.repository.InformationRepository;
import com.SafetyNet.SafetyNetAlerts.repository.MedicalRecordsRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@SpringBootTest
class MedicalRecordRepositoryTest {

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private InformationRepository informationRepository;

	@Mock
	private MedicalRecordDTO medicalRecordDTO;

	@InjectMocks
	private MedicalRecordsRepository medicalRecordRepository;

	private ObjectNode root;
	private ArrayNode medicalRecordsArray;
	private JsonNode mockRecordNode;
	private ObjectNode recordNode;


	@BeforeEach
	void setUp() {
		root = mock(ObjectNode.class);
		medicalRecordsArray = mock(ArrayNode.class);
		mockRecordNode = mock(JsonNode.class);
		recordNode = mock(ObjectNode.class);
	}

	//  Test: Successfully add a new medical record
	@Test
	void testAddMedicalRecord_Success() throws IOException {
		MedicalRecords medicalRecord = new MedicalRecords("John", "Doe", "01/01/2000", List.of("med1"), List.of("allergy1"));

		when(informationRepository.readFile()).thenReturn(root);


		when(root.get("medicalrecords")).thenReturn(medicalRecordsArray);  


		when(root.has("medicalrecords")).thenReturn(true);
		when(medicalRecordsArray.isArray()).thenReturn(true);  

		when(objectMapper.valueToTree(medicalRecord)).thenReturn(mockRecordNode);


		when(medicalRecordsArray.add(mockRecordNode)).thenReturn(medicalRecordsArray);


		when(root.set("medicalrecords", medicalRecordsArray)).thenReturn(root); 


		medicalRecordRepository.addMedicalRecords(medicalRecord);


		verify(medicalRecordsArray, times(1)).add(mockRecordNode);  
		verify(informationRepository, times(1)).writeFile(root);  
		verify(root, times(1)).set("medicalrecords", medicalRecordsArray); 
	}


	//  Test: Adding a medical record fails due to missing "medicalrecords" node
	@Test
	void testAddMedicalRecord_Failure_NoMedicalRecordsNode() throws IOException {
		MedicalRecords medicalRecord = new MedicalRecords("John", "Doe", "01/01/2000", List.of("med1"), List.of("allergy1"));

		when(informationRepository.readFile()).thenReturn(root);
		when(root.has("medicalrecords")).thenReturn(false);

		assertThrows(IOException.class, () -> medicalRecordRepository.addMedicalRecords(medicalRecord));
	}


	@Test
	public void testUpdateMedicalRecord() throws IOException {
		ObjectMapper realObjectMapper = new ObjectMapper(); 
		String firstName = "John";
		String lastName = "Doe";


		String json = "{ \"medicalrecords\": [{\"firstName\": \"John\",\"lastName\": \"Doe\", \"birthdate\": \"03/06/1984\", "
				+ "\"medications\": [\"aznol:350mg\", \"hydrapermazol:100mg\"], \"allergies\": [\"nillacilan\"] }] }";

		JsonNode root = realObjectMapper.readTree(json);


		when(informationRepository.readFile()).thenReturn(root);

		JsonNode medicalRecordsNode = root.get("medicalrecords");
		if (medicalRecordsNode.isArray()) {
			for (JsonNode record : medicalRecordsNode) {
				if (record.get("firstName").asText().equals(firstName) && record.get("lastName").asText().equals(lastName)) {
					((ObjectNode) record).put("birthdate", medicalRecordDTO.getBirthdate());


					ArrayNode medicationsArray = realObjectMapper.createArrayNode();
					for (String medication : medicalRecordDTO.getMedications()) {
						medicationsArray.add(medication);
					}
					((ObjectNode) record).set("medications", medicationsArray);

					ArrayNode allergiesArray = realObjectMapper.createArrayNode();
					for (String allergy : medicalRecordDTO.getAllergies()) {
						allergiesArray.add(allergy);
					}
					((ObjectNode) record).set("allergies", allergiesArray);
				}
			}
		}


		doNothing().when(informationRepository).writeFile(any(JsonNode.class));

		medicalRecordRepository.updateMedicalrecords(firstName, lastName, medicalRecordDTO);


		verify(informationRepository, times(1)).readFile();	  
		verify(informationRepository, times(1)).writeFile(root);
	}

	//  Test: Update medical record fails due to missing "medicalrecords" node
	@Test
	void testUpdateMedicalRecord_Failure_NoMedicalRecordsNode() throws IOException {
		String firstName = "John";
		String lastName = "Doe";
		MedicalRecordDTO updatedRecord = new MedicalRecordDTO("John", "Doe", "02/02/2001", List.of("med2"), List.of("allergy2"));

		when(informationRepository.readFile()).thenReturn(root);
		when(root.get("medicalrecords")).thenReturn(null);

		assertThrows(NullPointerException.class, () -> medicalRecordRepository.updateMedicalrecords(firstName, lastName, updatedRecord));
	}

	//  Test: Successfully delete an existing medical record
	@Test
	void testDeleteMedicalRecord_Success() throws IOException {
		String firstName = "John";
		String lastName = "Doe";

		when(informationRepository.readFile()).thenReturn(root);
		when(root.get("medicalrecords")).thenReturn(medicalRecordsArray);

		when(recordNode.get("firstName")).thenReturn(mock(JsonNode.class));
		when(recordNode.get("lastName")).thenReturn(mock(JsonNode.class));
		when(recordNode.get("firstName").asText()).thenReturn(firstName);
		when(recordNode.get("lastName").asText()).thenReturn(lastName);
		List<JsonNode> recordsList = new ArrayList<>();
		recordsList.add(recordNode);
		when(medicalRecordsArray.iterator()).thenReturn(recordsList.iterator());

		medicalRecordRepository.deleteMedicalrecords(firstName, lastName);

		verify(informationRepository, times(1)).writeFile(root);
	}

	//  Test: Deleting a medical record fails due to missing "medicalrecords" node
	@Test
	void testDeleteMedicalRecord_Failure_NoMedicalRecordsNode() throws IOException {
		String firstName = "John";
		String lastName = "Doe";

		when(informationRepository.readFile()).thenReturn(root);
		when(root.get("medicalrecords")).thenReturn(null);

		assertThrows(NullPointerException.class, () -> medicalRecordRepository.deleteMedicalrecords(firstName, lastName));
	}
}