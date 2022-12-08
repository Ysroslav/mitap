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
public class ProductsGrpcService {

    @Value("${grpc.product.hostname}")
    private String hostname;

    @Value("${grpc.product.port}")
    private Integer port;

    private ManagedChannel channel;
    private ReactorProductServiceGrpc.ReactorProductServiceStub serviceStub;

    @PostConstruct
    public void defineGRPC() {
        final var channelBuilder = ManagedChannelBuilder
                .forAddress(hostname, port).usePlaintext();
        channel = channelBuilder.build();
        serviceStub = ReactorProductServiceGrpc.newReactorStub(channel);
    }

    @PreDestroy
    public void destroyGRPC() {
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("get repository method shutdown not right: " + e.getMessage());
        }
    }

    public Flux<Product> getAllProducts(){
        final var response = serviceStub.getAllProducts(
                ProductsRequest.newBuilder().build());
        return response
                .map(ProductsResponse::getProductList
                ).flatMapMany(Flux::fromIterable);
    }



}
