package co.com.todolist.exceptions.bussiness;

import co.com.todolist.exceptions.error.ErrorMessage;
import lombok.Getter;

import java.util.List;

@Getter
public class CustomBusinessException extends RuntimeException {
    private final String code;
    private final String title;
    private final List<ErrorMessage> errors;
    private final int statusCode;

    public CustomBusinessException(BusinessErrorMessages errorMessages, List<ErrorMessage> errors) {
        super(errorMessages.getMessage());
        this.code = errorMessages.getCode();
        this.title = errorMessages.getTitle();
        this.errors = errors;
        this.statusCode = errorMessages.getStatusCode();
    }

    public CustomBusinessException(String code, String message, String title, List<ErrorMessage> errors, int statusCode) {
        super(message);
        this.code = code;
        this.title = title;
        this.errors = errors;
        this.statusCode = statusCode;
    }
}
