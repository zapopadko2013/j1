/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.minusd.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.web.ErrorResponse;
import ru.minusd.security.config.ErrorResponse;

/**
 *
 * @author User
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //ErrorResponse errorResponse = new ErrorResponse("Unauthorized", authException.getMessage());
        //System.out.println("auth");
        //System.out.println(authException.getMessage());
        /*
        ErrorResponse errorResponse = new ErrorResponse("Unauthorized", "Не авторизован."+authException.getMessage());
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
        
        */
        
        if (authException.getMessage().contains("Bad credentials")) {
           //response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse = new ErrorResponse("Unauthorized", "Неверный токен. " + authException.getMessage());
            objectMapper.writeValue(response.getOutputStream(), errorResponse);
        } else {
            // Для других случаев, например, отсутствие токена или общая ошибка аутентификации
           // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse = new ErrorResponse("Unauthorized", "Не авторизован: " + authException.getMessage());
            objectMapper.writeValue(response.getOutputStream(), errorResponse);
        }
    }
}
