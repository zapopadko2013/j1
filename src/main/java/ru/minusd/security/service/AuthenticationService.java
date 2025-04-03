package ru.minusd.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.minusd.security.domain.dto.JwtAuthenticationResponse;
import ru.minusd.security.domain.dto.SignInRequest;
import ru.minusd.security.domain.dto.SignUpRequest;
import ru.minusd.security.domain.model.Role;
import ru.minusd.security.domain.model.User;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    
    private Role getRoleFromString(String role) {
        try {
            // Преобразуем строку в enum
            return Role.valueOf(role.toUpperCase());  // Преобразуем в верхний регистр, чтобы избежать проблем с регистрами
        } catch (IllegalArgumentException e) {
            // Если строка не является допустимым значением enum
            return Role.ROLE_USER; // Значение по умолчанию
        }
    }
    
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        
        //var roles=request.getRole();
        
        ///
        String rolestr = request.getRole() != null  ? request.getRole() : "ROLE_USER";
        Role role=getRoleFromString(rolestr);
        ///        

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                //.role(Role.ROLE_USER)
                .role(role)
                .build();

        userService.create(user);

       // System.out.println("1");
       // System.out.println(user);
        
        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
               // request.getUsername(),
                request.getEmail(),
                request.getPassword()
        ));

        var user = userService
                .userDetailsService()
                //.loadUserByUsername(request.getUsername());
                .loadUserByUsername(request.getEmail());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}
