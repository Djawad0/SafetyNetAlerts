package com.SafetyNet.SafetyNetAlerts.Repository.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import com.SafetyNet.SafetyNetAlerts.dto.PersonDTO;
import com.SafetyNet.SafetyNetAlerts.model.Persons;
import com.SafetyNet.SafetyNetAlerts.repository.InformationRepository;
import com.SafetyNet.SafetyNetAlerts.repository.PersonsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SpringBootTest
class PersonsRepositoryTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private InformationRepository informationRepository;

    @InjectMocks
    private PersonsRepository personsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testAddPersons_Success() throws IOException {
       
        Persons persons = new Persons("John", "Doe", "1234 Elm St", "City", "12345", "555-1234", "john.doe@example.com");

        ObjectNode root = mock(ObjectNode.class);
        ArrayNode personsArray = mock(ArrayNode.class);
        ObjectNode mockPersonNode = mock(ObjectNode.class);

        when(informationRepository.readFile()).thenReturn(root);
        when(root.has("persons")).thenReturn(true);
        when(root.get("persons")).thenReturn(personsArray); 
        when(personsArray.isArray()).thenReturn(true); 
        when(objectMapper.valueToTree(persons)).thenReturn(mockPersonNode); 

     
        personsRepository.addPersons(persons);

       
        verify(objectMapper, times(1)).valueToTree(persons); 
        verify(personsArray, times(1)).add(mockPersonNode); 
        verify(informationRepository, times(1)).writeFile(root);
    }
    @Test
    void testAddPersons_Failure() {
      
        when(informationRepository.readFile()).thenThrow(new RuntimeException("Error reading file"));

       
        assertThrows(RuntimeException.class, () -> personsRepository.addPersons(
            new Persons("John", "Doe", "1234 Elm St", "City", "12345", "555-1234", "john.doe@example.com")
        ));
    }

    @Test
    void testUpdatePersons_Success() throws IOException {
     
        PersonDTO personDTO = new PersonDTO("John", "Doe", "1234 Elm St", "City", "12345", "555-1234", "john.doe@example.com");

        ObjectNode record = mock(ObjectNode.class);
        JsonNode root = mock(JsonNode.class);
        JsonNode personsNode = mock(JsonNode.class);
        JsonNode firstNameNode = mock(JsonNode.class);
        JsonNode lastNameNode = mock(JsonNode.class);

        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("persons")).thenReturn(personsNode);

     
        List<JsonNode> recordsList = new ArrayList<>();
        recordsList.add(record);
        when(personsNode.iterator()).thenReturn(recordsList.iterator());

     
        when(record.get("firstName")).thenReturn(firstNameNode);
        when(record.get("lastName")).thenReturn(lastNameNode);
        when(firstNameNode.asText()).thenReturn("John");
        when(lastNameNode.asText()).thenReturn("Doe");

      
        personsRepository.updatePersons("John", "Doe", personDTO);

      
        verify(record, times(1)).put("address", personDTO.getAddress());
        verify(record, times(1)).put("city", personDTO.getCity());
        verify(record, times(1)).put("zip", personDTO.getZip());
        verify(record, times(1)).put("phone", personDTO.getPhone());
        verify(record, times(1)).put("email", personDTO.getEmail());
        verify(informationRepository, times(1)).writeFile(root);
    }
    
    @Test
    void testUpdatePersons_Failure() {
       
        when(informationRepository.readFile()).thenThrow(new RuntimeException("Error reading file"));

       
        assertThrows(RuntimeException.class, () -> personsRepository.updatePersons("John", "Doe", 
           new PersonDTO("John", "Doe", "1234 Elm St", "City", "12345", "555-1234", "john.doe@example.com")));
    }

    @Test
    void testDeletePersons_Success() throws IOException {
      
        JsonNode root = mock(JsonNode.class);
        JsonNode personsNode = mock(JsonNode.class);
        ObjectNode record = mock(ObjectNode.class);
        JsonNode firstNameNode = mock(JsonNode.class);
        JsonNode lastNameNode = mock(JsonNode.class);

        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("persons")).thenReturn(personsNode);
        List<JsonNode> jsonNodeList = new ArrayList<>();
        jsonNodeList.add(record);
        when(personsNode.iterator()).thenReturn(jsonNodeList.iterator());
    

        when(record.get("firstName")).thenReturn(firstNameNode);
        when(record.get("lastName")).thenReturn(lastNameNode);
        when(firstNameNode.asText()).thenReturn("John");
        when(lastNameNode.asText()).thenReturn("Doe");

       
        personsRepository.deletePersons("John", "Doe");

       
        verify(informationRepository, times(1)).writeFile(root);
    }

    @Test
    void testDeletePersons_Failure() {
      
        when(informationRepository.readFile()).thenThrow(new RuntimeException("Error reading file"));

      
        assertThrows(RuntimeException.class, () -> personsRepository.deletePersons("John", "Doe"));
    }

    @Test
    void testGetEmailOfAllCityResidents_Success() throws IOException {
        // Arrange
        JsonNode root = mock(JsonNode.class);
        JsonNode personsNode = mock(JsonNode.class);
        ObjectNode record = mock(ObjectNode.class);
        JsonNode cityNode = mock(JsonNode.class);
        JsonNode emailNode = mock(JsonNode.class);

        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("persons")).thenReturn(personsNode);
        List<JsonNode> jsonNodeList = new ArrayList<>();
        jsonNodeList.add(record);
        when(personsNode.iterator()).thenReturn(jsonNodeList.iterator());
    

      
        when(record.get("city")).thenReturn(cityNode);
        when(cityNode.asText()).thenReturn("City");

        when(record.get("email")).thenReturn(emailNode);

     
        Object result = personsRepository.getEmailOfAllCityResidents("City");

       
        assertNotNull(result);
    }

    @Test
    void testGetEmailOfAllCityResidents_Failure() throws IOException {
       
        when(informationRepository.readFile()).thenThrow(new RuntimeException("Error reading file"));

       
        assertThrows(RuntimeException.class, () -> personsRepository.getEmailOfAllCityResidents("City"));
    }

    @Test
    void testGetPersonInfo_Success() throws IOException {
       
        JsonNode root = mock(JsonNode.class);
        JsonNode personsNode = mock(JsonNode.class);
        JsonNode medicalRecordsNode = mock(JsonNode.class);
        ObjectNode person = mock(ObjectNode.class);
        ObjectNode record = mock(ObjectNode.class);
        JsonNode lastNameNode = mock(JsonNode.class);
        JsonNode firstNameNode = mock(JsonNode.class);
        JsonNode birthdateNode = mock(JsonNode.class);

        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("persons")).thenReturn(personsNode);
        when(root.get("medicalrecords")).thenReturn(medicalRecordsNode);
        List<JsonNode> jsonNodeList = new ArrayList<>();
        jsonNodeList.add(person);
        jsonNodeList.add(record);
        when(personsNode.iterator()).thenReturn(jsonNodeList.iterator());
   

        when(person.get("lastName")).thenReturn(lastNameNode);
        when(lastNameNode.asText()).thenReturn("Doe");

        when(record.get("lastName")).thenReturn(lastNameNode);
        when(record.get("firstName")).thenReturn(firstNameNode);
        when(firstNameNode.asText()).thenReturn("John");

        when(record.get("birthdate")).thenReturn(birthdateNode);
        when(birthdateNode.asText()).thenReturn("01/01/2000");

       
        Object result = personsRepository.getPersonInfo("Doe");

       
        assertNotNull(result);
    }

    @Test
    void testGetPersonInfo_Failure() {
      
        when(informationRepository.readFile()).thenThrow(new RuntimeException("Error reading file"));

     
        assertThrows(RuntimeException.class, () -> personsRepository.getPersonInfo("Doe"));
    }
    
    @Test
    public void testGetPersonInfo_found() throws IOException {
    	String json = "{ \"persons\": [{ \"firstName\": \"John\", \"lastName\": \"Boyd\", \"address\": \"1\", \"city\": \"1\", \"zip\": \"1\", \"phone\": \"1\", \"email\": \"1\" }] , \"medicalrecords\": [{ \"firstName\": \"John\", \"lastName\": \"Boyd\", \"birthdate\": \"01/01/2000\", \"medications\": \"1\", \"allergies\": \"1\" }] }";
        
        JsonNode mockNode = new ObjectMapper().readTree(json);
        when(informationRepository.readFile()).thenReturn(mockNode);

        Object result = personsRepository.getPersonInfo("Boyd");

        assertNotNull(result);
    	 
    }
}