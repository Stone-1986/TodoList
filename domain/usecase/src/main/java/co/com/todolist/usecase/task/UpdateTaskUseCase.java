package co.com.todolist.usecase.task;

import co.com.todolist.model.task.Task;
import co.com.todolist.model.task.TaskUpdate;
import co.com.todolist.model.task.gateways.TaskRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateTaskUseCase {
    private final TaskRepository taskRepository;

    public Mono<Task> action(String id, TaskUpdate updatedTask) {
        return taskRepository.getTaskById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La tarea con el id " + id + " no existe")))
                .flatMap(existingTask -> {
                    Task taskToUpdate = existingTask.toBuilder()
                            .title(updatedTask.getTitle() != null ? updatedTask.getTitle() : existingTask.getTitle())
                            .description(updatedTask.getDescription() != null ? updatedTask.getDescription() : existingTask.getDescription())
                            .completed(updatedTask.getCompleted() != null ? updatedTask.getCompleted() : existingTask.getCompleted())
                            .build();
                    return taskRepository.updateTask(id, taskToUpdate);
                });
    }
}
