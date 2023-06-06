package com.evox.evox.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListBridgeFundUsersDto {

    private String transaction;
    private String title;
    private String price;
    private Integer quantity;
    private Integer total;
    private String currency;
    private String username;
    private String email;
    private Boolean state;


}
