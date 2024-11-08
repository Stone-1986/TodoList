package co.com.todolist.usecase.task;

import co.com.todolist.model.task.Task;
import co.com.todolist.model.task.gateways.TaskRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ToggleTaskStatusUseCase {
    private final TaskRepository taskRepository;

    public Mono<Task> action(String id) {
        return taskRepository.getTaskById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La tarea con el id " + id + " no existe")))
                .flatMap(existingTask -> {
                    Task toggledTask = existingTask.toBuilder()
                            .completed(!existingTask.getCompleted())
                            .build();
                    return taskRepository.updateTask(id, toggledTask);
                });
    }
}
