/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.minusd.security.domain.dto;

/**
 *
 * @author User
 */
public class ErrorResponsen {
    
    private String message;
    private Long taskId;

    // Конструктор
    public ErrorResponsen(String message, Long taskId) {
        this.message = message;
        this.taskId = taskId;
    }

    // Геттеры
    public String getMessage() {
        return message;
    }

    public Long getTaskId() {
        return taskId;
    }
}
    
