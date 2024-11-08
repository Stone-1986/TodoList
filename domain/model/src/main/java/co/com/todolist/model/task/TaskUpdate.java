package co.com.todolist.model.task;

import lombok.Data;

@Data
public class TaskUpdate {
    private String title;
    private String description;
    private Boolean completed;
}
