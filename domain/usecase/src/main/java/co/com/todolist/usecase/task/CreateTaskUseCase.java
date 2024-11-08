package co.com.todolist.usecase.task;

import co.com.todolist.exceptions.bussiness.BusinessErrorMessages;
import co.com.todolist.exceptions.bussiness.CustomBusinessException;
import co.com.todolist.exceptions.error.ErrorMessage;
import co.com.todolist.model.task.Task;
import co.com.todolist.model.task.gateways.TaskRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class CreateTaskUseCase {
    private final TaskRepository taskRepository;

    public Mono<Task> action(Task task) {
        return Mono.just(task)
                .flatMap(t -> {
                    if (t.getTitle() == null || t.getTitle().isEmpty()) {
                        ErrorMessage errorMessage = ErrorMessage.builder()
                                .code(BusinessErrorMessages.EMPTY_TITLE.getCode())
                                .message("El titulo de la tarea no puede estar vacio")
                                .build();
                        return Mono.error(new CustomBusinessException(BusinessErrorMessages.EMPTY_TITLE, List.of(errorMessage)));
                    }
                    return Mono.just(t);
                })
                .map(t -> t.toBuilder().completed(false).build())
                .flatMap(taskRepository::createTask);
    }
}
