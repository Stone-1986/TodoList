package co.com.todolist.r2dbc.task.mapper;

import co.com.todolist.model.task.Task;
import co.com.todolist.r2dbc.task.TaskData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskData toTaskData(Task task);

    Task toTask(TaskData taskData);
}
