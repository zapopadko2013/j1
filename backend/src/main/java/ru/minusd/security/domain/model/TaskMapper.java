package ru.minusd.security.domain.model;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.minusd.security.domain.model.TaskRequestDto;
import ru.minusd.security.domain.model.TaskResponseDto;
import ru.minusd.security.domain.model.Task;



import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

public class TaskMapper {
    public static TaskResponseDto toResponseDto(Task task) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setComment(task.getComment());


        dto.setUserAuthor(task.getUserAuthor());

        dto.setUserExec(task.getUserExec());
       

       /*
        TaskResponseDto authorDto = new TaskResponseDto();
        authorDto.setId(task.getUserAuthor().getId());
        authorDto.setUsername(task.getUserAuthor().getUsername());
        dto.setUserAuthor(authorDto);

        TaskResponseDto execDto = new TaskResponseDto();
        execDto.setId(task.getUserExec().getId());
        execDto.setUsername(task.getUserExec().getUsername());
        dto.setUserExec(execDto);
        */

        return dto;
    }

    public static Task toEntity(TaskRequestDto dto) {
        Task task = new Task();
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setComment(dto.getComment());
        task.setUserAuthorId(dto.getUserAuthorId());
        task.setUserExecId(dto.getUserExecId());
        return task;
    }
}