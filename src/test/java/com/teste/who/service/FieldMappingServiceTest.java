package com.teste.who.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FieldMappingServiceTest {

    @InjectMocks
    private FieldMappingService fieldMappingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConfigureMapping_ValidMapping() {
        Map<String, Map<String, String>> mapping = new HashMap<>();
        Map<String, String> fieldProperties = new HashMap<>();
        fieldProperties.put("name", "mappedName");
        fieldProperties.put("type", "String");
        mapping.put("inputField", fieldProperties);

        String result = fieldMappingService.configureMapping(mapping);

        assertEquals("Mapping updated successfully.", result);
    }

    @Test
    void testConfigureMapping_ValidMapping_WithAllTypes() {
        Map<String, Map<String, String>> mapping = new HashMap<>();
        Map<String, String> fieldPropertiesString = new HashMap<>();
        fieldPropertiesString.put("name", "mappedName");
        fieldPropertiesString.put("type", "String");
        Map<String, String> fieldPropertiesInteger = new HashMap<>();
        fieldPropertiesInteger.put("name", "mappedNameInteger");
        fieldPropertiesInteger.put("type", "Integer");
        Map<String, String> fieldPropertiesDouble = new HashMap<>();
        fieldPropertiesDouble.put("name", "mappedNameDouble");
        fieldPropertiesDouble.put("type", "Double");
        Map<String, String> fieldPropertiesBoolean = new HashMap<>();
        fieldPropertiesBoolean.put("name", "mappedNameBoolean");
        fieldPropertiesBoolean.put("type", "Boolean");
        Map<String, String> fieldPropertiesDate = new HashMap<>();
        fieldPropertiesDate.put("name", "mappedNameDate");
        fieldPropertiesDate.put("type", "Date");
        Map<String, String> fieldPropertiesObject = new HashMap<>();
        fieldPropertiesObject.put("name", "mappedNameObject");
        fieldPropertiesObject.put("type", "Object");
        Map<String, String> fieldPropertiesLong = new HashMap<>();
        fieldPropertiesLong.put("name", "mappedNameLong");
        fieldPropertiesLong.put("type", "Long");
        mapping.put("fieldPropertiesString", fieldPropertiesString);
        mapping.put("fieldPropertiesInteger", fieldPropertiesInteger);
        mapping.put("fieldPropertiesDouble", fieldPropertiesDouble);
        mapping.put("fieldPropertiesBoolean", fieldPropertiesBoolean);
        mapping.put("fieldPropertiesDate", fieldPropertiesDate);
        mapping.put("fieldPropertiesObject", fieldPropertiesObject);
        mapping.put("fieldPropertiesLong", fieldPropertiesLong);

        String result = fieldMappingService.configureMapping(mapping);

        assertEquals("Mapping updated successfully.", result);
    }

    @Test
    void testConfigureMapping_ValidMapping_NoFields() {

        String result = fieldMappingService.configureMapping(null);

        assertEquals("Mapping updated successfully.", result);
    }

    @Test
    void testConfigureMapping_UnsupportedType() {
        Map<String, Map<String, String>> mapping = new HashMap<>();
        Map<String, String> fieldProperties = new HashMap<>();
        fieldProperties.put("name", "mappedName");
        fieldProperties.put("type", "UnsupportedType");
        mapping.put("inputField", fieldProperties);

        String result = fieldMappingService.configureMapping(mapping);

        assertTrue(result.startsWith("Unsupported type: "));
    }

    @Test
    void testProcessData_ValidInputData() throws ParseException {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("messageId", "123");
        inputData.put("protocolId", "abc");
        inputData.put("inputField", "value");

        Map<String, Map<String, String>> mapping = new HashMap<>();
        Map<String, String> fieldProperties = new HashMap<>();
        fieldProperties.put("name", "mappedName");
        fieldProperties.put("type", "String");
        mapping.put("inputField", fieldProperties);
        fieldMappingService.configureMapping(mapping);

        ResponseEntity<?> response = fieldMappingService.processData(inputData);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> transformedData = (Map<String, Object>) response.getBody();
        assertEquals("value", transformedData.get("mappedName"));
    }

    @Test
    void testProcessData_MissingRequiredFields() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("inputField", "value");

        ResponseEntity<?> response = fieldMappingService.processData(inputData);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Required fields 'messageId' or 'protocolId' are missing.", response.getBody());
    }

    @Test
    void testProcessData_UnmappedFields() {
        fieldMappingService.configureMapping(null);
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("messageId", "123");
        inputData.put("protocolId", "abc");
        inputData.put("unmappedField", "value");

        ResponseEntity<?> response = fieldMappingService.processData(inputData);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Field(s) not registered: unmappedField"));
    }

    @Test
    void testTransformData_InvalidDateFormat() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("messageId", "123");
        inputData.put("protocolId", "abc");
        inputData.put("dateField", "2024-10-31");

        Map<String, Map<String, String>> mapping = new HashMap<>();
        Map<String, String> fieldProperties = new HashMap<>();
        fieldProperties.put("name", "dateMapped");
        fieldProperties.put("type", "Date");
        fieldProperties.put("dateFormat", "dd/MM/yyyy");
        mapping.put("dateField", fieldProperties);
        fieldMappingService.configureMapping(mapping);

        ResponseEntity<?> response = fieldMappingService.processData(inputData);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Cannot convert value: 2024-10-31 to type: Date"));
    }
}
