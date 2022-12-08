package ru.bodrov.mitap.demo.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.bodrov.mitap.demo.handler.HandlerDemo;

@Configuration
public class RouteDemo {

    @Bean
    public RouterFunction<ServerResponse> route(final HandlerDemo handler) {
        return RouterFunctions.route()
                              .GET("/api/v1/product/list", handler::mergeProductWithClient)
                              .GET("/api/v1/client/list", handler::getAllClients)
                              .build();
    }
}
