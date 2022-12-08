package ru.bodrov.mitap.demo.handler;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.bodrov.mitap.demo.dto.ClientDto;
import ru.bodrov.mitap.demo.dto.ItemDto;
import ru.bodrov.mitap.demo.dto.ProductDto;
import ru.bodrov.mitap.demo.grpc.Client;
import ru.bodrov.mitap.demo.grpc.ClientsGrpcService;
import ru.bodrov.mitap.demo.grpc.Product;
import ru.bodrov.mitap.demo.grpc.ProductsGrpcService;
import ru.bodrov.mitap.demo.mapper.DemoMapper;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class HandlerDemo {

    private final HandlerErrorForWeb handlerError;
    private final ProductsGrpcService productsService;
    private final ClientsGrpcService clientsGrpcService;

    public Mono<ServerResponse> getAllProducts(final ServerRequest request) {
        return Mono.defer(() -> productsService
                .getAllProducts()
                .subscribeOn(Schedulers.boundedElastic())
                .map(pr -> mapProductAddClient(pr, Map.of()))
                .collectList()
                .flatMap(list -> getResult(list, request)));
    }

    private Mono<ServerResponse> getResult(final List<? extends ItemDto> items, final ServerRequest request) {
        if (items.isEmpty()) {
            return handlerError.getAttributesError(new RuntimeException("getAllRates not found"), request);
        }
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(items.stream().limit(100).toList());
    }

    public Mono<ServerResponse> getAllClients(ServerRequest request) {
        return Mono.defer(() -> clientsGrpcService
                .getAllClients()
                .subscribeOn(Schedulers.boundedElastic())
                .map(DemoMapper::clientsToClientDto)
                .collectList()
                .flatMap(list -> getResult(list, request)));
    }

    public Mono<ServerResponse> mergeProductWithClient(final ServerRequest request) {
        return Mono.defer(() -> clientsGrpcService
                .getAllClients()
                .publishOn(Schedulers.boundedElastic())
                .collectList()
                .map(clients ->
                        clients.stream()
                               .flatMap(client -> client
                                       .getProductIdList()
                                       .stream()
                                       .map(productId -> new ValueConvert<>(productId, client)))
                               .collect(groupingBy(ValueConvert::getLeft, mapping(ValueConvert::getRight, toList()))))
                .flatMap(mapClient -> productsService.getAllProducts().publishOn(Schedulers.boundedElastic())
                                                     .map(pr -> mapProductAddClient(pr, mapClient)).collectList()
                )
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(list -> getResult(list, request)));
    }


    @Value
    static class ValueConvert<L, R> {
        L left;
        R right;
    }

    private ProductDto mapProductAddClient(final Product product, final Map<Integer, List<Client>> mapClients) {
        final var clients = mapClients.get(product.getId());
        final List<ClientDto> clientsDto = clients == null || clients.isEmpty()
                ? List.of()
                : clients.stream().map(DemoMapper::clientsToClientDto).toList();
        return DemoMapper.productsToProductDto(product, clientsDto);
    }
}
