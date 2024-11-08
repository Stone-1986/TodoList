package co.com.todolist.exceptions.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ErrorDetails {
    private String currentTime;
    private String title;
    private List<ErrorMessage> errorMessages;
}
