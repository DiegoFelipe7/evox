package com.evox.evox.model;

import com.evox.evox.model.enums.AccountState;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "marketing")
@Builder
public class Marketing {
    @Id
    private Integer id;
    private String type;
    private String transaction;
    private String currency;
    private Integer price;
    private LocalDateTime activationDate;
    private LocalDateTime expirationDate;
    private Integer marketingAccountId;
    private Integer userId;
    private AccountState marketingState;
    private Boolean state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


