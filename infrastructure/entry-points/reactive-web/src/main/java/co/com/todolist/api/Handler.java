package co.com.todolist.api;

import co.com.todolist.model.task.FilterCriteria;
import co.com.todolist.model.task.Task;
import co.com.todolist.model.task.TaskRequest;
import co.com.todolist.model.task.TaskUpdate;
import co.com.todolist.usecase.task.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class Handler {
    private final CreateTaskUseCase createTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final FilterTasksUseCase filterTasksUseCase;
    private final ToggleTaskStatusUseCase toggleTaskStatusUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;

    public Mono<ServerResponse> createTask(ServerRequest request) {
        return request.bodyToMono(TaskRequest.class)
                .flatMap(taskRequest -> {
                    Task task = Task.builder()
                            .title(taskRequest.getTitle())
                            .description(taskRequest.getDescription())
                            .build();
                    return createTaskUseCase.action(task);
                })
                .flatMap(task -> ServerResponse.ok().body(fromValue(task)))
                .switchIfEmpty(ServerResponse.badRequest().build());

    }

    public Mono<ServerResponse> deleteTask(ServerRequest request) {
        String id = request.pathVariable("id");
        return deleteTaskUseCase.action(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(IllegalArgumentException.class, e -> ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> filterTasks(ServerRequest request) {
        return request.bodyToMono(FilterCriteria.class)
                .flatMap(criteria -> ServerResponse.ok()
                        .body(filterTasksUseCase.action(criteria), Task.class)
                )
                .switchIfEmpty(ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> toggleTaskStatus(ServerRequest request) {
        String id = request.pathVariable("id");
        return toggleTaskStatusUseCase.action(id)
                .flatMap(task -> ServerResponse.ok().body(fromValue(task)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateTask(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(TaskUpdate.class)
                .flatMap(taskUpdate -> updateTaskUseCase.action(id, taskUpdate))
                .flatMap(updatedTask -> ServerResponse.ok().body(fromValue(updatedTask)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}