package co.com.todolist.model.task;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Task {
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String title;
    private String description;
    private Boolean completed;
}
