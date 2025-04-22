package ru.minusd.security.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификацию")
public class SignInRequest {

    
    
    @Schema(description = "Почта", example = "jondoe3457a@gmail.com")
    @Size(min = 1, max = 250, message = "Почта должно содержать от 1 до 250 символов")
    @NotBlank(message = "Почта не может быть пустыми")
    private String email;

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(min = 1, max = 255, message = "Длина пароля должна быть от 1 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password;
}