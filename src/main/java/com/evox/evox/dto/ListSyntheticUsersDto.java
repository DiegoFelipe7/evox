package com.evox.evox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListSyntheticUsersDto {
    private String type;
    private String transaction;
    private String currency;
    private Integer price;
    private LocalDateTime createdAt;
    private LocalDateTime expirationDate;
    private Boolean state;
    private String username;
    private String email;
}
