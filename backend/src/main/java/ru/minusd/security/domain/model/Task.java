
package ru.minusd.security.domain.model;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;


@Entity
public class Task {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @NotNull(message = "Имя не может быть null")
	private String name;
        @NotNull(message = "Описание задачи не может быть null")
        private String description;
        @NotNull(message = "Статус не может быть null")
        private String status;
        private String priority;
        private String comment;
        //private Long user_author_id;
        
        @NotNull(message = "Автор не может быть null")
        @Column(name = "user_author_id") // Указываем имя поля в базе данных
        private Long userAuthorId;
        
       // private Long user_exec_id;
        
        @NotNull(message = "Исполнитель не может быть null")
        @Column(name = "user_exec_id") // Указываем имя поля в базе данных
        private Long userExecId;
        
       

    
        
    // @JsonIgnore   
        @ManyToOne(fetch = FetchType.LAZY) // Автоматически подгружает пользователя
    @JoinColumn(name = "user_author_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User userAuthor;

    //@JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) // Аналогично для userExecId
    @JoinColumn(name = "user_exec_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User userExec;
   

	public Task() {
	}

	public Task(Long id, String name) {
		this.id = id;
		this.name = name;
	}

        public Task(Long id, String name,String description,Long userAuthorId,Long userExecId) {
		this.id = id;
		this.name = name;
                this.description = description;
                this.userAuthorId = userAuthorId;
                this.userExecId = userExecId;
	}
        

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
        
        public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
        
        public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
        
        public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
        
        public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
        
        public Long getUserAuthorId() {
		return userAuthorId;
	}

	public void setUserAuthorId(Long user_author_id) {
		this.userAuthorId = user_author_id;
	}
        
        public Long getUserExecId() {
		return userExecId;
	}

	public void setUserExecId(Long user_exec_id) {
		this.userExecId = user_exec_id;
	}
        
        public User getUserAuthor() {
        return userAuthor;
    }

    public void setUserAuthor(User userAuthor) {
        this.userAuthor = userAuthor;
    }

    public User getUserExec() {
        return userExec;
    }

    public void setUserExec(User userExec) {
        this.userExec = userExec;
    }
        
        
}
