package com.uyghrujava.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Map;

public class EntityResponse {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseBody) {

        Map<String, Object> response = Map.of(
                "Timestamp", new Date(),
                "Message", message,
                "Status", status.value(),
                "Response_Data", responseBody
        );

        return new ResponseEntity<Object>(response, status);
    }
}
