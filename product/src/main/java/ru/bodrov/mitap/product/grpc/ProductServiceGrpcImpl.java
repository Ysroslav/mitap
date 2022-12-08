package ru.bodrov.mitap.product.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.bodrov.mitap.demo.grpc.Product;
import ru.bodrov.mitap.demo.grpc.ProductsRequest;
import ru.bodrov.mitap.demo.grpc.ProductsResponse;
import ru.bodrov.mitap.demo.grpc.ReactorProductServiceGrpc;

import java.time.LocalDate;

@GrpcService
@RequiredArgsConstructor
public class ProductServiceGrpcImpl extends ReactorProductServiceGrpc.ProductServiceImplBase {


    @Override
    public Mono<ProductsResponse> getAllProducts(Mono<ProductsRequest> request) {
        return request
                .publishOn(Schedulers.boundedElastic())
                .map(r -> ProductsResponse.newBuilder()
                                          .addAllProduct(generateProduct().toIterable())
                                          .build());
    }


    public Flux<Product> generateProduct(){
        return Flux.range(1, 100).map(id -> Product.newBuilder()
                                                   .setId(id)
                                                   .setProductName("Test " + id)
                                                   .setIsValid(true)
                                                   .setDateAdd(LocalDate.now().toString()).build()).publishOn(Schedulers.boundedElastic());
    }
}
