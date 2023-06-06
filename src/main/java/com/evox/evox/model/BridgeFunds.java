package com.evox.evox.model;


import com.evox.evox.model.enums.AccountState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bridge_funds")
public class BridgeFunds {
    private Integer id;
    private String transaction;
    private Integer quantity;
    private BigDecimal total;
    private Integer userId;
    private Integer bridgeAccountType;
    private AccountState bridgeFundsState;
    private Boolean state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
