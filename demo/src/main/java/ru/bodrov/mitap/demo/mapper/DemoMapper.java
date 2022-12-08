package ru.bodrov.mitap.demo.mapper;

import ru.bodrov.mitap.demo.dto.ClientDto;
import ru.bodrov.mitap.demo.dto.ProductDto;
import ru.bodrov.mitap.demo.grpc.Client;
import ru.bodrov.mitap.demo.grpc.Product;

import java.time.LocalDate;
import java.util.List;

public class DemoMapper {

    public static ProductDto productsToProductDto(final Product product, final List<ClientDto> clients) {
        return ProductDto.builder()
                         .productId(product.getId())
                         .isValid(product.getIsValid())
                         .name(product.getProductName())
                         .dateAdd(LocalDate.parse(product.getDateAdd()))
                         .clients(clients)
                         .build();

    }

    public static ClientDto clientsToClientDto(final Client client) {
        return ClientDto.builder().name(client.getClientName()).build();
    }
}
