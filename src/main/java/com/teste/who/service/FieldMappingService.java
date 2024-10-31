package com.teste.who.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class FieldMappingService {

    private final Map<String, FieldInfo> fieldMapping = new ConcurrentHashMap<>();

    static class FieldInfo {
        String mappedName;
        Class<?> type;
        String dateFormat;
        String enterDateFormat;

        FieldInfo(String mappedName, Class<?> type, String dateFormat, String enterDateFormat) {
            this.mappedName = mappedName;
            this.type = type;
            this.dateFormat = dateFormat;
            this.enterDateFormat = enterDateFormat;
        }
    }

    private Class<?> getClassForType(String typeStr) {
        switch (typeStr) {
            case "String":
                return String.class;
            case "Integer":
                return Integer.class;
            case "Double":
                return Double.class;
            case "Boolean":
                return Boolean.class;
            case "Date":
                return Date.class;
            case "Object":
                return Object.class;
            case "Long":
                return Long.class;
            default:
                return null;
        }
    }

    public String configureMapping(Map<String, Map<String, String>> mapping) {

        fieldMapping.put("messageId", new FieldInfo("messageId", String.class, null, null));
        fieldMapping.put("protocolId", new FieldInfo("protocolId", String.class, null, null));
        if (mapping == null ) {
            return "Mapping updated successfully.";
        }
        for (Map.Entry<String, Map<String, String>> entry : mapping.entrySet()) {
            String fieldName = entry.getKey();
            String mappedName = entry.getValue().get("name");
            String typeStr = entry.getValue().get("type");
            String dateFormat = entry.getValue().getOrDefault("dateFormat", null);
            String enterDateFormat = entry.getValue().getOrDefault("enterDateFormat", null);

            Class<?> type = getClassForType(typeStr);
            if (type == null) {
                return "Unsupported type: " + typeStr;
            }

            fieldMapping.put(fieldName, new FieldInfo(mappedName, type, dateFormat, enterDateFormat));
        }
        return "Mapping updated successfully.";
    }

    public ResponseEntity<?> processData(Map<String, Object> inputData) {
        Set<String> unmappedFields = findUnmappedFields(inputData);

        if (!inputData.containsKey("messageId") || !inputData.containsKey("protocolId")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Required fields 'messageId' or 'protocolId' are missing.");
        }

        if (!unmappedFields.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Field(s) not registered: " + String.join(", ", unmappedFields));
        }

        try {
            Map<String, Object> transformedData = transformData(inputData);
            return ResponseEntity.ok(transformedData);
        } catch (IllegalArgumentException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    private Set<String> findUnmappedFields(Map<String, Object> data) {
        return data.entrySet().stream()
                .flatMap(entry -> {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (value instanceof Map) {
                        return findUnmappedFields((Map<String, Object>) value).stream();
                    } else {
                        return fieldMapping.containsKey(key) ? null : Set.of(key).stream();
                    }
                })
                .collect(Collectors.toSet());
    }

    private Map<String, Object> transformData(Map<String, Object> data) throws ParseException {
        Map<String, Object> transformedData = new ConcurrentHashMap<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            FieldInfo fieldInfo = fieldMapping.get(key);
            if (fieldInfo == null) {
                throw new IllegalArgumentException("Field not mapped: " + key);
            }

            String mappedKey = fieldInfo.mappedName;
            Object convertedValue;

            if (value instanceof Map) {
                convertedValue = transformData((Map<String, Object>) value);
            } else {
                convertedValue = convertValue(value, fieldInfo.type, fieldInfo.dateFormat, fieldInfo.enterDateFormat);
            }

            transformedData.put(mappedKey, convertedValue);
        }

        return transformedData;
    }

    private Object convertValue(Object value, Class<?> targetType, String dateFormat, String enterDateFormat) throws ParseException {
        try {
            if (targetType == String.class) {
                return value.toString();
            } else if (targetType == Double.class) {
                return Double.parseDouble(value.toString());
            } else if (targetType == Integer.class) {
                return Integer.parseInt(value.toString());
            } else if (targetType == Boolean.class) {
                return Boolean.parseBoolean(value.toString());
            } else if (targetType == Long.class) {
                return Long.parseLong(value.toString());
            } else if (targetType == Date.class) {
                if (dateFormat == null) {
                    throw new IllegalArgumentException("Date format not specified for date field");
                }
                SimpleDateFormat sdf = new SimpleDateFormat(enterDateFormat);
                Date date = sdf.parse(value.toString());
                sdf.applyPattern(dateFormat);
                return sdf.format(date);
            } else if (targetType == Object.class) {
                throw new IllegalArgumentException("Cannot convert value: " + value + " to type: " + targetType.getSimpleName());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot convert value: " + value + " to type: " + targetType.getSimpleName());
        }
        throw new IllegalArgumentException("Unsupported target type: " + targetType.getSimpleName());
    }

}
