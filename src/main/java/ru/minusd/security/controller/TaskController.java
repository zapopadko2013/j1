/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.minusd.security.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.minusd.security.repository.TaskRepository;
import ru.minusd.security.domain.model.Task;
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


/**
 *
 * @author User
 */

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
    
    /*        
    private  Role  getRole(){
        
        var user=userserv.getCurrentUser();
        return user.getRole();
        
    }    
    
    private  Long  getId(){
        
        var user=userserv.getCurrentUser();
        return user.getId();
        
    }  
    */
    
    /*
    @GetMapping("/secured-endpoint")
    public String getSecuredData(@RequestHeader("Authorization") String token) {
        // Логика обработки запроса с использованием токена
        return "Secure data accessed with token: " + token;
    }

    @GetMapping("/another-secured-endpoint")
    public String getAnotherSecuredData(@RequestHeader("Authorization") String token) {
        // Логика обработки запроса с использованием токена
        return "Another secure data accessed with token: " + token;
    }
    */
    
    @GetMapping("/filter")
    @Operation(summary = "Фильтрация задач")
    public Page<Task> getTasks(@RequestParam(required = false) String filterField, // Динамическое поле
                               @RequestParam(required = false) String filterValue, // Значение для фильтрации
                               @RequestParam int page,
                               @RequestParam int size) {
        
        logger.info("Получение данных по задачам с фильтрацией и пагинацией: пользователь={}, filterField={},filterValue={},page={},size={},",userserv.getCurrentUser(), filterField,filterValue,page,size);
       //logger.info("Received request to greet: name={}, age={}", name, age);
        
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
        //return taskService.getAllTasks();
            return taskService.getTasks();
        else {
          //return taskService.getAllTasks();
          return taskService.getTasksByUserId(userserv.getId());
        //  long i=1;
       //   Optional<Task> user = taskService.getTaskById(i);
       //return (Iterable<Task>) user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение задачи по id")
    //public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        
        logger.info("Получение данных по id задачи : пользователь={},id={}",userserv.getCurrentUser(),id);
        
        if (userserv.getRole()==Role.ROLE_USER) {
          if (taskService.checkTasknExists(id,userserv.getId(),userserv.getId())==false) {
              
              logger.info("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа: пользователь={},id={}",userserv.getCurrentUser(),id);
        
              //return ResponseEntity.notFound().build();
              SuccessResponse successResponse = new SuccessResponse("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа", null);
              
             // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа");
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(successResponse);
              
              
          }
        }
        
        Optional<Task> task = taskService.getTaskById(id);
       
       // return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    //   return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа"));
     /*
      return user.map(u -> ResponseEntity.ok("User found: " + u.getName()))  // Если пользователь найден
           .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                                         .body("User not found"));
      */
    
      return task.map(t -> {
                    SuccessResponse successResponse = new SuccessResponse("Задача найдена", t);  // Создаем SuccessResponse
                    return ResponseEntity.ok(successResponse);  // Возвращаем ResponseEntity с успешным ответом
                })
                .orElseGet(() -> {
                    SuccessResponse successResponse = new SuccessResponse("Задача не найдена", null);
                    // return ResponseEntity("Task not found",404);
                    return ResponseEntity.status(404).body(successResponse);
                  //  ErrorResponsen errorResponsen = new ErrorResponsen("Task not found", id);  // Создаем ErrorResponse
                  // return ResponseEntity.status(404).body(errorResponsen);
                  // return ResponseEntity.status(404).body((Object)new ErrorResponsen("Task not found", id));  // Возвращаем ResponseEntity с ошибкой
                });
     
     /*
      return task.map(t -> ResponseEntity.ok(new SuccessResponse("Task found", t))) // Возвращаем задачу и сообщение
              //   .orElseGet(() -> ResponseEntity.status(404)); // Сообщение и id
               .orElseGet(() -> ResponseEntity.status(404).body(new ErrorResponse("Task not found", id))); // Сообщение и id
     
      *//*
      return user.map(t -> ResponseEntity.ok().body("Task found"))
                   .orElseGet(() -> ResponseEntity.status(404).body("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа"));
    */
    }

    @PostMapping
    @Operation(summary = "Создание задачи")
    public ResponseEntity<?> createTask(@RequestBody @Valid Task task) {
        
        logger.info("Cоздание задачи : пользователь={},task={}",userserv.getCurrentUser(),task);
        
        /*
        /////
         if (task.getName()==null) {
           
           logger.info("При изменении задачи, нет имени задачи");
          
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessResponse("При изменении задачи, нет имени задачи", null));
             
         }
         if (task.getStatus()==null) {
           
           logger.info("При изменении задачи, нет статуса задачи");
          
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessResponse("При изменении задачи, нет статуса задачи", null));
             
         }
         if (task.getDescription()==null) {
           
           logger.info("При изменении задачи, нет описания задачи");
          
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessResponse("При изменении задачи, нет описания задачи", null));
             
         }
         if (task.getUserAuthorId()==null) {
           
           logger.info("При изменении задачи, нет автора задачи");
          
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessResponse("При изменении задачи, нет автора задачи", null));
             
         }
         if (task.getUserExecId()==null) {
           
           logger.info("При изменении задачи, нет исполнителя задачи");
          
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessResponse("При изменении задачи, нет исполнителя задачи", null));
             
         }
         /////
        */
        
        task.setUserAuthorId(userserv.getId());
        System.out.println("UserId");
        System.out.println(userserv.getId());
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменение задачи")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody @Valid Task task) {
        
         logger.info("Изменение задачи по id : пользователь={},id={},task={}",userserv.getCurrentUser(),id,task);
        
         /*
         /////
         if (task.getName()==null) {
           
           logger.info("При изменении задачи, нет имени задачи");
          
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessResponse("При изменении задачи, нет имени задачи", null));
             
         }
         if (task.getStatus()==null) {
           
           logger.info("При изменении задачи, нет статуса задачи");
          
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessResponse("При изменении задачи, нет статуса задачи", null));
             
         }
         if (task.getDescription()==null) {
           
           logger.info("При изменении задачи, нет описания задачи");
          
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessResponse("При изменении задачи, нет описания задачи", null));
             
         }
         if (task.getUserAuthorId()==null) {
           
           logger.info("При изменении задачи, нет автора задачи");
          
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessResponse("При изменении задачи, нет автора задачи", null));
             
         }
         if (task.getUserExecId()==null) {
           
           logger.info("При изменении задачи, нет исполнителя задачи");
          
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SuccessResponse("При изменении задачи, нет исполнителя задачи", null));
             
         }
         /////
         */
         
         SuccessResponse successResponse = new SuccessResponse("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа", null);
        
        
        if (userserv.getRole()==Role.ROLE_USER) {
          if (taskService.checkTasknExists(id,userserv.getId(),userserv.getId())==false) {
               logger.info("При изменении задачи, задачи с id таким не найдены для пользователя или к ним у пользователя нет длступа : пользователь={},id={},task={}",userserv.getCurrentUser(),id,task);
        
               
             // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа");
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(successResponse);
              //return ResponseEntity.notFound().build();
          }
        }
              
        Task updatedTask = taskService.updateTask(id, task);
        return updatedTask != null ? ResponseEntity.ok(updatedTask) :
             ResponseEntity.status(HttpStatus.BAD_REQUEST).body(successResponse);
               // ResponseEntity.notFound().build();
               
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление задачи")
   // public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        
        logger.info("Удаление задачи по id : пользователь={},id={}",userserv.getCurrentUser(),id);
        
         SuccessResponse successResponse = new SuccessResponse("Задачи с id таким не найдены для пользователя или к ним у пользователя нет доступа", null);
        
        
        if (userserv.getRole()==Role.ROLE_USER) {
          if (taskService.checkTasknExists(id,userserv.getId(),userserv.getId())==false) {
              
              logger.info("Задачи с id таким не найдены для пользователя или к ним у пользователя нет длступа: пользователь={},id={}",userserv.getCurrentUser(),id);
        
             // return ResponseEntity.notFound().build();
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(successResponse);
          }
          if (taskService.checkTaskExists(id,userserv.getId())==false) {
              
              logger.info("Задачи с id таким не найдены для пользователя или к ним у пользователя нет длступа: пользователь={},id={}",userserv.getCurrentUser(),id);
        
              //return ResponseEntity.notFound().build();
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(successResponse);
          }
        }
        
        SuccessResponse successResponse1 = new SuccessResponse("Задача удалена!", null);
        
        
        boolean deleted = taskService.deleteTask(id);
        
        /*
        return deleted ? 
                ResponseEntity.noContent().build() 
               
                : 
                ResponseEntity.notFound().build();
         */        
        
        
        return deleted ? 
               ResponseEntity.status(HttpStatus.OK).body(successResponse1)
                : 
                 ResponseEntity.status(HttpStatus.BAD_REQUEST).body(successResponse);
        
        /*
        return deleted ? 
                ResponseEntity.noContent().build() 
                //ResponseEntity.status(HttpStatus.OK).body(successResponse1)
                : 
                //ResponseEntity.notFound().build();
                 ResponseEntity.status(HttpStatus.BAD_REQUEST).body(successResponse);
        */
    }
    
    /*
    private final TaskRepository taskRepository;

	public TaskController(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	@GetMapping
	Iterable<Task> getTasks() {
		return taskRepository.findAll();
	}
        
        @GetMapping("/{id}")
	Optional<Task> getTaskById(@PathVariable Long id) {
		return taskRepository.findById(id);
	}

	@PostMapping
	Task postTask(@RequestBody Task task) {
		return taskRepository.save(task);
	}

	@PutMapping("/{id}")
	ResponseEntity<Task> putTask(@PathVariable Long id,@RequestBody Task task) {

		return (taskRepository.existsById(id))
				? new ResponseEntity<>(taskRepository.save(task), HttpStatus.OK)
				: new ResponseEntity<>(taskRepository.save(task), HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	ResponseEntity<Void> deleteTask(@PathVariable Long id) {
            
                Optional<Task> task = taskRepository.findById(id);
            
                if (task == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 404 Not Found
                }
            
		taskRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // 204 No Content, если успешно удален
                
	}
    */
}
