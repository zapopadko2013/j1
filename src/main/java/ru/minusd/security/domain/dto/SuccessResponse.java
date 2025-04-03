/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.minusd.security.domain.dto;
import ru.minusd.security.domain.model.Task;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author User
 */
public class SuccessResponse {
    
    @JsonProperty("message")
       private String message;
     @JsonProperty("task")
        private Task task;

        public SuccessResponse(String message, Task task) {
            this.message = message;
            this.task = task;
        }

        public String getMessage() {
            return message;
        }

        public Task getTask() {
            return task;
        }
    }

