package com.evox.evox.model;

import com.evox.evox.model.enums.Category;
import com.evox.evox.model.enums.PaymentsState;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
@Builder
public class Payments {
    @Id
    private Integer id;
    private String idCommission;
    private String transaction;
    private Category concept;
    private Integer level;
    private Integer userId;
    private BigDecimal total;
    private String currency;
    private Boolean status;
    private PaymentsState paymentsState;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Payments(String idCommission, String transaction, Category concept,Integer level, Integer userId, BigDecimal total, PaymentsState paymentsState) {
        this.idCommission = idCommission;
        this.transaction = transaction;
        this.concept = concept;
        this.level=level;
        this.userId = userId;
        this.total = total;
        this.paymentsState = paymentsState;
    }
}
