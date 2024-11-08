package co.com.todolist.usecase.task;

import co.com.todolist.exceptions.bussiness.CustomBusinessException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToggleTaskStatusUseCaseTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ToggleTaskStatusUseCase toggleTaskStatusUseCase;

    @Test
    void toggleTaskStatus_shouldToggleCompletedStatus_whenTaskExists() {
        // Arrange
        Task task = Task.builder().id("testId").completed(false).build();
        Task toggledTask = task.toBuilder().completed(true).build();
        when(taskRepository.getTaskById(anyString())).thenReturn(Mono.just(task));
        when(taskRepository.updateTask(anyString(), any(Task.class))).thenReturn(Mono.just(toggledTask));

        // Act
        Mono<Task> result = toggleTaskStatusUseCase.action("testId");

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(Task::getCompleted)
                .verifyComplete();
    }

    @Test
    void toggleTaskStatus_shouldReturnError_whenTaskDoesNotExist() {
        // Arrange
        when(taskRepository.getTaskById(anyString())).thenReturn(Mono.empty());

        // Act
        Mono<Task> result = toggleTaskStatusUseCase.action("testId");

        // Assert
        StepVerifier.create(result)
                .expectError(CustomBusinessException.class)
                .verify();
    }
}