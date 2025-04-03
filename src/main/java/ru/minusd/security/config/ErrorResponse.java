/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.minusd.security.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author User
 */
public class ErrorResponse {
    
    @JsonProperty("error")
    private String error;
    @JsonProperty("message")
    private String message;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    // getters and setters
    
    // Геттеры
    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
    
}
