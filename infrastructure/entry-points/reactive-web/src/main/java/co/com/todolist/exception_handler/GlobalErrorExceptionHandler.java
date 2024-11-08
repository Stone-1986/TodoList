package co.com.todolist.exception_handler;

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
        Map<String, Object> errorAttributes = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        HttpStatus status = HttpStatus.valueOf((Integer) errorAttributes.getOrDefault(
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value())
        );
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorAttributes));
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