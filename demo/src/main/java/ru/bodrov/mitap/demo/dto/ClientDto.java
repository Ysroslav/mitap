package ru.bodrov.mitap.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter
@Setter
public class ClientDto implements ItemDto{

    private String name;
}
