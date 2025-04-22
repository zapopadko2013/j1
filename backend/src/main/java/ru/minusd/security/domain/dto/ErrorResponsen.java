
package ru.minusd.security.domain.dto;


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
    
