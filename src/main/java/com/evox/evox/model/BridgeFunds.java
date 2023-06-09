package com.evox.evox.model;


import com.evox.evox.model.enums.AccountState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bridge_funds")
public class BridgeFunds {
    @Id
    private Integer id;
    private String type;
    @NotBlank(message = "La transaccion es requerida")
    private String transaction;
    private String title;
    @Min(value = 1, message = "La cantidad debe ser diferente de cero")
    private Integer quantity;
    private BigDecimal total;
    private Integer userId;
    @Min(value = 1, message = "Error en la seleccion de un tipo de cuenta")
    private Integer bridgeAccountId;
    private AccountState bridgeFundsState;
    private Boolean state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
