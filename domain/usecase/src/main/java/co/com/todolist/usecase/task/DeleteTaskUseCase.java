package co.com.todolist.usecase.task;

import co.com.todolist.model.task.gateways.TaskRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteTaskUseCase {
    private final TaskRepository taskRepository;

    public Mono<Void> action(String id) {
        return taskRepository.getTaskById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La tarea con el id " + id + " no existe")))
                .flatMap(existingTask -> taskRepository.deleteTask(id));
    }
}
