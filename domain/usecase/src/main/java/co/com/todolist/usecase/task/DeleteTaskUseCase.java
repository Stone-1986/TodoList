package co.com.todolist.usecase.task;

import co.com.todolist.exceptions.bussiness.BusinessErrorMessages;
import co.com.todolist.exceptions.bussiness.CustomBusinessException;
import co.com.todolist.exceptions.error.ErrorMessage;
import co.com.todolist.model.task.gateways.TaskRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class DeleteTaskUseCase {
    private final TaskRepository taskRepository;

    public Mono<Void> action(String id) {
        return taskRepository.getTaskById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    ErrorMessage errorMessage = ErrorMessage.builder()
                            .code(BusinessErrorMessages.TASK_NOT_FOUND.getCode())
                            .message("La tarea con el id " + id + " no existe")
                            .build();
                    return Mono.error(new CustomBusinessException(BusinessErrorMessages.TASK_NOT_FOUND,
                            List.of(errorMessage)));
                }))
                .flatMap(existingTask -> taskRepository.deleteTask(id));
    }
}
