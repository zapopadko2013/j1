/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ru.minusd.security.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.minusd.security.domain.model.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author User
 */

public interface TaskRepository extends CrudRepository<Task, Long>, JpaSpecificationExecutor<Task>  {

    
    // Запрос для выборки всех задач, где userAuthorId или userExecId соответствуют значению
    //@Query("SELECT t FROM Task t WHERE t.userAuthorId = :userId OR t.userExecId = :userId")
    @Query("SELECT t, ua, ue FROM Task t " +
           "LEFT JOIN User ua ON t.userAuthorId = ua.id " +
           "LEFT JOIN User ue ON t.userExecId = ue.id " +
            " WHERE t.userAuthorId = :userId OR t.userExecId = :userId")
    List<Task> findTasksByUserAuthorIdOrUserExecId(Long userId);
    @Query("SELECT t, ua, ue FROM Task t " +
           "LEFT JOIN User ua ON t.userAuthorId = ua.id " +
           "LEFT JOIN User ue ON t.userExecId = ue.id " )
    List<Task> findTasks();
    boolean existsByIdAndUserAuthorId(Long taskId, Long userAuthorId);
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END FROM Task t WHERE t.id = :taskId AND (t.userAuthorId = :userAuthorId OR t.userExecId = :userExecId)")
    boolean existsByIdAndUserAuthorIdOrUserExecId(Long taskId, Long userAuthorId, Long userExecId);
    @Query("SELECT t, ua, ue FROM Task t " +
           "LEFT JOIN User ua ON t.userAuthorId = ua.id " +
           "LEFT JOIN User ue ON t.userExecId = ue.id " +
           "WHERE t.id = :taskId")
    Optional<Task> findTaskWithUsersById(@Param("taskId") Long taskId);
    
    /*
    @Query("SELECT t, ua, ue FROM Task t " +
           "LEFT JOIN User ua ON t.userAuthorId = ua.id " +
           "LEFT JOIN User ue ON t.userExecId = ue.id ")
    Page<Task> findTasksWithPaginationAndFilter(Specification<Task> specification, Pageable pageable);
    */
    
    
    @Query("SELECT t, ua, ue FROM Task t " +
           "LEFT JOIN User ua ON t.userAuthorId = ua.id " +
           "LEFT JOIN User ue ON t.userExecId = ue.id " +
           //"LEFT JOIN User ua ON t.userAuthorId = ua.id AND (:userAuthorId IS NULL OR t.userAuthorId = :userAuthorId) " +
           //"LEFT JOIN User ue ON t.userExecId = ue.id AND (:userExecId IS NULL OR t.userExecId = :userExecId) " +
           "WHERE ((:userAuthorId IS NULL OR t.userAuthorId = :userAuthorId) " +
           "OR (:userExecId IS NULL OR t.userExecId = :userExecId)) " +
          // "AND (:filterField IS NULL OR t.name LIKE %:filterValue%)") // Пример фильтрации по taskName
          " AND (:filterField IS NULL OR " +
           "       CASE WHEN :filterField = 'name' THEN t.name " +
           "            WHEN :filterField = 'comment' THEN t.comment " +
           "            WHEN :filterField = 'description' THEN t.description " +
           "            WHEN :filterField = 'priority' THEN t.priority " +
           "            WHEN :filterField = 'status' THEN t.status " +
           //"            WHEN :filterField = 'userAuthorId' THEN t.userAuthorId " +
           //"            WHEN :filterField = 'userExecId' THEN t.userExecId " +
           "       END LIKE %:filterValue%)")
    Page<Task> findTasksWithPaginationAndFilter(@Param("userAuthorId") Long userAuthorId,
                                    @Param("userExecId") Long userExecId,
                                    @Param("filterField") String filterField,
                                    @Param("filterValue") String filterValue,
                                    Pageable pageable);

}