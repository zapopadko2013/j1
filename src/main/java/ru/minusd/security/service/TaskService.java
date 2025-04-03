/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.minusd.security.service;

import java.util.List;
import ru.minusd.security.domain.model.Task;
import ru.minusd.security.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.minusd.security.domain.model.User;
import ru.minusd.security.service.TaskSpecification;

/**
 *
 * @author User
 */

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    public Iterable<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /*
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    */

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }
    
    public boolean checkTaskExists(Long taskId, Long userAuthorId) {
        return taskRepository.existsByIdAndUserAuthorId(taskId, userAuthorId);
    }
    
    // Проверка наличия задачи с определенным taskId, userAuthorId или userExecId
    public boolean checkTasknExists(Long taskId, Long userAuthorId, Long userExecId) {
        return taskRepository.existsByIdAndUserAuthorIdOrUserExecId(taskId, userAuthorId, userExecId);
    }
    
    
    
   
    public Optional<Task> getTaskById(Long taskId) {
        
      /*  
        Optional<Object[]> result = taskRepository.findTaskWithUsersById(taskId);
    
    if (result.isPresent()) {
        Object[] objects = result.get();
        
        Task task = (Task) objects[0];  // Первая часть массива - задача
       // User userAuthor = (User) objects[1];  // Вторая часть массива - автор
       // User userExec = (User) objects[2];  // Третья часть массива - исполнитель

        // Устанавливаем пользователей в задачу
       // task.setUserAuthor(userAuthor);
       // task.setUserExec(userExec);

        return Optional.of(task);
    }
    
    return Optional.empty();  // Если не нашли задачу, возвращаем пустой Optional
    */   
        
        return taskRepository.findTaskWithUsersById(taskId);
    }
   
    public List<Task> getTasks() {
        return taskRepository.findTasks();
    }
    
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findTasksByUserAuthorIdOrUserExecId(userId);
    }
    
    public Page<Task> getFilteredTasks(Long userAuthorId, Long userExecId, String filterField, String filterValue, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Task> specification = TaskSpecification.filterTasks(userAuthorId, userExecId,  filterField, filterValue);
        //return taskRepository.findAll(specification, pageable);
        //return taskRepository.findTasksWithPaginationAndFilter(specification,  pageable);
        return taskRepository.findTasksWithPaginationAndFilter( userAuthorId,  userExecId,  filterField,  filterValue,  pageable);
    }
    

    public Task updateTask(Long id, Task task) {
        if (taskRepository.existsById(id)) {
            task.setId(id);
            return taskRepository.save(task);
        }
        return null;
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
}
