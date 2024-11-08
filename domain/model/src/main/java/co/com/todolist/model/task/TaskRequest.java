package co.com.todolist.model.task;

import lombok.Data;

@Data
public class TaskRequest {
    private String title;
    private String description;
}
