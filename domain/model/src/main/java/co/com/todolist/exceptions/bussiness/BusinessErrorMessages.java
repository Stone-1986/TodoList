package co.com.todolist.exceptions.bussiness;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorMessages {
    BAD_REQUEST("SCB002", "Bad request", 400, "Invalid Request"),
    TASK_NOT_FOUND("SCB003", "Task not found", 404, "Resource Not Found"),
    EMPTY_TITLE("SCB004", "Empty title", 400, "Invalid Input");

    private final String code;
    private final String message;
    private final int statusCode;
    private final String title;
}
