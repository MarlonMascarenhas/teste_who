package com.teste.who.rest.controller;

import com.teste.who.service.FieldMappingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FieldMappingController.class)
public class FieldMappingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FieldMappingService fieldMappingService;

    @Test
    void testConfigureMappingWithValidMapping() throws Exception {
        String expectedResponse = "Mapping updated successfully.";
        when(fieldMappingService.configureMapping(any())).thenReturn(expectedResponse);

        ResultActions result = mockMvc.perform(post("/v1/user/configureMapping")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mapping\": {\"inputField1\": \"outputField1\"}}"));

        result.andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void testProcessDataWithValidMapping() throws Exception {
        String expectedResponse = "Mapping updated successfully.";
        when(fieldMappingService.configureMapping(any())).thenReturn(expectedResponse);

        ResultActions result = mockMvc.perform(post("/v1/user/configureMapping")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mapping\": {\"inputField1\": \"outputField1\"}}"));

        result.andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void testProcessDataWithValidInput() throws Exception {
        // Arrange
        Map<String, Object> userData = new HashMap<>();
        userData.put("protocolId", "b3788d36-037d-40d8-b448-90bc9724f388");
        userData.put("messageId", "4f3f9fe2-2f19-43b4-b611-b871a80ecfec");

        Map<String, Object> data = new HashMap<>();
        data.put("score", 800.3);
        data.put("name", "Maria");
        data.put("birthDate", "19/05/1995");
        data.put("age", 29);

        userData.put("data", data);

        // Mock the service method
        when(fieldMappingService.processData(any(Map.class))).thenReturn(ResponseEntity.ok(userData));

        // Act & Assert
        mockMvc.perform(post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"messageId\": \"4f3f9fe2-2f19-43b4-b611-b871a80ecfec\", \"protocolId\": \"b3788d36-037d-40d8-b448-90bc9724f388\", \"dadosPessoais\": {\"nome\": \"Maria\", \"idade\": 29, \"dataNascimento\": \"19-05-1995 00:00:00\", \"score\": \"800.30\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.protocolId").value("b3788d36-037d-40d8-b448-90bc9724f388"))
                .andExpect(jsonPath("$.messageId").value("4f3f9fe2-2f19-43b4-b611-b871a80ecfec"))
                .andExpect(jsonPath("$.data.score").value(800.3))
                .andExpect(jsonPath("$.data.name").value("Maria"))
                .andExpect(jsonPath("$.data.birthDate").value("19/05/1995"))
                .andExpect(jsonPath("$.data.age").value(29));
    }
}