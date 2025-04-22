
package ru.minusd.security.domain.dto;
import ru.minusd.security.domain.model.Task;
import com.fasterxml.jackson.annotation.JsonProperty;


public class SuccessResponse {
    
    @JsonProperty("message")
       private String message;
     @JsonProperty("task")
        private Task task;

        public SuccessResponse(String message, Task task) {
            this.message = message;
            this.task = task;
        }

        public String getMessage() {
            return message;
        }

        public Task getTask() {
            return task;
        }
    }

