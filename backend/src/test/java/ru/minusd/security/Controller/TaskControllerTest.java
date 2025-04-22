package ru.minusd.security.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import ru.minusd.security.controller.TaskController;
import ru.minusd.security.domain.model.Role;
import ru.minusd.security.domain.model.Task;
import ru.minusd.security.domain.model.User;
import ru.minusd.security.service.TaskService;
import ru.minusd.security.service.UserService;
import ru.minusd.security.domain.model.TaskMapper;
import ru.minusd.security.domain.model.TaskRequestDto;
import ru.minusd.security.domain.model.TaskResponseDto;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import ru.minusd.security.repository.TaskRepository;
import ru.minusd.security.domain.model.Task;
import ru.minusd.security.domain.model.Task;
import ru.minusd.security.domain.model.User;
import ru.minusd.security.service.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.data.domain.Page;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.minusd.security.domain.dto.SuccessResponse;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    private User mockAdmin;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockAdmin = new User();
        mockAdmin.setId(1L);
        mockAdmin.setRole(Role.ROLE_ADMIN);

        mockUser = new User();
        mockUser.setId(2L);
        mockUser.setRole(Role.ROLE_USER);
    }

    ///////админ запрашивает с фильтром
    @Test
    void testGetTasks_AsAdmin() {
        // given
        when(userService.getCurrentUser()).thenReturn(mockAdmin);
        when(userService.getRole()).thenReturn(Role.ROLE_ADMIN);

        Page<Task> mockPage = new PageImpl<>(Collections.singletonList(new Task()));
        when(taskService.getFilteredTasks(null, null, "status", "open", 0, 10)).thenReturn(mockPage);

        // when
        Page<Task> result = taskController.getTasks("status", "open", 0, 10);

        // then
        assertEquals(1, result.getContent().size());
        verify(taskService).getFilteredTasks(null, null, "status", "open", 0, 10);
    }

    ///////обычный пользователь запрашивает свои задачи
    @Test
    void testGetTasks_AsUser() {
        // given
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(userService.getRole()).thenReturn(Role.ROLE_USER);
        when(userService.getId()).thenReturn(mockUser.getId());

        Page<Task> mockPage = new PageImpl<>(Collections.singletonList(new Task()));
        when(taskService.getFilteredTasks(2L, 2L, "priority", "high", 1, 5)).thenReturn(mockPage);

        // when
        Page<Task> result = taskController.getTasks("priority", "high", 1, 5);

        // then
        assertEquals(1, result.getContent().size());
        verify(taskService).getFilteredTasks(2L, 2L, "priority", "high", 1, 5);
    }

//////некорректные параметры фильтра
    @Test
void getTasks_InvalidParams_StillReturnsEmptyPage() {
    when(userService.getCurrentUser()).thenReturn(new User());
    when(userService.getRole()).thenReturn(Role.ROLE_ADMIN);

    // Некорректные поля, но сервис должен просто вернуть пустой список
    Page<Task> emptyPage = new PageImpl<>(Collections.emptyList());
    when(taskService.getFilteredTasks(null, null, "wrongField", "???", 0, 10))
            .thenReturn(emptyPage);

    Page<Task> result = taskController.getTasks("wrongField", "???", 0, 10);

    assertNotNull(result);
    assertEquals(0, result.getTotalElements());
}

////////задача найдена, доступ разрешён
@Test
void getTaskById_UserTaskExists_ReturnsSuccessResponse() {
    Long taskId = 1L;
    Task mockTask = new Task(); // Настрой при необходимости
    mockTask.setId(taskId);

    when(userService.getCurrentUser()).thenReturn(new User());
    when(userService.getRole()).thenReturn(Role.ROLE_USER);
    when(userService.getId()).thenReturn(100L);
    when(taskService.checkTasknExists(taskId, 100L, 100L)).thenReturn(true);
    when(taskService.getTaskById(taskId)).thenReturn(Optional.of(mockTask));

    ResponseEntity<?> response = taskController.getTaskById(taskId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody() instanceof SuccessResponse);
    SuccessResponse body = (SuccessResponse) response.getBody();
    assertEquals("Задача найдена", body.getMessage());
    assertEquals(mockTask, body.getTask()); // Используем getTask() вместо getData()
}

//////у пользователя нет доступа к задаче (или не существует)
@Test
void getTaskById_UserNoAccessOrNotFound_ReturnsBadRequest() {
    Long taskId = 999L;

    when(userService.getCurrentUser()).thenReturn(new User());
    when(userService.getRole()).thenReturn(Role.ROLE_USER);
    when(userService.getId()).thenReturn(200L);
    when(taskService.checkTasknExists(taskId, 200L, 200L)).thenReturn(false);

    ResponseEntity<?> response = taskController.getTaskById(taskId);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof SuccessResponse);
    SuccessResponse body = (SuccessResponse) response.getBody();
    assertEquals("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа", body.getMessage());
    assertNull(body.getTask()); // Используем getTask(), который возвращает null
}

//////задача не найдена даже у админа (возврат 404)
@Test
void getTaskById_AdminTaskNotFound_ReturnsNotFound() {
    Long taskId = 10L;

    when(userService.getCurrentUser()).thenReturn(new User());
    when(userService.getRole()).thenReturn(Role.ROLE_ADMIN);
    when(taskService.getTaskById(taskId)).thenReturn(Optional.empty());

    ResponseEntity<?> response = taskController.getTaskById(taskId);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(response.getBody() instanceof SuccessResponse);
    SuccessResponse body = (SuccessResponse) response.getBody();
    assertEquals("Задача не найдена", body.getMessage());
    assertNull(body.getTask()); // Используем getTask(), который возвращает null
}

//////Успешное создание задачи
@Test
void createTask_ValidRequest_ReturnsCreatedTask() {
    // Arrange
    TaskRequestDto dto = new TaskRequestDto();
    dto.setName("Test Task");
    dto.setStatus("NEW");
    dto.setDescription("Description of the task");
    dto.setUserExecId(2L);

    Task taskEntity = new Task();
    taskEntity.setName(dto.getName());
    taskEntity.setStatus(dto.getStatus());
    taskEntity.setDescription(dto.getDescription());
    taskEntity.setUserExecId(dto.getUserExecId());
    taskEntity.setUserAuthorId(1L); // Установится в контроллере через userserv.getId()

    Task createdTask = new Task();
    createdTask.setId(1L);
    createdTask.setName("Test Task");
    createdTask.setStatus("NEW");
    createdTask.setDescription("Description of the task");
    createdTask.setUserExecId(2L);
    createdTask.setUserAuthorId(1L);

    User mockUser = new User();
    mockUser.setId(1L);

    // Мокаем поведение
    when(userService.getCurrentUser()).thenReturn(mockUser);
    when(userService.getId()).thenReturn(1L);
    when(taskService.createTask(any(Task.class))).thenReturn(createdTask);

    // Act
    ResponseEntity<?> response = taskController.createTask(dto);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertTrue(response.getBody() instanceof Task);
    Task responseBody = (Task) response.getBody();
    assertEquals(createdTask.getId(), responseBody.getId());
    assertEquals(createdTask.getName(), responseBody.getName());
    assertEquals(createdTask.getStatus(), responseBody.getStatus());
    assertEquals(createdTask.getDescription(), responseBody.getDescription());
    assertEquals(createdTask.getUserAuthorId(), responseBody.getUserAuthorId());
    assertEquals(createdTask.getUserExecId(), responseBody.getUserExecId());

    // Проверка, что сервис вызван
    verify(taskService).createTask(any(Task.class));
}

///////Задача успешно обновлена
@Test
void updateTask_ValidRequest_ReturnsUpdatedTask() {
    // Arrange
    Long taskId = 1L;

    // Исходящий DTO от клиента
    TaskRequestDto dto = new TaskRequestDto();
    dto.setName("Updated Task");
    dto.setStatus("IN_PROGRESS");
    dto.setDescription("Updated Description");
    dto.setUserAuthorId(100L);
    dto.setUserExecId(200L);

    // Ожидаемое обновлённое Task
    Task updatedTask = new Task();
    updatedTask.setId(taskId);
    updatedTask.setName("Updated Task");
    updatedTask.setStatus("IN_PROGRESS");
    updatedTask.setDescription("Updated Description");
    updatedTask.setUserAuthorId(100L);
    updatedTask.setUserExecId(200L);

    // Настроим моки
    when(userService.getCurrentUser()).thenReturn(mockUser);
    when(userService.getId()).thenReturn(100L);
    when(userService.getRole()).thenReturn(Role.ROLE_USER);
    when(taskService.checkTasknExists(taskId, 100L, 100L)).thenReturn(true);
    when(taskService.updateTask(eq(taskId), any(Task.class))).thenReturn(updatedTask);

    // Act
    ResponseEntity<?> response = taskController.updateTask(taskId, dto);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody() instanceof Task);
    Task responseBody = (Task) response.getBody();
    assertEquals("Updated Task", responseBody.getName());
    assertEquals("IN_PROGRESS", responseBody.getStatus());
}

///////Задача с таким ID не найдена
@Test
void updateTask_TaskNotFound_ReturnsBadRequest() {
    Long taskId = 999L;
    TaskRequestDto dto = new TaskRequestDto();
    dto.setName("Updated Task");

    when(userService.getCurrentUser()).thenReturn(mockUser);
    when(userService.getId()).thenReturn(100L);
    when(userService.getRole()).thenReturn(Role.ROLE_USER);
    when(taskService.checkTasknExists(taskId, 100L, 100L)).thenReturn(false);

    ResponseEntity<?> response = taskController.updateTask(taskId, dto);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof SuccessResponse);
    SuccessResponse body = (SuccessResponse) response.getBody();
    assertEquals("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа", body.getMessage());
}

/////Пользователь не имеет доступа к задаче
@Test
void updateTask_UserNoAccess_ReturnsBadRequest() {
    Long taskId = 2L;
    TaskRequestDto dto = new TaskRequestDto();
    dto.setName("Updated Task");

    when(userService.getCurrentUser()).thenReturn(mockUser);
    when(userService.getId()).thenReturn(100L);
    when(userService.getRole()).thenReturn(Role.ROLE_USER);
    when(taskService.checkTasknExists(taskId, 100L, 100L)).thenReturn(false);

    ResponseEntity<?> response = taskController.updateTask(taskId, dto);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof SuccessResponse);
    SuccessResponse body = (SuccessResponse) response.getBody();
    assertEquals("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа", body.getMessage());
}


/////Обновление задачи, когда задача не может быть обновлена
@Test
void updateTask_UpdateFailed_ReturnsBadRequest() {
    Long taskId = 1L;
    TaskRequestDto dto = new TaskRequestDto();
    dto.setName("Updated Task");

    when(userService.getCurrentUser()).thenReturn(mockUser);
    when(userService.getId()).thenReturn(100L);
    when(userService.getRole()).thenReturn(Role.ROLE_USER);
    when(taskService.checkTasknExists(taskId, 100L, 100L)).thenReturn(true);
    when(taskService.updateTask(eq(taskId), any(Task.class))).thenReturn(null);

    ResponseEntity<?> response = taskController.updateTask(taskId, dto);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof SuccessResponse);
    SuccessResponse body = (SuccessResponse) response.getBody();
    assertEquals("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа", body.getMessage());
}



//////Удаление задачи успешно
@Test
void deleteTask_TaskExistsAndDeleted_ReturnsSuccessResponse() {
    Long taskId = 1L;

    when(userService.getCurrentUser()).thenReturn(new User());
    when(userService.getId()).thenReturn(100L);
    when(userService.getRole()).thenReturn(Role.ROLE_USER);
    when(taskService.checkTasknExists(taskId, 100L, 100L)).thenReturn(true);
    when(taskService.checkTaskExists(taskId, 100L)).thenReturn(true);
    when(taskService.deleteTask(taskId)).thenReturn(true);

    ResponseEntity<?> response = taskController.deleteTask(taskId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody() instanceof SuccessResponse);
    SuccessResponse responseBody = (SuccessResponse) response.getBody();
    assertEquals("Задача удалена!", responseBody.getMessage());
}

//////Задача не найдена (пользователь не имеет доступа)
@Test
void deleteTask_TaskNotFound_ReturnsBadRequest() {
    Long taskId = 999L;

    when(userService.getCurrentUser()).thenReturn(new User());
    when(userService.getId()).thenReturn(100L);
    when(userService.getRole()).thenReturn(Role.ROLE_USER);
    when(taskService.checkTasknExists(taskId, 100L, 100L)).thenReturn(false);

    ResponseEntity<?> response = taskController.deleteTask(taskId);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof SuccessResponse);
    SuccessResponse responseBody = (SuccessResponse) response.getBody();
    assertEquals("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа", responseBody.getMessage());
}

////Задача существует, но пользователь не имеет прав на удаление
@Test
void deleteTask_UserNoPermission_ReturnsBadRequest() {
    Long taskId = 2L;

    when(userService.getCurrentUser()).thenReturn(new User());
    when(userService.getId()).thenReturn(100L);
    when(userService.getRole()).thenReturn(Role.ROLE_USER);
    when(taskService.checkTasknExists(taskId, 100L, 100L)).thenReturn(true);
    when(taskService.checkTaskExists(taskId, 100L)).thenReturn(false);

    ResponseEntity<?> response = taskController.deleteTask(taskId);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof SuccessResponse);
    SuccessResponse responseBody = (SuccessResponse) response.getBody();
    assertEquals("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа", responseBody.getMessage());
}


////Удаление задачи не удалось (ошибка удаления)
@Test
void deleteTask_DeleteFailed_ReturnsBadRequest() {
    Long taskId = 1L;

    when(userService.getCurrentUser()).thenReturn(new User());
    when(userService.getId()).thenReturn(100L);
    when(userService.getRole()).thenReturn(Role.ROLE_USER);
    when(taskService.checkTasknExists(taskId, 100L, 100L)).thenReturn(true);
    when(taskService.checkTaskExists(taskId, 100L)).thenReturn(true);
    when(taskService.deleteTask(taskId)).thenReturn(false);

    ResponseEntity<?> response = taskController.deleteTask(taskId);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody() instanceof SuccessResponse);
    SuccessResponse responseBody = (SuccessResponse) response.getBody();
    assertEquals("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа", responseBody.getMessage());
}

}
