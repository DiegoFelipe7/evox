package com.evox.evox.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bridge_funds_account")
public class BridgeFundsAccount {
    @Id
    private Integer id;
    @NotBlank(message = "El login es requerida")
    private String login;
    @NotBlank(message = "La contraseña es requerida")
    private String password;
    @Min(value = 1, message = "Error al momento de enviar el id de cuenta")
    private Integer bridgeFundsId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
