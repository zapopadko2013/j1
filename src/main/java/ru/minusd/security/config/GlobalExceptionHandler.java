/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.minusd.security.config;

/**
 *
 * @author User
 */
//import ru.minusd.security.config.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        // Создайте объект ошибки с необходимыми деталями
        ErrorResponse errorResponse = new ErrorResponse("Not Found", ex.getMessage());
        // Верните ответ с кодом статуса 404 и деталями ошибки
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    /*
     // Обработка исключения неверного токена
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Invalid Token", "Предоставленный токен недействителен.");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // Обработка исключения истекшего токена
    @ExceptionHandler(OAuth2IntrospectionException.class)
    public ResponseEntity<ErrorResponse> handleOAuth2IntrospectionException(OAuth2IntrospectionException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Expired Token", "Срок действия токена истек.");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    */
}
