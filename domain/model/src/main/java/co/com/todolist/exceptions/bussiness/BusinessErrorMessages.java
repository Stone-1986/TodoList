package co.com.todolist.exceptions.bussiness;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorMessages {
    BAD_REQUEST("SCB002", "Bad request", 400, "Invalid Request");
    private final String code;
    private final String message;
    private final int statusCode;
    private final String title;
}
