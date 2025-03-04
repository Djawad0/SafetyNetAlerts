package com.SafetyNet.SafetyNetAlerts.Repository.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.SafetyNet.SafetyNetAlerts.dto.FireStationDTO;
import com.SafetyNet.SafetyNetAlerts.model.FireStations;
import com.SafetyNet.SafetyNetAlerts.model.MedicalRecords;
import com.SafetyNet.SafetyNetAlerts.repository.FireStationRepository;
import com.SafetyNet.SafetyNetAlerts.repository.InformationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FireStationRepositoryTest {
	@Mock
    private ObjectMapper objectMapper;

    @Mock
    private InformationRepository informationRepository;
    
    @Mock
    private JsonNode personsNode;
    
    @Mock
    private JsonNode firestationsNode;
    
    @Mock
    private JsonNode fireStation1;
    
    @Mock
    private JsonNode fireStation2;
    
    @Mock
    private JsonNode person1;
    
    @Mock
    private JsonNode person2;

    @InjectMocks
    private FireStationRepository fireStationRepository;
    
    private JsonNode rootNode;
    private JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

    private JsonNode root;
    private ArrayNode fireStationsArray;
    private ObjectNode mockStationNode;
    private ObjectNode recordNode;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        root = mock(ObjectNode.class);
        fireStationsArray = mock(ArrayNode.class);
        mockStationNode = mock(ObjectNode.class);
        recordNode = mock(ObjectNode.class);
 
    }

    // Test: Successfully add a new fire station
    @Test
    void testAddFirestations_Success() throws IOException {
        FireStations fireStations = new FireStations("1234 Elm St", "3");

        when(informationRepository.readFile()).thenReturn(root);
        when(root.has("firestations")).thenReturn(true);
        when(root.get("firestations")).thenReturn(fireStationsArray);
        when(fireStationsArray.isArray()).thenReturn(true);
        when(objectMapper.valueToTree(fireStations)).thenReturn(mockStationNode);

        fireStationRepository.addFirestations(fireStations);

        verify(fireStationsArray, times(1)).add(mockStationNode);
        verify(informationRepository, times(1)).writeFile(root);
    }

    //  Test: Adding a fire station fails due to missing "firestations" node
    @Test
    void testAddFirestations_Failure_NoFirestationsNode() throws IOException {
        FireStations fireStations = new FireStations("1234 Elm St", "3");

        when(informationRepository.readFile()).thenReturn(root);
        when(root.has("firestations")).thenReturn(false); 
        when(root.get("firestations")).thenReturn(null);  

        assertThrows(IOException.class, () -> fireStationRepository.addFirestations(fireStations));
    }
    


    //  Test: Successfully update an existing fire station
    @Test
    void testUpdateFirestations_Success() throws IOException {
        FireStationDTO fireStationDTO = new FireStationDTO("1234 Elm St", "4");

        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("firestations")).thenReturn(fireStationsArray);
        
        when(recordNode.get("address")).thenReturn(mock(JsonNode.class));
        when(recordNode.get("address").asText()).thenReturn(fireStationDTO.getAddress());
        List<JsonNode> recordsList = new ArrayList<>();
        recordsList.add(recordNode);
        when(fireStationsArray.iterator()).thenReturn(recordsList.iterator());

        fireStationRepository.updateFirestations(fireStationDTO.getAddress(), fireStationDTO);

        verify(recordNode, times(1)).put("station", fireStationDTO.getStation());
        verify(informationRepository, times(1)).writeFile(root);
    }

    //  Test: Update fire station fails due to missing "firestations" node
    @Test
    void testUpdateFirestations_Failure_NoFirestationsNode() throws IOException {
        FireStationDTO fireStationDTO = new FireStationDTO("1234 Elm St", "4");

        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("firestations")).thenReturn(null);

        assertThrows(NullPointerException.class, () -> fireStationRepository.updateFirestations(fireStationDTO.getAddress(), fireStationDTO));
    }
    
 
    //  Test: Successfully delete an existing fire station
    @Test
    void testDeleteFirestations_Success() throws IOException {
        String address = "1234 Elm St";

        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("firestations")).thenReturn(fireStationsArray);

        when(recordNode.get("address")).thenReturn(mock(JsonNode.class));
        when(recordNode.get("address").asText()).thenReturn(address);
        List<JsonNode> recordsList = new ArrayList<>();
        recordsList.add(recordNode);
        when(fireStationsArray.iterator()).thenReturn(recordsList.iterator());

        fireStationRepository.deleteFirestations(address);

        verify(informationRepository, times(1)).writeFile(root);
    }

    //  Test: Deleting a fire station fails due to missing "firestations" node
    @Test
    void testDeleteFirestations_Failure_NoFirestationsNode() throws IOException {
        String address = "1234 Elm St";

        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("firestations")).thenReturn(null);

        assertThrows(NullPointerException.class, () -> fireStationRepository.deleteFirestations(address));
    }


    //  Test: `test7` - Fetch data for fire stations
    @Test
    void testGetAllHomesServedByStation_Success() throws IOException {
        JsonNode personsNode = mock(JsonNode.class);
        JsonNode medicalRecordsNode = mock(JsonNode.class);
        JsonNode fireStationsNode = mock(JsonNode.class);

        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("persons")).thenReturn(personsNode);
        when(root.get("medicalrecords")).thenReturn(medicalRecordsNode);
        when(root.get("firestations")).thenReturn(fireStationsNode);

        
        List<JsonNode> recordsList = new ArrayList<>();
        recordsList.add(recordNode);
        when(fireStationsNode.iterator()).thenReturn(recordsList.iterator());

        when(recordNode.get("station")).thenReturn(mock(JsonNode.class));
        when(recordNode.get("station").asText()).thenReturn("3");

        Object result = fireStationRepository.getAllHomesServedByStation(List.of("3"));
        assertNotNull(result);
    }

    //  Test: `test7` fails due to missing "firestations" node
    @Test
    void testGetAllHomesServedByStation_Failure() throws IOException {
        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("firestations")).thenReturn(null);

        assertThrows(NullPointerException.class, () -> fireStationRepository.getAllHomesServedByStation(List.of("3")));
  
    }
    
 // Test: `test7` fails due to empty fire stations
    @Test
    void testGetAllHomesServedByStation_Failure_EmptyFireStations() throws IOException {
    	rootNode = nodeFactory.objectNode(); // Initialise rootNode
        when(informationRepository.readFile()).thenReturn(rootNode);
        
        ArrayNode fireStationsArray = nodeFactory.arrayNode(); // Créer un tableau vide
        ((ObjectNode) rootNode).set("firestations", fireStationsArray); // Ajoute-le à rootNode

        Object result = fireStationRepository.getAllHomesServedByStation(List.of("3"));
        assertTrue(((List<?>) result).isEmpty()); // Vérifie que le résultat est vide
    }

    @Test
    void testGetAllHomesServedByStationNullInput() {
        assertThrows(NullPointerException.class, () -> {
        	fireStationRepository.getAllHomesServedByStation(null);
        });
    }

    //  Test: `test8` - Fetch data for fire stations by address
    @Test
    void testGetResidentsAndTheNumberOfTheFireStationAtTheAddress_Success() throws IOException {
        JsonNode personsNode = mock(JsonNode.class);
        JsonNode medicalRecordsNode = mock(JsonNode.class);
        JsonNode fireStationsNode = mock(JsonNode.class);

        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("persons")).thenReturn(personsNode);
        when(root.get("medicalrecords")).thenReturn(medicalRecordsNode);
        when(root.get("firestations")).thenReturn(fireStationsNode);

        
        List<JsonNode> recordsList = new ArrayList<>();
        recordsList.add(recordNode);
        when(fireStationsNode.iterator()).thenReturn(recordsList.iterator());

        when(recordNode.get("address")).thenReturn(mock(JsonNode.class));
        when(recordNode.get("address").asText()).thenReturn("1234 Elm St");

        Object result = fireStationRepository.getResidentsAndTheNumberOfTheFireStationAtTheAddress("1234 Elm St");

        assertNotNull(result);
    }

    //  Test: `test8` fails due to missing "firestations" node
    @Test
    void testGetResidentsAndTheNumberOfTheFireStationAtTheAddress_Failure() throws IOException {
        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("firestations")).thenReturn(null);

        assertThrows(NullPointerException.class, () -> fireStationRepository.getResidentsAndTheNumberOfTheFireStationAtTheAddress("1234 Elm St"));
    }
    @Test
    void testFireStationExistsWithPersonsAndRecords() throws IOException {
    	String address = "123 Main St";
        
        // Initialiser rootNode avec des données valides
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "{ \"persons\": [{\"firstName\": \"John\", \"lastName\": \"Doe\", \"address\": \"123 Main St\", \"phone\": \"123-456-7890\"}], " +
                            "\"medicalrecords\": [{\"firstName\": \"John\", \"lastName\": \"Doe\", \"birthdate\": \"01/01/2000\", \"medications\": [], \"allergies\": []}], " +
                            "\"firestations\": [{\"address\": \"123 Main St\", \"station\": \"1\"}] }"; // Remplir avec des données appropriées
        JsonNode rootNode = objectMapper.readTree(jsonString);
        
        // Configurer le mock pour retourner rootNode
        when(informationRepository.readFile()).thenReturn(rootNode);

        // Vérifiez que rootNode n'est pas null
        assertNotNull(rootNode, "rootNode should not be null");

        // Appeler la méthode à tester
        List<Map<String, Object>> result = (List<Map<String, Object>>) fireStationRepository.getResidentsAndTheNumberOfTheFireStationAtTheAddress(address);

        // Ajoutez des assertions pour vérifier que le résultat est comme attendu
        assertFalse(result.isEmpty());
        assertEquals(2, result.size()); 
    }

    @Test
    void testFireStationDoesNotExist() throws IOException {
    	String address = "Nonexistent St";

        // Initialiser rootNode avec une adresse qui n'existe pas
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "{ \"persons\": [{\"firstName\": \"John\", \"lastName\": \"Doe\", \"address\": \"123 Main St\", \"phone\": \"123-456-7890\"}], " +
                            "\"medicalrecords\": [{\"firstName\": \"John\", \"lastName\": \"Doe\", \"birthdate\": \"01/01/2000\", \"medications\": [], \"allergies\": []}], " +
                            "\"firestations\": [{\"address\": \"123 Main St\", \"station\": \"1\"}] }";
        JsonNode rootNode = objectMapper.readTree(jsonString);

        // Configurer le mock pour retourner rootNode
        when(informationRepository.readFile()).thenReturn(rootNode);
        
        // Vérifiez que rootNode n'est pas null
        assertNotNull(rootNode, "rootNode should not be null");

        // Appel de la méthode à tester
        List<Map<String, Object>> result = (List<Map<String, Object>>) fireStationRepository.getResidentsAndTheNumberOfTheFireStationAtTheAddress(address);

        // Assertions pour vérifier que la station n'existe pas
        assertTrue(result.isEmpty());
    }

    @Test
    void testFireStationExistsNoPersons() throws IOException {
    	 String address = "123 Main St";
    	    
    	    // Initialiser rootNode avec des données valides
    	    ObjectMapper objectMapper = new ObjectMapper();
    	    String jsonString = "{ \"persons\": [], \"medicalrecords\": [], \"firestations\": [{\"address\": \"123 Main St\", \"station\": \"1\"}] }";
    	    rootNode = objectMapper.readTree(jsonString);
    	    
    	    // Configurer le mock pour retourner rootNode
    	    when(informationRepository.readFile()).thenReturn(rootNode);

    	    // Vérifiez que rootNode n'est pas null
    	    assertNotNull(rootNode, "rootNode should not be null");

    	    List<Map<String, Object>> result = (List<Map<String, Object>>) fireStationRepository.getResidentsAndTheNumberOfTheFireStationAtTheAddress(address);

    	    assertFalse(result.isEmpty());
    }

    @Test
    void testMedicalRecordMissing() throws IOException {
    	 String address = "123 Main St";

    	    // Préparation des données de test
    	    ObjectMapper objectMapper = new ObjectMapper();
    	    String jsonString = "{ \"persons\": [{\"firstName\": \"John\", \"lastName\": \"Doe\", \"address\": \"" + address + "\", \"phone\": \"123-456-7890\"}], " +
    	                        "\"medicalrecords\": [], " + // Pas d'enregistrements médicaux
    	                        "\"firestations\": [{\"address\": \"" + address + "\", \"station\": \"1\"}] }"; // Station valide
    	    JsonNode rootNode = objectMapper.readTree(jsonString);

    	    // Configuration du mock
    	    when(informationRepository.readFile()).thenReturn(rootNode);
    	    
    	    // Vérifiez que rootNode n'est pas null
    	    assertNotNull(rootNode, "rootNode should not be null");

    	    // Appel de la méthode à tester
    	    List<Map<String, Object>> result = (List<Map<String, Object>>) fireStationRepository.getResidentsAndTheNumberOfTheFireStationAtTheAddress(address);

    	    // Assertions pour vérifier le comportement attendu
    	    assertFalse(result.isEmpty());
    	    // Vérifiez que les données de la station sont présentes, mais pas d'informations médicales
    	    
    }

    
 //  Test: `test9` - Successfully fetch data for fire station
    @Test
    void testGetTelephoneNumbersOfResidentsServedByTheFireStation_Success() throws IOException {
    	 String station = "1";

    	    when(informationRepository.readFile()).thenReturn(root);
    	    when(root.get("persons")).thenReturn(personsNode);
    	    when(root.get("firestations")).thenReturn(firestationsNode);

    	   
    	    when(firestationsNode.iterator()).thenReturn(Arrays.asList(fireStation1).iterator());

    	  
    	    JsonNode mockStationNode = mock(JsonNode.class);
    	    JsonNode mockAddressNode = mock(JsonNode.class);

    	    when(fireStation1.get("station")).thenReturn(mockStationNode);
    	    when(mockStationNode.asText()).thenReturn("1");

    	    when(fireStation1.get("address")).thenReturn(mockAddressNode);
    	    when(mockAddressNode.asText()).thenReturn("address1");

    	
    	    when(personsNode.iterator()).thenReturn(Arrays.asList(person1).iterator());

    	    JsonNode mockPersonAddressNode = mock(JsonNode.class);
    	    JsonNode mockPhoneNode = mock(JsonNode.class);

    	    when(person1.get("address")).thenReturn(mockPersonAddressNode);
    	    when(mockPersonAddressNode.asText()).thenReturn("address1"); 

    	    when(person1.get("phone")).thenReturn(mockPhoneNode);
    	    when(mockPhoneNode.asText()).thenReturn("123456789"); 

    	   
    	    List<Map<String, Object>> result = fireStationRepository.getTelephoneNumbersOfResidentsServedByTheFireStation(station);

    	    
    	    assertNotNull(result);
    	    
    }
    
    //  Test: `test9` fails when no matching station is found
    @Test
    void testGetTelephoneNumbersOfResidentsServedByTheFireStation_Failure_NoMatchingStation() throws IOException {
        
        String station = "3"; // No matching station

        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("persons")).thenReturn(personsNode);
        when(root.get("firestations")).thenReturn(firestationsNode);

       
        when(firestationsNode.iterator()).thenReturn(Arrays.asList(fireStation1, fireStation2).iterator());

      
        JsonNode mockStation1Node = mock(JsonNode.class);
        JsonNode mockStation2Node = mock(JsonNode.class);

        when(fireStation1.get("station")).thenReturn(mockStation1Node);
        when(mockStation1Node.asText()).thenReturn("1");

        when(fireStation2.get("station")).thenReturn(mockStation2Node);
        when(mockStation2Node.asText()).thenReturn("2");

        
        List<Map<String, Object>> result = fireStationRepository.getTelephoneNumbersOfResidentsServedByTheFireStation(station);

       
        assertEquals(0, result.size()); 
    }

    //  Test: `test9` fails when no matching person is found
    @Test
    void testGetTelephoneNumbersOfResidentsServedByTheFireStation_Failure_NoMatchingPerson() throws IOException {
        String station = "1";

        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("persons")).thenReturn(personsNode);
        when(root.get("firestations")).thenReturn(firestationsNode);

        when(firestationsNode.iterator()).thenReturn(Collections.singletonList(fireStation1).iterator());

        JsonNode mockStationNode = mock(JsonNode.class);
        JsonNode mockAddressNode = mock(JsonNode.class);

        when(fireStation1.get("station")).thenReturn(mockStationNode);
        when(mockStationNode.asText()).thenReturn("1");

        when(fireStation1.get("address")).thenReturn(mockAddressNode);
        when(mockAddressNode.asText()).thenReturn("address1");

        when(personsNode.iterator()).thenReturn(Arrays.asList(person1, person2).iterator());

       
        JsonNode mockPerson1AddressNode = mock(JsonNode.class);
        JsonNode mockPerson2AddressNode = mock(JsonNode.class);

        when(person1.get("address")).thenReturn(mockPerson1AddressNode);
        when(mockPerson1AddressNode.asText()).thenReturn("address2");

        when(person2.get("address")).thenReturn(mockPerson2AddressNode);
        when(mockPerson2AddressNode.asText()).thenReturn("address3");

        List<Map<String, Object>> result = fireStationRepository.getTelephoneNumbersOfResidentsServedByTheFireStation(station);

        assertEquals(0, result.size());  
    }
    //  Test: `test9` returns empty list when no persons exist
    @Test
    void testGetTelephoneNumbersOfResidentsServedByTheFireStation_Failure_NoPersons() throws IOException {
       
        String station = "1";

        when(informationRepository.readFile()).thenReturn(root);
        when(root.get("persons")).thenReturn(personsNode);
        when(root.get("firestations")).thenReturn(firestationsNode);

       
        when(firestationsNode.iterator()).thenReturn(Collections.singletonList(fireStation1).iterator());

      
        JsonNode mockStationNode = mock(JsonNode.class);
        JsonNode mockAddressNode = mock(JsonNode.class);

        when(fireStation1.get("station")).thenReturn(mockStationNode);
        when(mockStationNode.asText()).thenReturn("1");

        when(fireStation1.get("address")).thenReturn(mockAddressNode);
        when(mockAddressNode.asText()).thenReturn("address1");

        
        when(personsNode.iterator()).thenReturn(Collections.emptyIterator());

      
        List<Map<String, Object>> result = fireStationRepository.getTelephoneNumbersOfResidentsServedByTheFireStation(station);

       
        assertEquals(0, result.size()); 
    }
   
    
    @Test
    void testGetChildrenLivingAtThisAddress_Success() throws IOException {
    	rootNode = nodeFactory.objectNode();
        when(informationRepository.readFile()).thenReturn(rootNode);
        ObjectNode personNode = nodeFactory.objectNode();
        personNode.put("address", "123 Main St");
        personNode.put("firstName", "John");
        personNode.put("lastName", "Doe");
        
        ObjectNode medicalRecordNode = nodeFactory.objectNode();
        medicalRecordNode.put("firstName", "John");
        medicalRecordNode.put("lastName", "Doe");
        medicalRecordNode.put("birthdate", LocalDate.now().minusYears(20).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        
        ((ObjectNode) rootNode).set("persons", nodeFactory.arrayNode().add(personNode));
        ((ObjectNode) rootNode).set("medicalrecords", nodeFactory.arrayNode().add(medicalRecordNode));

        Object result = fireStationRepository.getChildrenLivingAtThisAddress("123 Main St");
        assertNotNull(result);
        assertTrue(result instanceof List);
        assertFalse(((List<?>) result).isEmpty());
    }

 // Test: `test10` returns empty result when no records exist
    @Test
    void testGetChildrenLivingAtThisAddress_Failure_NoRecords() throws IOException {
    	 // Create and initialize the rootNode
        rootNode = nodeFactory.objectNode();
        
        // Assuming personsNode should be part of the rootNode
        ObjectNode personsNode = nodeFactory.objectNode(); // or create an array if needed
        ((ObjectNode) rootNode).set("persons", personsNode); // Add to rootNode

        // Mock the readFile method to return the rootNode
        when(informationRepository.readFile()).thenReturn(rootNode);
        
        // Provide the required argument for test10 method
        String fireStationId = "stationId"; // Replace with appropriate value
        Object result = fireStationRepository.getChildrenLivingAtThisAddress(fireStationId); // Call with argument

        // Assert that the result is as expected
        assertTrue(((List<?>) result).isEmpty()); // Example assertion, adjust as needed
    }
  
    @Test
    void testGetPersonsCoveredByTheFireStation_Success() throws IOException {
    	rootNode = nodeFactory.objectNode();
        
        
        ObjectNode fireStationNode = nodeFactory.objectNode();
        fireStationNode.put("station", "1");
        fireStationNode.put("address", "123 Main St");

        ObjectNode personNode = nodeFactory.objectNode();
        personNode.put("address", "123 Main St");
        personNode.put("firstName", "Jane");
        personNode.put("lastName", "Doe");

        ObjectNode medicalRecordNode = nodeFactory.objectNode();
        medicalRecordNode.put("firstName", "Jane");
        medicalRecordNode.put("lastName", "Doe");
        medicalRecordNode.put("birthdate", LocalDate.now().minusYears(10).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

       
        ((ObjectNode) rootNode).set("firestations", nodeFactory.arrayNode().add(fireStationNode));
        ((ObjectNode) rootNode).set("persons", nodeFactory.arrayNode().add(personNode));
        ((ObjectNode) rootNode).set("medicalrecords", nodeFactory.arrayNode().add(medicalRecordNode));

      
        when(informationRepository.readFile()).thenReturn(rootNode);

       
        Object result = fireStationRepository.getPersonsCoveredByTheFireStation("1");

       
        assertNotNull(result);
        assertTrue(result instanceof List);
        assertFalse(((List<?>) result).isEmpty());

       
        Map<String, Object> counts = (Map<String, Object>) ((List<?>) result).get(((List<?>) result).size() - 1);
        assertEquals(0, counts.get("number Adult"));
        assertEquals(1, counts.get("number Child"));
    }
}
