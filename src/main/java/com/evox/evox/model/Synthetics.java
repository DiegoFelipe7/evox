package com.evox.evox.model;
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
    private String transaction;
    private String currency;
    private Integer price=15;
    private LocalDateTime createdAt;
    private LocalDateTime expirationDate;
    private Integer userId;
    private Boolean state;
}
