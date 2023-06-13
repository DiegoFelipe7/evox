package com.evox.evox.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "session")
@Builder
public class Session {
    @Id
    private Integer id;
    private Integer userId;
    private String ipAddress;
    private String country;
    private String browser;
    private LocalDateTime dateOfEntry;

    public Session(Integer user, String ipAddress, String country, String browser, LocalDateTime dateOfEntry) {
        this.userId = user;
        this.ipAddress = ipAddress;
        this.country = country;
        this.browser = browser;
        this.dateOfEntry = dateOfEntry;
    }
}
