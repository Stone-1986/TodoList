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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteTaskUseCaseTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private DeleteTaskUseCase deleteTaskUseCase;

    @Test
    void deleteTask_shouldCompleteSuccessfully_whenTaskExists() {
        // Arrange
        when(taskRepository.getTaskById(anyString())).thenReturn(Mono.just(new Task()));
        when(taskRepository.deleteTask(anyString())).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = deleteTaskUseCase.action("testId");

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void deleteTask_shouldReturnError_whenTaskDoesNotExist() {
        // Arrange
        when(taskRepository.getTaskById(anyString())).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = deleteTaskUseCase.action("testId");

        // Assert
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}