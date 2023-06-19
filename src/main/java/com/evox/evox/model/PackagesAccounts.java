package com.evox.evox.model;

import com.evox.evox.model.enums.Category;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "packages_accounts")
@Builder
public class PackagesAccounts {
    @Id
    private Integer id;
    private String title;
    private String description;
    private BigDecimal price;
    private String currency;
    private Category category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
