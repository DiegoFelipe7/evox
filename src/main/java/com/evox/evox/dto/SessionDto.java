package com.evox.evox.dto;

import com.evox.evox.model.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDto {
    private Integer id;
    private User user;
    private String ipAddress;
    private String country;
    private String browser;
    private LocalDateTime dateOfEntry;
}
