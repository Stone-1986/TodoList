package co.com.todolist.usecase.task;

import co.com.todolist.exceptions.bussiness.BusinessErrorMessages;
import co.com.todolist.exceptions.bussiness.CustomBusinessException;
import co.com.todolist.exceptions.error.ErrorMessage;
import co.com.todolist.model.task.Task;
import co.com.todolist.model.task.TaskUpdate;
import co.com.todolist.model.task.gateways.TaskRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class UpdateTaskUseCase {
    private final TaskRepository taskRepository;

    public Mono<Task> action(String id, TaskUpdate updatedTask) {
        return taskRepository.getTaskById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    ErrorMessage errorMessage = ErrorMessage.builder()
                            .code(BusinessErrorMessages.TASK_NOT_FOUND.getCode())
                            .message("La tarea con el id " + id + " no existe")
                            .build();
                    return Mono.error(new CustomBusinessException(BusinessErrorMessages.TASK_NOT_FOUND,
                            List.of(errorMessage)));
                }))
                .flatMap(existingTask -> {
                    String newTitle = updatedTask.getTitle() != null ? updatedTask.getTitle() : existingTask.getTitle();

                    if (newTitle == null || newTitle.isEmpty()) {
                        ErrorMessage errorMessage = ErrorMessage.builder()
                                .code(BusinessErrorMessages.EMPTY_TITLE.getCode())
                                .message("El titulo de la tarea no puede estar vacio")
                                .build();
                        return Mono.error(new CustomBusinessException(BusinessErrorMessages.EMPTY_TITLE,
                                List.of(errorMessage)));
                    }

                    Task taskToUpdate = existingTask.toBuilder()
                            .title(newTitle)
                            .description(updatedTask.getDescription() != null ? updatedTask.getDescription() : existingTask.getDescription())
                            .completed(updatedTask.getCompleted() != null ? updatedTask.getCompleted() : existingTask.getCompleted())
                            .build();
                    return taskRepository.updateTask(id, taskToUpdate);
                });
    }
}
