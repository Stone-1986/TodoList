package co.com.todolist.model.task;

import lombok.Data;

@Data
public class FilterCriteria {
    private String id;
    private String title;
    private String description;
    private Boolean completed;
}