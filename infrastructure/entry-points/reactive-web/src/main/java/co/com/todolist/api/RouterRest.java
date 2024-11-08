package co.com.todolist.api;

import co.com.todolist.model.task.FilterCriteria;
import co.com.todolist.model.task.TaskRequest;
import co.com.todolist.model.task.TaskUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/tasks",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createTask",
                    operation = @Operation(
                            operationId = "createTask",
                            summary = "Crear una nueva tarea",
                            description = "Crea una nueva tarea a partir de la información proporcionada",
                            tags = {"Task app"},
                            requestBody = @RequestBody(
                                    description = "Información de la tarea a crear",
                                    content = @Content(schema = @Schema(implementation = TaskRequest.class)),
                                    required = true
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Tarea creada correctamente"),
                                    @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/tasks/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = Handler.class,
                    beanMethod = "deleteTask",
                    operation = @Operation(
                            operationId = "deleteTask",
                            summary = "Eliminar una tarea",
                            description = "Elimina una tarea existente según el ID proporcionado",
                            tags = {"Task app"},
                            parameters = @Parameter(
                                    in = ParameterIn.PATH,
                                    name = "id", description = "ID de la tarea a eliminar"
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Tarea eliminada correctamente"),
                                    @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/tasks/filter",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "filterTasks",
                    operation = @Operation(
                            operationId = "filterTasks",
                            summary = "Obtener lista de tareas con filtros",
                            description = "Obtiene una lista de tareas filtradas de forma dinamica por ID, descripción y estado",
                            tags = {"Task app"},
                            requestBody = @RequestBody(
                                    description = "Criterios de filtro para las tareas",
                                    content = @Content(schema = @Schema(implementation = FilterCriteria.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200",
                                            description = "Lista de tareas obtenida correctamente"),
                                    @ApiResponse(responseCode = "400",
                                            description = "Solicitud incorrecta")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/tasks/{id}/toggle",
                    method = RequestMethod.PUT,
                    beanClass = Handler.class,
                    beanMethod = "toggleTaskStatus",
                    operation = @Operation(
                            operationId = "toggleTaskStatus",
                            summary = "Alternar estado de completado de una tarea",
                            description = "Alterna el estado de completado de una tarea entre verdadero y falso",
                            tags = {"Task app"},
                            parameters = @Parameter(in = ParameterIn.PATH, name = "id", description = "ID de la tarea a modificar"),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Estado de la tarea alternado correctamente"),
                                    @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/tasks/{id}",
                    method = RequestMethod.PUT,
                    beanClass = Handler.class,
                    beanMethod = "updateTask",
                    operation = @Operation(
                            operationId = "updateTask",
                            summary = "Actualizar una tarea existente",
                            description = "Actualiza la información de una tarea existente según el ID proporcionado",
                            tags = {"Task app"},
                            parameters = @Parameter(in = ParameterIn.PATH, name = "id", description = "ID de la tarea a actualizar"),
                            requestBody = @RequestBody(
                                    description = "Información actualizada de la tarea",
                                    content = @Content(schema = @Schema(implementation = TaskUpdate.class)),
                                    required = true
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Tarea actualizada correctamente"),
                                    @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/tasks"), handler::createTask)
                .andRoute(DELETE("/api/tasks/{id}"), handler::deleteTask)
                .andRoute(POST("/api/tasks/filter"), handler::filterTasks)
                .andRoute(PUT("/api/tasks/{id}/toggle"), handler::toggleTaskStatus)
                .andRoute(PUT("/api/tasks/{id}"), handler::updateTask);
    }
}
