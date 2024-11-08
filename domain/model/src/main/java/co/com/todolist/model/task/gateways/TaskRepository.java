package co.com.todolist.model.task.gateways;

import co.com.todolist.model.task.FilterCriteria;
import co.com.todolist.model.task.Task;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskRepository {
    Mono<Task> createTask(Task task);

    Flux<Task> getAllTasks(FilterCriteria filterCriteria);

    Mono<Task> getTaskById(String id);

    Mono<Task> updateTask(String id, Task updatedTask);

    Mono<Void> deleteTask(String id);
}
