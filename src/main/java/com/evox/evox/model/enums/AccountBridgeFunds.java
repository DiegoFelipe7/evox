package com.evox.evox.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account_bridge_funds")
public class AccountBridgeFunds {
    private Integer id;
    private String login;
    private String password;
    private Integer bridgeFundsId;
    private Boolean state;
}
