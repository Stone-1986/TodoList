package co.com.todolist.usecase.task;

import co.com.todolist.exceptions.bussiness.CustomBusinessException;
import co.com.todolist.model.task.Task;
import co.com.todolist.model.task.TaskUpdate;
import co.com.todolist.model.task.gateways.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private UpdateTaskUseCase updateTaskUseCase;

    @Test
    void updateTask_shouldUpdateFields_whenFieldsAreProvided() {
        // Arrange
        String taskId = "testId";
        Task existingTask = Task.builder()
                .id(taskId)
                .title("Old Title")
                .description("Old Description")
                .completed(false)
                .build();

        TaskUpdate updatedTask = new TaskUpdate();
        updatedTask.setTitle("New Title");
        updatedTask.setDescription("New Description");
        updatedTask.setCompleted(true);

        Task expectedUpdatedTask = existingTask.toBuilder()
                .title("New Title")
                .description("New Description")
                .completed(true)
                .build();

        when(taskRepository.getTaskById(taskId)).thenReturn(Mono.just(existingTask));
        when(taskRepository.updateTask(eq(taskId), any(Task.class))).thenReturn(Mono.just(expectedUpdatedTask));

        // Act
        Mono<Task> result = updateTaskUseCase.action(taskId, updatedTask);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(task ->
                        task.getTitle().equals("New Title") &&
                                task.getDescription().equals("New Description") &&
                                Boolean.TRUE.equals(task.getCompleted()))
                .verifyComplete();
    }

    @Test
    void updateTask_shouldReturnExistingTask_whenNoFieldsProvided() {
        // Arrange
        String taskId = "testId";
        Task existingTask = Task.builder()
                .id(taskId)
                .title("Existing Title")
                .description("Existing Description")
                .completed(false)
                .build();

        TaskUpdate updatedTask = new TaskUpdate(); // No fields set

        when(taskRepository.getTaskById(taskId)).thenReturn(Mono.just(existingTask));
        when(taskRepository.updateTask(eq(taskId), any(Task.class))).thenReturn(Mono.just(existingTask));

        // Act
        Mono<Task> result = updateTaskUseCase.action(taskId, updatedTask);

        // Assert
        StepVerifier.create(result)
                .expectNext(existingTask)
                .verifyComplete();
    }

    @Test
    void updateTask_shouldReturnError_whenTaskDoesNotExist() {
        // Arrange
        String taskId = "nonExistentId";
        TaskUpdate updatedTask = new TaskUpdate();
        updatedTask.setDescription("Updated Description");

        when(taskRepository.getTaskById(taskId)).thenReturn(Mono.empty());

        // Act
        Mono<Task> result = updateTaskUseCase.action(taskId, updatedTask);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertTrue(throwable instanceof CustomBusinessException, " CustomBusinessException");
                    CustomBusinessException exception = (CustomBusinessException) throwable;
                    assertEquals("SCB003", exception.getCode(), "Código de error incorrecto");
                })
                .verify();
    }


    @Test
    void updateTask_shouldUpdatePartialFields_whenSomeFieldsAreProvided() {
        // Arrange
        String taskId = "testId";
        Task existingTask = Task.builder()
                .id(taskId)
                .title("Old Title")
                .description("Old Description")
                .completed(false)
                .build();

        TaskUpdate updatedTask = new TaskUpdate();
        updatedTask.setTitle("New Title");

        Task expectedUpdatedTask = existingTask.toBuilder()
                .title("New Title")
                .description("Old Description")
                .completed(false)
                .build();

        when(taskRepository.getTaskById(taskId)).thenReturn(Mono.just(existingTask));
        when(taskRepository.updateTask(eq(taskId), any(Task.class))).thenReturn(Mono.just(expectedUpdatedTask));

        // Act
        Mono<Task> result = updateTaskUseCase.action(taskId, updatedTask);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(task ->
                        task.getTitle().equals("New Title") &&
                                task.getDescription().equals("Old Description") &&
                                Boolean.FALSE.equals(task.getCompleted()))
                .verifyComplete();
    }
}