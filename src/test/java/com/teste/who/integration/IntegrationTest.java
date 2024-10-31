package com.teste.who.integration;

import com.teste.who.rest.controller.FieldMappingController;
import com.teste.who.service.FieldMappingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class IntegrationTest {
    @InjectMocks
    private FieldMappingController fieldMappingController;

    @BeforeEach
    void setUp() {
        FieldMappingService fieldMappingService = new FieldMappingService();
        fieldMappingController = new FieldMappingController(fieldMappingService);
    }

    @Test
    void testConfigureMappingWithValidMapping() {
        Map<String, Map<String, String>> mapping = new HashMap<>();
        Map<String, String> nameMapping = new HashMap<>();
        nameMapping.put("name", "nome");
        nameMapping.put("type", "String");

        mapping.put("name", nameMapping);

        String result = fieldMappingController.configureMapping(mapping);

        Assertions.assertEquals("Mapping updated successfully.", result);
    }

    @Test
    void testTransformDataWithConfiguredMapping() throws ParseException {
        Map<String, Map<String, String>> mapping = new HashMap<>();
        Map<String, String> nameMapping = new HashMap<>();
        nameMapping.put("name", "nome");
        nameMapping.put("type", "String");

        Map<String, String> scoreMapping = new HashMap<>();
        scoreMapping.put("name", "pontuacao");
        scoreMapping.put("type", "Double");

        Map<String, String> userDataObjectEnter = new HashMap<>();
        userDataObjectEnter.put("name", "user");
        userDataObjectEnter.put("type", "Object");

        mapping.put("name", nameMapping);
        mapping.put("score", scoreMapping);
        mapping.put("user", userDataObjectEnter);

        fieldMappingController.configureMapping(mapping);

        Map<String, Object> inputData = new HashMap<>();
        inputData.put("messageId", "4123asd");
        inputData.put("protocolId", "789xyz");
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "Carlos");
        userData.put("score", "500.50");
        inputData.put("user", userData);

        ResponseEntity<?> response = fieldMappingController.processData(inputData);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> transformedData = (Map<String, Object>) response.getBody();

        Assertions.assertNotNull(transformedData);
        Assertions.assertEquals("4123asd", transformedData.get("messageId"));
        Assertions.assertEquals("789xyz", transformedData.get("protocolId"));

        Map<String, Object> transformedUserData = (Map<String, Object>) transformedData.get("user");
        Assertions.assertEquals("Carlos", transformedUserData.get("nome"));
        Assertions.assertEquals(500.5, transformedUserData.get("pontuacao"));
    }

    @Test
    void testMissingRequiredFieldError() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("protocolId", "789xyz");

        ResponseEntity<?> response = fieldMappingController.processData(inputData);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Required fields 'messageId' or 'protocolId' are missing.", response.getBody());
    }

    @Test
    void testUnmappedFieldError() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("messageId", "4123asd");
        inputData.put("protocolId", "789xyz");
        inputData.put("extraField", "unexpected"); // Campo não registrado
        fieldMappingController.configureMapping(null);
        ResponseEntity<?> response = fieldMappingController.processData(inputData);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Field(s) not registered: extraField", response.getBody());
    }

    @Test
    void testTypeConversionError() {
        // Configura o mapeamento com campo de tipo Double
        Map<String, Map<String, String>> mapping = new HashMap<>();
        Map<String, String> scoreMapping = new HashMap<>();
        scoreMapping.put("name", "pontuacao");
        scoreMapping.put("type", "Double");

        mapping.put("score", scoreMapping);

        fieldMappingController.configureMapping(mapping);

        // Dados de entrada com tipo incompatível para score
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("messageId", "4123asd");
        inputData.put("protocolId", "789xyz");
        inputData.put("score", "invalid_number");

        ResponseEntity<?> response = fieldMappingController.processData(inputData);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Error: Cannot convert value: invalid_number to type: Double", response.getBody());
    }

    @Test
    void testDateConversionErrorWithoutDateFormat() {
        // Configura o mapeamento com campo de tipo Date, mas sem formato de data
        Map<String, Map<String, String>> mapping = new HashMap<>();
        Map<String, String> dateMapping = new HashMap<>();
        dateMapping.put("name", "dataCriacao");
        dateMapping.put("type", "Date");

        mapping.put("createdAt", dateMapping);

        fieldMappingController.configureMapping(mapping);

        // Dados de entrada
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("messageId", "4123asd");
        inputData.put("protocolId", "789xyz");
        inputData.put("createdAt", "27/08/2024");

        ResponseEntity<?> response = fieldMappingController.processData(inputData);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Error: Cannot convert value: 27/08/2024 to type: Date", response.getBody());
    }

    @Test
    void testValidDateConversionWithDateFormat() {
        // Configura o mapeamento com campo de tipo Date e formato de data
        Map<String, Map<String, String>> mapping = new HashMap<>();
        Map<String, String> dateMapping = new HashMap<>();
        dateMapping.put("name", "dataCriacao");
        dateMapping.put("type", "Date");
        dateMapping.put("enterDateFormat", "dd/MM/yyyy");
        dateMapping.put("dateFormat", "dd/MM/yyyy");

        mapping.put("createdAt", dateMapping);

        fieldMappingController.configureMapping(mapping);

        // Dados de entrada
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("messageId", "4123asd");
        inputData.put("protocolId", "789xyz");
        inputData.put("createdAt", "27/08/2024");

        ResponseEntity<?> response = fieldMappingController.processData(inputData);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> transformedData = (Map<String, Object>) response.getBody();

        Assertions.assertNotNull(transformedData);
        Assertions.assertEquals("4123asd", transformedData.get("messageId"));
        Assertions.assertEquals("789xyz", transformedData.get("protocolId"));
        Assertions.assertNotNull(transformedData.get("dataCriacao"));
    }
}
