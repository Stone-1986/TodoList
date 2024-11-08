package co.com.todolist.usecase.task;

import co.com.todolist.model.task.Task;
import co.com.todolist.model.task.gateways.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CreateTaskUseCase createTaskUseCase;

    @Test
    void createTask_shouldReturnCreatedTask_whenTitleIsProvided() {
        // Arrange
        Task task = Task.builder()
                .title("New Task")
                .description("Task Description")
                .build();

        Task savedTask = task.toBuilder()
                .completed(false)
                .build();

        when(taskRepository.createTask(any(Task.class))).thenReturn(Mono.just(savedTask));

        // Act
        Mono<Task> result = createTaskUseCase.action(task);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(createdTask ->
                        createdTask.getTitle().equals("New Task") &&
                                createdTask.getDescription().equals("Task Description") &&
                                Boolean.FALSE.equals(createdTask.getCompleted()))
                .verifyComplete();
    }

    @Test
    void createTask_shouldReturnError_whenTitleIsEmpty() {
        // Arrange
        Task task = Task.builder()
                .title("")
                .description("Task Description")
                .build();

        // Act
        Mono<Task> result = createTaskUseCase.action(task);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El titulo de la tarea no puede estar vacío"))
                .verify();
    }

    @Test
    void createTask_shouldReturnError_whenTitleIsNull() {
        // Arrange
        Task task = Task.builder()
                .title(null)
                .description("Task Description")
                .build();

        // Act
        Mono<Task> result = createTaskUseCase.action(task);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El titulo de la tarea no puede estar vacío"))
                .verify();
    }
}