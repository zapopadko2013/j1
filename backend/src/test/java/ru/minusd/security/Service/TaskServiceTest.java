package ru.minusd.security.Service;

//import com.springframeworkguru.Exception.ProductAlreadyExistsException;
import ru.minusd.security.repository.TaskRepository;
import ru.minusd.security.domain.model.Task;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

//handles business logic
    //unit testing so mocking
    @Mock
    private TaskRepository taskRepository;

    @Autowired
    @InjectMocks
    private TaskService taskService;
    private Task task1;
    private Task task2;
    List<Task> taskList;

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

    @Test
   // void givenProductToAddShouldReturnAddedProduct() throws ProductAlreadyExistsException {
    void givenTaskToAddShouldReturnAddedTask() {

        //stubbing
        when(taskRepository.save(any())).thenReturn(task1);
        //taskService.addProduct(task1);
       taskService.createTask(task1);
       verify(taskRepository,times(1)).save(any());

    }


    @Test
    public void GivenGetAllTasksShouldReturnListOfAllTasks(){
    taskRepository.save(task1);
        //stubbing mock to return specific data
        when(taskRepository.findTasks()).thenReturn(taskList);
        List<Task> taskList1 =taskService.getTasks();
        assertEquals(taskList1,taskList);
        verify(taskRepository,times(1)).save(task1);
        verify(taskRepository,times(1)).findTasks();
    }

/*
    @Test
    public void testCreateTask() {
        Task task = new Task();
        task.setName("New task");
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task created = taskService.createTask(task);
        assertEquals("New task", created.getName());
        verify(taskRepository).save(task);
    }

    @Test
    public void testCheckAccess() {
        when(taskRepository.existsByIdAndUserAuthorIdOrUserExecId(new Long(1),new Long(2), new Long(2)))
            .thenReturn(true);

        assertTrue(taskService.checkTasknExists(new Long(1), new Long(2), new Long(2)));
    }
    */

/////////    

@Test
void testGetAllTasks() {
    // Подготовка данных
    List<Task> tasks = Arrays.asList(new Task(), new Task());
    when(taskRepository.findAll()).thenReturn(tasks);

    // Вызов метода
    Iterable<Task> result = taskService.getAllTasks();

    // Проверка
    assertNotNull(result);
    assertEquals(2, ((Collection<Task>) result).size());
}


@Test
void testGetAllTasksEmpty() {
    // Подготовка данных
    when(taskRepository.findAll()).thenReturn(Collections.emptyList());

    // Вызов метода
    Iterable<Task> result = taskService.getAllTasks();

    // Проверка
    assertNotNull(result);
    assertEquals(0, ((Collection<Task>) result).size());
}



@Test
void testGetTaskByIdFound() {
    // Подготовка данных
    Task task = new Task();
    task.setId(1L);
    when(taskRepository.findTaskWithUsersById(1L)).thenReturn(Optional.of(task));

    // Вызов метода
    Optional<Task> result = taskService.getTaskById(1L);

    // Проверка
    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
}

@Test
void testGetTaskByIdNotFound() {
    // Подготовка данных
    when(taskRepository.findTaskWithUsersById(999L)).thenReturn(Optional.empty());

    // Вызов метода
    Optional<Task> result = taskService.getTaskById(999L);

    // Проверка
    assertFalse(result.isPresent());
}


@Test
void testCreateTask() {
    // Подготовка данных
    Task task = new Task();
    task.setId(1L);
    when(taskRepository.save(task)).thenReturn(task);

    // Вызов метода
    Task result = taskService.createTask(task);

    // Проверка
    assertNotNull(result);
    assertEquals(1L, result.getId());
}

@Test
void testCreateTaskInvalid() {
    // Подготовка данных
    Task task = new Task();  // Например, задача без обязательных полей
    when(taskRepository.save(task)).thenThrow(new IllegalArgumentException("Invalid task"));

    // Проверка исключения
    assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task));
}

@Test
void testCheckTaskExists() {
    // Подготовка данных
    when(taskRepository.existsByIdAndUserAuthorId(1L, 1L)).thenReturn(true);

    // Вызов метода
    boolean result = taskService.checkTaskExists(1L, 1L);

    // Проверка
    assertTrue(result);
}

@Test
void testCheckTaskExistsNotFound() {
    // Подготовка данных
    when(taskRepository.existsByIdAndUserAuthorId(1L, 2L)).thenReturn(false);

    // Вызов метода
    boolean result = taskService.checkTaskExists(1L, 2L);

    // Проверка
    assertFalse(result);
}


@Test
void testCheckTasknExists() {
    // Подготовка данных
    when(taskRepository.existsByIdAndUserAuthorIdOrUserExecId(1L, 1L, 2L)).thenReturn(true);

    // Вызов метода
    boolean result = taskService.checkTasknExists(1L, 1L, 2L);

    // Проверка
    assertTrue(result);
}

@Test
void testCheckTasknExistsNotFound() {
    // Подготовка данных
    when(taskRepository.existsByIdAndUserAuthorIdOrUserExecId(1L, 3L, 4L)).thenReturn(false);

    // Вызов метода
    boolean result = taskService.checkTasknExists(1L, 3L, 4L);

    // Проверка
    assertFalse(result);
}


@Test
void testUpdateTask() {
    // Подготовка данных
    Task task = new Task();
    task.setId(1L);
    task.setName("Updated Task");
    when(taskRepository.existsById(1L)).thenReturn(true);
    when(taskRepository.save(task)).thenReturn(task);

    // Вызов метода
    Task updatedTask = taskService.updateTask(1L, task);

    // Проверка
    assertNotNull(updatedTask);
    assertEquals("Updated Task", updatedTask.getName());
}

@Test
void testUpdateTaskNotFound() {
    // Подготовка данных
    Task task = new Task();
    task.setId(999L);
    when(taskRepository.existsById(999L)).thenReturn(false);

    // Вызов метода
    Task updatedTask = taskService.updateTask(999L, task);

    // Проверка
    assertNull(updatedTask);
}

@Test
void testDeleteTask() {
    // Подготовка данных
    when(taskRepository.existsById(1L)).thenReturn(true);

    // Вызов метода
    boolean result = taskService.deleteTask(1L);

    // Проверка
    assertTrue(result);
    verify(taskRepository).deleteById(1L);
}

@Test
void testDeleteTaskNotFound() {
    // Подготовка данных
    when(taskRepository.existsById(999L)).thenReturn(false);

    // Вызов метода
    boolean result = taskService.deleteTask(999L);

    // Проверка
    assertFalse(result);
    verify(taskRepository, never()).deleteById(999L);
}



/*
    @Test
    public void givenIdThenShouldReturnProductOfThatId() {

        Mockito.when(productRepository.findById(1)).thenReturn(Optional.ofNullable(product1));
        assertThat(productService.getProductByid(product1.getId())).isEqualTo(product1);
    }
    */

   // @Test
//    public void givenIdTODeleteThenShouldDeleteTheProduct(){
//        when(productService.deleteProductById(product1.getId())).thenReturn(product1);
//        assertThat(productService.f);
//        verify(productRepository,times(1)).findAll();
//
//   }
}