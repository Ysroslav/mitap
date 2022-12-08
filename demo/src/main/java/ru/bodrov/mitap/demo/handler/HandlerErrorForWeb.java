package ru.bodrov.mitap.demo.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
@Component
public class HandlerErrorForWeb {

    public Mono<ServerResponse> getAttributesError(
            final Throwable serverException,
            final ServerRequest request
    ) {
        log.error("Error! {} message: {}", serverException, serverException.getMessage());
        final var map = new HashMap<String, Object>();
        map.put("status", getStatusWithException(serverException));
        map.put("error", serverException.toString());
        map.put("message", serverException.getMessage());
        map.put("timestamp", LocalDateTime.now());
        map.put("path", request.path());

        return ServerResponse.status(getStatusWithException(serverException))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(map));

    }

    private HttpStatus getStatusWithException(
            final Throwable serverException
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (serverException instanceof NullPointerException) {
            status = HttpStatus.NOT_FOUND;
        }

        if (serverException instanceof RuntimeException) {
            status = HttpStatus.BAD_REQUEST;
        }

        return status;
    }
}
