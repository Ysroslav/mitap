package ru.bodrov.mitap.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
public class ProductDto implements ItemDto {

    private Integer productId;
    private String name;
    private LocalDate dateAdd;
    private boolean isValid;
    private List<ClientDto> clients;
}
