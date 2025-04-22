
package ru.minusd.security.config;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ErrorResponse {
    
    @JsonProperty("error")
    private String error;
    @JsonProperty("message")
    private String message;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    
    
    // Геттеры
    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
    
}
