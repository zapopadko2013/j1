/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.minusd.security.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.minusd.security.domain.model.Task;

/**
 *
 * @author User
 */
public class TaskSpecification {
    
    public static Specification<Task> filterTasks(Long userAuthorId, Long userExecId, String filterField, String filterValue) {
        return (root, query, criteriaBuilder) -> {
            // Если все параметры null, возвращаем "пустой" запрос (выбираем все записи)
            Predicate predicate = criteriaBuilder.conjunction(); 

            if (userAuthorId != null && userExecId != null) {
                Predicate userAuthorCondition = criteriaBuilder.equal(root.get("userAuthorId"), userAuthorId);
                Predicate userExecCondition = criteriaBuilder.equal(root.get("userExecId"), userExecId);
                predicate = criteriaBuilder.or(userAuthorCondition, userExecCondition); // Логическое "или"
            } else {
                if (userAuthorId != null) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("userAuthorId"), userAuthorId));
                }
                if (userExecId != null) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("userExecId"), userExecId));
                }
            }


            

            if (filterField != null && filterValue != null && !filterField.isEmpty() && !filterValue.isEmpty()) {
                // Добавляем фильтрацию по переданному полю и значению
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get(filterField), "%" + filterValue + "%"));
            }

            return predicate;  // Возвращаем скомпилированный предикат
        };
    }
    
}
