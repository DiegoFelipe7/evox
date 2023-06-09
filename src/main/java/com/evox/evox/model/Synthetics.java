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
@Builder(toBuilder = true)
@Table(name = "synthetics")
public class Synthetics {
    @Id
    private Integer id;
    private String type;
    private String transaction;
    private String currency;
    private Integer price=15;
    private LocalDateTime activationDate;
    private LocalDateTime expirationDate;
    private Integer userId;
    private AccountState syntheticState;
    private Boolean state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
