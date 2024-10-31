package com.teste.who.rest.controller;

import com.teste.who.service.FieldMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Validated
@RestController
@RequestMapping(path = "v1/user", produces = "application/json")
@RequiredArgsConstructor
public class FieldMappingController {

    private final FieldMappingService fieldMappingService;

    @PostMapping("/configureMapping")
    public String configureMapping(@RequestBody Map<String, Map<String, String>> mapping) {
        return fieldMappingService.configureMapping(mapping);
    }

    @PostMapping
    public ResponseEntity<?> processData(@RequestBody Map<String, Object> inputData) {
        return fieldMappingService.processData(inputData);
    }

}
