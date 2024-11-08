package co.com.todolist.usecase.task;

import co.com.todolist.model.task.FilterCriteria;
import co.com.todolist.model.task.Task;
import co.com.todolist.model.task.gateways.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilterTasksUseCaseTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private FilterTasksUseCase filterTasksUseCase;

    @Test
    void filterTasks_shouldReturnMatchingTasks_whenFiltersAreApplied() {
        // Arrange
        Task task1 = Task.builder()
                .id("1")
                .title("Test Task 1")
                .description("Description 1")
                .completed(false)
                .build();

        FilterCriteria filterCriteria = new FilterCriteria();
        filterCriteria.setId("1");

        when(taskRepository.getAllTasks(filterCriteria)).thenReturn(Flux.just(task1));

        // Act
        Flux<Task> result = filterTasksUseCase.action(filterCriteria);

        // Assert
        StepVerifier.create(result)
                .expectNext(task1)
                .verifyComplete();
    }

    @Test
    void filterTasks_shouldReturnAllTasks_whenNoFiltersAreApplied() {
        // Arrange
        Task task1 = Task.builder()
                .id("1")
                .title("Task 1")
                .description("Description 1")
                .completed(true)
                .build();

        Task task2 = Task.builder()
                .id("2")
                .title("Task 2")
                .description("Description 2")
                .completed(false)
                .build();

        FilterCriteria filterCriteria = new FilterCriteria(); // No filters set

        when(taskRepository.getAllTasks(filterCriteria)).thenReturn(Flux.just(task1, task2));

        // Act
        Flux<Task> result = filterTasksUseCase.action(filterCriteria);

        // Assert
        StepVerifier.create(result)
                .expectNext(task1)
                .expectNext(task2)
                .verifyComplete();
    }

    @Test
    void filterTasks_shouldReturnEmptyFlux_whenNoTasksMatchFilters() {
        // Arrange
        FilterCriteria filterCriteria = new FilterCriteria();
        filterCriteria.setTitle("Non-existent Title");

        when(taskRepository.getAllTasks(filterCriteria)).thenReturn(Flux.empty());

        // Act
        Flux<Task> result = filterTasksUseCase.action(filterCriteria);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void filterTasks_shouldHandleMultipleFilters() {
        // Arrange
        Task task1 = Task.builder()
                .id("1")
                .title("Task 1")
                .description("Description 1")
                .completed(true)
                .build();

        FilterCriteria filterCriteria = new FilterCriteria();
        filterCriteria.setTitle("Task 1");
        filterCriteria.setCompleted(true);

        when(taskRepository.getAllTasks(filterCriteria)).thenReturn(Flux.just(task1));

        // Act
        Flux<Task> result = filterTasksUseCase.action(filterCriteria);

        // Assert
        StepVerifier.create(result)
                .expectNext(task1)
                .verifyComplete();
    }
}