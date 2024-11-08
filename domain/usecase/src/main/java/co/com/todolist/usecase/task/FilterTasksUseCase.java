package co.com.todolist.usecase.task;

import co.com.todolist.exceptions.bussiness.BusinessErrorMessages;
import co.com.todolist.exceptions.bussiness.CustomBusinessException;
import co.com.todolist.exceptions.error.ErrorMessage;
import co.com.todolist.model.task.FilterCriteria;
import co.com.todolist.model.task.Task;
import co.com.todolist.model.task.gateways.TaskRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
public class FilterTasksUseCase {
    private final TaskRepository taskRepository;

    public Flux<Task> action(FilterCriteria filterCriteria) {
        if (filterCriteria == null) {
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .code(BusinessErrorMessages.BAD_REQUEST.getCode())
                    .message("Los criterios de filtro no pueden ser nulos")
                    .build();
            return Flux.error(new CustomBusinessException(BusinessErrorMessages.BAD_REQUEST, List.of(errorMessage)));
        }
        return taskRepository.getAllTasks(filterCriteria);
    }
}
