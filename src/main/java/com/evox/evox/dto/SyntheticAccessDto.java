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
public class SyntheticAccessDto {
    private String username;
    private String email;
    private String login;
    private String password;
    private LocalDateTime createdAt;
    private Boolean state;
}
