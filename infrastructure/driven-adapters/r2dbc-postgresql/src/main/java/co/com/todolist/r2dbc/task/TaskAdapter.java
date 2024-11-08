package co.com.todolist.r2dbc.task;

import co.com.todolist.model.task.FilterCriteria;
import co.com.todolist.model.task.Task;
import co.com.todolist.model.task.gateways.TaskRepository;
import co.com.todolist.r2dbc.task.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.relational.core.query.Criteria.where;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TaskAdapter implements TaskRepository {
    private final R2dbcEntityTemplate entityTemplate;
    private final TaskMapper mapper;

    @Override
    public Mono<Task> createTask(Task task) {
        TaskData taskData = mapper.toTaskData(task);
        return entityTemplate.insert(TaskData.class)
                .using(taskData)
                .map(mapper::toTask);
    }

    @Override
    public Flux<Task> getAllTasks(FilterCriteria filterCriteria) {
        Criteria criteria = buildCriteriaFilter(filterCriteria);
        Query query = Query.query(criteria);
        return entityTemplate.select(query, TaskData.class).map(mapper::toTask);
    }

    @Override
    public Mono<Task> getTaskById(String id) {
        return entityTemplate.select(TaskData.class)
                .matching(Query.query(where("id").is(id)))
                .one()
                .flatMap(taskData -> taskData != null ? Mono.just(mapper.toTask(taskData)) : Mono.empty());
    }

    @Override
    public Mono<Task> updateTask(String id, Task updatedTask) {
        return getTaskById(id)
                .flatMap(existingTask -> {
                    Map<SqlIdentifier, Object> updates = new HashMap<>();
                    if (updatedTask.getTitle() != null) {
                        updates.put(SqlIdentifier.unquoted("titulo"), updatedTask.getTitle());
                    }
                    if (updatedTask.getDescription() != null) {
                        updates.put(SqlIdentifier.unquoted("descripcion"), updatedTask.getDescription());
                    }
                    if (updatedTask.getCompleted() != null) {
                        updates.put(SqlIdentifier.unquoted("estado"), updatedTask.getCompleted());
                    }
                    if (updates.isEmpty()) {
                        return Mono.just(existingTask);
                    }
                    Update update = Update.from(updates);
                    return entityTemplate.update(TaskData.class)
                            .matching(Query.query(where("id").is(id)))
                            .apply(update)
                            .flatMap(count -> count > 0 ? getTaskById(id) : Mono.empty());
                });
    }

    @Override
    public Mono<Void> deleteTask(String id) {
        return entityTemplate.delete(TaskData.class)
                .matching(Query.query(where("id").is(id)))
                .all()
                .then();
    }

    private Criteria buildCriteriaFilter(FilterCriteria filterCriteria) {
        List<Criteria> criteriaList = new ArrayList<>();
        if (filterCriteria.getId() != null) {
            criteriaList.add(where("id").is(filterCriteria.getId()));
        }
        if (filterCriteria.getTitle() != null) {
            criteriaList.add( where("titulo").is(filterCriteria.getTitle()));
        }
        if (filterCriteria.getDescription() != null) {
            criteriaList.add(where("descripcion").is(filterCriteria.getDescription()));
        }
        if (filterCriteria.getCompleted() != null) {
            criteriaList.add(where("estado").is(filterCriteria.getCompleted()));
        }
        return criteriaList.stream().reduce(Criteria::and).orElseGet(Criteria::empty);
    }
}
