package com.SafetyNet.SafetyNetAlerts.Repository.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.SafetyNet.SafetyNetAlerts.repository.InformationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;


@SpringBootTest
class InformationRepositoryTest {
	

	@Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private InformationRepository informationRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testReadFile_Success() throws IOException {
       
        JsonNode mockJsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(any(File.class))).thenReturn(mockJsonNode);

       
        JsonNode result = informationRepository.readFile();

       
        assertNotNull(result);
        verify(objectMapper, times(1)).readTree(any(File.class));
    }

    @Test
    public void testReadFile_Failure() throws IOException {
       
        when(objectMapper.readTree(any(File.class))).thenThrow(new IOException("File not found"));

        
        JsonNode result = informationRepository.readFile();

       
        assertNull(result);
        verify(objectMapper, times(1)).readTree(any(File.class));
    }

    @Test
    public void testWriteFile() throws IOException {
       
        JsonNode mockJsonNode = mock(JsonNode.class);

       
        informationRepository.writeFile(mockJsonNode);

       
        verify(objectMapper, times(1)).writeValue(any(File.class), eq(mockJsonNode));
    }

    @Test
    public void testReadPersons_Success() throws IOException {
        
        Map<String, Object> mockMap = mock(Map.class);
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(mockMap);
        when(mockMap.get("persons")).thenReturn(new Object());

        
        Object result = informationRepository.readPersons();

        
        assertNotNull(result);
        verify(objectMapper, times(1)).readValue(any(File.class), any(TypeReference.class));
    }

    @Test
    public void testReadPersons_Failure() throws IOException {
        
        when(objectMapper.readValue(any(File.class), any(TypeReference.class)))
            .thenThrow(new IOException("Error reading persons data"));

      
        Object result = informationRepository.readPersons();

        
        assertNull(result);
        verify(objectMapper, times(1)).readValue(any(File.class), any(TypeReference.class));
    }

    @Test
    public void testReadMedicalRecords_Success() throws IOException {
       
        Map<String, Object> mockMap = mock(Map.class);
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(mockMap);
        when(mockMap.get("medicalrecords")).thenReturn(new Object());

        
        Object result = informationRepository.readMedicalRecords();

        
        assertNotNull(result);
        verify(objectMapper, times(1)).readValue(any(File.class), any(TypeReference.class));
    }

    @Test
    public void testReadMedicalRecords_Failure() throws IOException {
       
        when(objectMapper.readValue(any(File.class), any(TypeReference.class)))
            .thenThrow(new IOException("Error reading medical records data"));

        
        Object result = informationRepository.readMedicalRecords();

        
        assertNull(result);
        verify(objectMapper, times(1)).readValue(any(File.class), any(TypeReference.class));
    }

    @Test
    public void testReadFirestations_Success() throws IOException {
        
        Map<String, Object> mockMap = mock(Map.class);
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(mockMap);
        when(mockMap.get("firestations")).thenReturn(new Object());

       
        Object result = informationRepository.readFirestations();

        
        assertNotNull(result);
        verify(objectMapper, times(1)).readValue(any(File.class), any(TypeReference.class));
    }

    @Test
    public void testReadFirestations_Failure() throws IOException {
       
        when(objectMapper.readValue(any(File.class), any(TypeReference.class)))
            .thenThrow(new IOException("Error reading firestations data"));

        
        Object result = informationRepository.readFirestations();

       
        assertNull(result);
        verify(objectMapper, times(1)).readValue(any(File.class), any(TypeReference.class));
    }
	

}
