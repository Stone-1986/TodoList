package co.com.todolist.exceptions.tecnical;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalErrorMessages {
    SERVER_ERROR("SCT001", "Server error", 500, "Internal Server Error");

    private final String code;
    private final String message;
    private final int statusCode;
    private final String title;
}
