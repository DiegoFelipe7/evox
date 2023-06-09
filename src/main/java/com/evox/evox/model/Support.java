package com.evox.evox.model;

import com.evox.evox.model.enums.State;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "support")
@Builder
public class Support {
    @Id
    private Integer id;
    private String ticket;
    private String category;
    private String question;
    private String answer;
    private Integer userId;
    private String urlPhoto;
    private State state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
