package co.com.todolist.exception_handler;

import co.com.todolist.exceptions.bussiness.CustomBusinessException;
import co.com.todolist.exceptions.error.ErrorDetails;
import co.com.todolist.exceptions.tecnical.CustomTechnicalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
public class GlobalErrorExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties,
                                       ApplicationContext applicationContext, ServerCodecConfigurer configure) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageReaders(configure.getReaders());
        this.setMessageWriters(configure.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        HttpStatus status = getHttpStatus(error);

        Map<String, Object> errorAttributesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        ErrorDetails errorDetails = (ErrorDetails) errorAttributesMap.get("errorDetails");

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorDetails));
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

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        return super.handle(exchange, ex)
                .doOnTerminate(() -> logError(exchange, ex));
    }

    private void logError(ServerWebExchange exchange, Throwable ex) {
        log.error("Exception handled: request={}, exception={}",
                exchange.getRequest().getPath(), ex.getMessage(), ex);
    }
}
