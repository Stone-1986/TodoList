package co.com.todolist.exception_handler;

import co.com.todolist.exceptions.bussiness.CustomBusinessException;
import co.com.todolist.exceptions.tecnical.CustomTechnicalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = new HashMap<>();
        Throwable error = getError(request);
        HttpStatus status;
        String title;
        if (error instanceof CustomBusinessException businessException) {
            status = HttpStatus.valueOf(businessException.getStatusCode());
            title = businessException.getTitle();
            errorAttributes.put("errors", businessException.getErrors());
        } else if (error instanceof CustomTechnicalException technicalException) {
            status = HttpStatus.valueOf(technicalException.getStatusCode());
            title = technicalException.getTitle();
            errorAttributes.put("errors", technicalException.getErrors());
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            title = "An unexpected error occurred";
        }
        errorAttributes.put("status", status.value());
        errorAttributes.put("title", title);
        errorAttributes.put("timestamp", LocalDateTime.now().toString());
        // Log the error
        log.error("Error occurred: {}, status: {}", title, status);
        return errorAttributes;
    }
}