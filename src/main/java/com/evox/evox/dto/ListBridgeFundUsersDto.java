package com.evox.evox.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListBridgeFundUsersDto {

    private String transaction;
    private String title;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal total;
    private String currency;
    private String username;
    private String email;
    private Boolean state;


}
