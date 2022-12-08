package ru.bodrov.mitap.demo.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ClientsGrpcService {

    @Value("${grpc.client.hostname}")
    private String hostname;

    @Value("${grpc.client.port}")
    private Integer port;

    private ManagedChannel channel;
    private ReactorClientServiceGrpc.ReactorClientServiceStub serviceStub;

    @PostConstruct
    public void defineGRPC() {
        final var channelBuilder = ManagedChannelBuilder
                .forAddress(hostname, port).usePlaintext();
        channel = channelBuilder.build();
        serviceStub = ReactorClientServiceGrpc.newReactorStub(channel);
    }

    @PreDestroy
    public void destroyGRPC() {
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("get repository method shutdown not right: " + e.getMessage());
        }
    }

    public Flux<Client> getAllClients(){
        final var response = serviceStub.getAllClients(
                ClientsRequest.newBuilder().build());
        return response
                .map(ClientsResponse::getClientList).flatMapMany(Flux::fromIterable);
    }

}
