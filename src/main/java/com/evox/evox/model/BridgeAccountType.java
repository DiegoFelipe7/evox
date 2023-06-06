package com.evox.evox.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bridge_account_type")
public class BridgeAccountType {
    @Id
    private Integer id;
    private String title;
    private String description;
    private BigDecimal price;
    private String currency;
}

