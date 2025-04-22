package ru.minusd.security.Repository;

//import com.springframeworkguru.Exception.ProductAlreadyExistsException;
import ru.minusd.security.repository.TaskRepository;
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

@ExtendWith(MockitoExtension.class)
class TaskRepositoryTest {

//handles business logic
    //unit testing so mocking
    @Mock
    private TaskRepository taskRepository;

    @Autowired
    @InjectMocks
    private TaskService taskService;

    /*
    private Task task1;
    private Task task2;
    List<Task> taskList;
    */

    /*

    @BeforeEach
    public void setUp() {
        taskList = new ArrayList<>();
        task1 = new Task(new Long(1), "bread");
        task2 = new Task(new Long(2), "jam");
        taskList.add(task1);
        taskList.add(task2);
    }

    @AfterEach
    public void tearDown() {
        task1 = task2 = null;
        taskList = null;
    }
*/

 @Test
void testFindTasksByUserAuthorIdOrUserExecId() {
    Long userId = 1L;

    Task task = new Task();
    task.setId(1L);
    task.setUserAuthorId(userId);
    task.setUserExecId(2L); // <--- добавь эту строку, иначе будет null

    List<Task> mockResult = List.of(task);

    when(taskRepository.findTasksByUserAuthorIdOrUserExecId(userId)).thenReturn(mockResult);

    List<Task> result = taskRepository.findTasksByUserAuthorIdOrUserExecId(userId);

    assertNotNull(result);
    assertFalse(result.isEmpty());

    // безопасное сравнение
    assertTrue(result.stream().anyMatch(t ->
        Objects.equals(t.getUserAuthorId(), userId) ||
        Objects.equals(t.getUserExecId(), userId)
    ));
}

@Test
void testFindTasks() {
    // Подготовка мок-данных
    Task task1 = new Task();
    task1.setId(1L);
    
    Task task2 = new Task();
    task2.setId(2L);
    
    // Задайте поведение мок-репозитория
    when(taskRepository.findTasks()).thenReturn(Arrays.asList(task1, task2));
    
    // Вызовите метод
    List<Task> tasks = taskRepository.findTasks();

    // Проверьте, что возвращаются все задачи
    assertNotNull(tasks);
    assertEquals(2, tasks.size());
}

@Test
void testExistsByIdAndUserAuthorId() {
    Long taskId = 1L;
    Long userAuthorId = 1L;

    // Задайте поведение мок-репозитория
    when(taskRepository.existsByIdAndUserAuthorId(taskId, userAuthorId)).thenReturn(true);

    // Вызовите метод
    boolean exists = taskRepository.existsByIdAndUserAuthorId(taskId, userAuthorId);

    // Проверьте, что метод возвращает true
    assertTrue(exists);
}

@Test
void testExistsByIdAndUserAuthorIdOrUserExecId() {
    Long taskId = 1L;
    Long userAuthorId = 1L;
    Long userExecId = 2L;

    // Задайте поведение мок-репозитория
    when(taskRepository.existsByIdAndUserAuthorIdOrUserExecId(taskId, userAuthorId, userExecId))
        .thenReturn(true);

    // Вызовите метод
    boolean exists = taskRepository.existsByIdAndUserAuthorIdOrUserExecId(taskId, userAuthorId, userExecId);

    // Проверьте, что метод возвращает true
    assertTrue(exists);
}

@Test
void testFindTaskWithUsersById() {
    Long taskId = 1L;

    // Создайте мок-данные
    Task task = new Task();
    task.setId(taskId);
    task.setName("Test Task");
    
    User userAuthor = new User();
    userAuthor.setId(1L);
    User userExec = new User();
    userExec.setId(2L);

    // Мокирование репозитория
    when(taskRepository.findTaskWithUsersById(taskId))
        .thenReturn(Optional.of(task));

    // Вызовите метод
    Optional<Task> result = taskRepository.findTaskWithUsersById(taskId);

    // Проверьте, что задача найдена
    assertTrue(result.isPresent());
    assertEquals(taskId, result.get().getId());
}

@Test
void testFindTasksWithPaginationAndFilter() {
    Long userAuthorId = 1L;
    Long userExecId = 2L;
    String filterField = "name";
    String filterValue = "Test Task";
    Pageable pageable = PageRequest.of(0, 10);

    // Создайте мок-данные
    Task task1 = new Task();
    task1.setId(1L);
    task1.setName("Test Task");

    Page<Task> page = new PageImpl<>(Arrays.asList(task1), pageable, 1);

    // Мокирование репозитория
    when(taskRepository.findTasksWithPaginationAndFilter(userAuthorId, userExecId, filterField, filterValue, pageable))
        .thenReturn(page);

    // Вызовите метод
    Page<Task> tasksPage = taskRepository.findTasksWithPaginationAndFilter(userAuthorId, userExecId, filterField, filterValue, pageable);

    // Проверьте, что возвращается правильная страница с задачами
    assertNotNull(tasksPage);
    assertEquals(1, tasksPage.getTotalElements());
    assertEquals("Test Task", tasksPage.getContent().get(0).getName());
}

   
}