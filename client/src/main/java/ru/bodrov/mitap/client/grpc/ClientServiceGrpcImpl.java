package ru.bodrov.mitap.client.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.bodrov.mitap.demo.grpc.Client;
import ru.bodrov.mitap.demo.grpc.ClientsRequest;
import ru.bodrov.mitap.demo.grpc.ClientsResponse;
import ru.bodrov.mitap.demo.grpc.ReactorClientServiceGrpc;

import java.time.LocalDate;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class ClientServiceGrpcImpl extends ReactorClientServiceGrpc.ClientServiceImplBase {

    @Override
    public Mono<ClientsResponse> getAllClients(Mono<ClientsRequest> request) {
        return request.publishOn(Schedulers.boundedElastic()).map(r -> ClientsResponse
                .newBuilder()
                .addAllClient(generateProduct().toIterable()).build());
    }

    public Flux<Client> generateProduct(){
        return Flux.range(1, 100).map(id ->  Client
                .newBuilder()
                .setClientName("test" + id)
                .setDateAdd(LocalDate.now().toString())
                .setId(1)
                .addAllProductId(List.of(id, id + 1, id + 2))
                .build()).publishOn(Schedulers.boundedElastic());
    }
}
