package co.com.todolist.usecase.task;

import co.com.todolist.model.task.Task;
import co.com.todolist.model.task.gateways.TaskRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateTaskUseCase {
    private final TaskRepository taskRepository;

    public Mono<Task> action(Task task) {
        return Mono.just(task)
                .filter(t -> t.getTitle() != null && !t.getTitle().isEmpty())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El titulo de la tarea no puede estar vacío")))
                .map(t -> t.toBuilder().completed(false).build())
                .flatMap(taskRepository::createTask);
    }
}
