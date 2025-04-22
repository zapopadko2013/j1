
package ru.minusd.security.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.minusd.security.repository.TaskRepository;
import ru.minusd.security.domain.model.Task;
import ru.minusd.security.domain.model.TaskMapper;
import ru.minusd.security.domain.model.TaskRequestDto;
import ru.minusd.security.domain.model.TaskResponseDto;
import ru.minusd.security.service.UserService;
import org.springframework.boot.SpringApplication;
import ru.minusd.security.service.TaskService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.minusd.security.domain.dto.ErrorResponsen;
import ru.minusd.security.domain.dto.SuccessResponse;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import static org.springframework.http.ResponseEntity.ok;
import ru.minusd.security.domain.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Tag(name = "Задачи", description = "Запросы по задачам")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    @Autowired    
    private  UserService userserv;

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    
    
    
   
    
    @GetMapping("/filter")
    @Operation(summary = "Фильтрация задач")
    public Page<Task> getTasks(@RequestParam(required = false) String filterField, // Динамическое поле
                               @RequestParam(required = false) String filterValue, // Значение для фильтрации
                               @RequestParam int page,
                               @RequestParam int size) {
        
        logger.info("Получение данных по задачам с фильтрацией и пагинацией: пользователь={}, filterField={},filterValue={},page={},size={},",userserv.getCurrentUser(), filterField,filterValue,page,size);
         
        if (userserv.getRole()==Role.ROLE_ADMIN)
            return taskService.getFilteredTasks(null, null, filterField, filterValue, page, size);
        else
            return taskService.getFilteredTasks(userserv.getId(), userserv.getId(), filterField, filterValue, page, size);
    }

    @GetMapping
    @Operation(summary = "Получение всех задач")
    public Iterable<Task> getAllTaks() {
        
        logger.info("Получение данных по задачам : пользователь={}",userserv.getCurrentUser());
       
        if (userserv.getRole()==Role.ROLE_ADMIN)
             return taskService.getTasks();
        else {
         return taskService.getTasksByUserId(userserv.getId());
            }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение задачи по id")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        
        logger.info("Получение данных по id задачи : пользователь={},id={}",userserv.getCurrentUser(),id);
        
        if (userserv.getRole()==Role.ROLE_USER) {
          if (taskService.checkTasknExists(id,userserv.getId(),userserv.getId())==false) {
              
              logger.info("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа: пользователь={},id={}",userserv.getCurrentUser(),id);
        
               SuccessResponse successResponse = new SuccessResponse("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа", null);
              
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(successResponse);
              
              
          }
        }
        
        Optional<Task> task = taskService.getTaskById(id);
       
    
    
      return task.map(t -> {
                    SuccessResponse successResponse = new SuccessResponse("Задача найдена", t);  // Создаем SuccessResponse
                    return ResponseEntity.ok(successResponse);  // Возвращаем ResponseEntity с успешным ответом
                })
                .orElseGet(() -> {
                    SuccessResponse successResponse = new SuccessResponse("Задача не найдена", null);
                     return ResponseEntity.status(404).body(successResponse);
                 });
     
     
    }

    @PostMapping
    @Operation(summary = "Создание задачи")
    //public ResponseEntity<?> createTask(@RequestBody @Valid Task task) {
    public ResponseEntity<?> createTask(@RequestBody @Valid TaskRequestDto dto) {

       Task task = TaskMapper.toEntity(dto);
        
        logger.info("Cоздание задачи : пользователь={},task={}",userserv.getCurrentUser(),task);

        if (task.getName()==null) {
           
           logger.info("При изменении задачи, нет имени задачи");
          
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessResponse("При изменении задачи, нет имени задачи", null));
             
         }
        
        
        
        task.setUserAuthorId(userserv.getId());
        System.out.println("UserId");
        System.out.println(userserv.getId());
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменение задачи")
    //public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody @Valid Task task) {
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody @Valid TaskRequestDto dto) {
     
         Task task = TaskMapper.toEntity(dto);
         logger.info("Изменение задачи по id : пользователь={},id={},task={}",userserv.getCurrentUser(),id,task);
        
         
         
         SuccessResponse successResponse = new SuccessResponse("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа", null);
        
        
        if (userserv.getRole()==Role.ROLE_USER) {
          if (taskService.checkTasknExists(id,userserv.getId(),userserv.getId())==false) {
               logger.info("При изменении задачи, задачи с id таким не найдены для пользователя или к ним у пользователя нет длступа : пользователь={},id={},task={}",userserv.getCurrentUser(),id,task);
        
               
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(successResponse);
              }
        }
              
        Task updatedTask = taskService.updateTask(id, task);
        return updatedTask != null ? ResponseEntity.ok(updatedTask) :
             ResponseEntity.status(HttpStatus.BAD_REQUEST).body(successResponse);
              
               
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление задачи")
   public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        
        logger.info("Удаление задачи по id : пользователь={},id={}",userserv.getCurrentUser(),id);
        
         SuccessResponse successResponse = new SuccessResponse("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа", null);
        
        
        if (userserv.getRole()==Role.ROLE_USER) {
          if (taskService.checkTasknExists(id,userserv.getId(),userserv.getId())==false) {
              
              logger.info("Задачи с id таким не найдены для пользователя или к ним у пользователя нет длступа: пользователь={},id={}",userserv.getCurrentUser(),id);
        
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(successResponse);
          }
          if (taskService.checkTaskExists(id,userserv.getId())==false) {
              
              logger.info("Задачи с id таким не найдены для пользователя или к ним у пользователя нет длступа: пользователь={},id={}",userserv.getCurrentUser(),id);
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(successResponse);
          }
        }
        
        SuccessResponse successResponse1 = new SuccessResponse("Задача удалена!", null);
        
        
        boolean deleted = taskService.deleteTask(id);
        
             
        
        
        return deleted ? 
               ResponseEntity.status(HttpStatus.OK).body(successResponse1)
                : 
                 ResponseEntity.status(HttpStatus.BAD_REQUEST).body(successResponse);
        
       
    }
    
    
}
