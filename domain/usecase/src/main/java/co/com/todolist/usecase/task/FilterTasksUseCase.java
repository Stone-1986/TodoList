package co.com.todolist.usecase.task;

import co.com.todolist.model.task.FilterCriteria;
import co.com.todolist.model.task.Task;
import co.com.todolist.model.task.gateways.TaskRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class FilterTasksUseCase {
    private final TaskRepository taskRepository;

    public Flux<Task> action(FilterCriteria filterCriteria) {
        return taskRepository.getAllTasks(filterCriteria);
    }
}
