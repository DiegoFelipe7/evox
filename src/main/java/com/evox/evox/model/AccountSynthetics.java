package com.evox.evox.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account_synthetics")
public class AccountSynthetics {
    @Id
    private Integer id;
    private String login;
    private String password;
    private LocalDateTime createdAt;
    private Boolean state;
}
