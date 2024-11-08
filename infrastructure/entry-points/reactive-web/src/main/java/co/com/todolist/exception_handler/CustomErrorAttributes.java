package co.com.todolist.exception_handler;

import co.com.todolist.exceptions.bussiness.CustomBusinessException;
import co.com.todolist.exceptions.error.ErrorDetails;
import co.com.todolist.exceptions.error.ErrorMessage;
import co.com.todolist.exceptions.tecnical.CustomTechnicalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        HttpStatus status = getHttpStatus(error);
        String title;
        List<ErrorMessage> errors;

        if (error instanceof CustomBusinessException businessException) {
            title = businessException.getTitle();
            errors = businessException.getErrors();
        } else if (error instanceof CustomTechnicalException technicalException) {
            title = technicalException.getTitle();
            errors = technicalException.getErrors();
        } else if (error instanceof WebExchangeBindException bindException) {
            title = "Validation Failed";
            errors = bindException.getFieldErrors().stream()
                    .map(fieldError -> ErrorMessage.builder()
                            .code("VALIDATION_ERROR")
                            .message(fieldError.getField() + " " + fieldError.getDefaultMessage())
                            .build())
                    .collect(Collectors.toList());
        } else {
            title = "An unexpected error occurred";
            errors = Collections.singletonList(ErrorMessage.builder()
                    .code("UNEXPECTED_ERROR")
                    .message("An unexpected error occurred. Please contact support.")
                    .build());
        }

        ErrorDetails errorDetails = ErrorDetails.builder()
                .currentTime(LocalDateTime.now().toString())
                .title(title)
                .errorMessages(errors)
                .build();

        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", status.value());
        errorAttributes.put("errorDetails", errorDetails);

        // Registro del error con detalles adicionales
        log.error("Error occurred: {}, status: {}, errors: {}", title, status, errors, error);

        return errorAttributes;
    }

    private HttpStatus getHttpStatus(Throwable error) {
        if (error instanceof CustomBusinessException businessException) {
            return HttpStatus.valueOf(businessException.getStatusCode());
        } else if (error instanceof CustomTechnicalException technicalException) {
            return HttpStatus.valueOf(technicalException.getStatusCode());
        } else if (error instanceof WebExchangeBindException) {
            return HttpStatus.BAD_REQUEST;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
