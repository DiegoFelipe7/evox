package com.evox.evox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultiLevelDto {
    private String parent;
    private String refLink;
    private String userName;
    private String fullName;
    private Boolean status;
    private LocalDateTime dateRegistered;

    public MultiLevelDto(String refLink, String userName, String fullName, Boolean status, LocalDateTime dateRegistered) {
        this.refLink = refLink;
        this.userName = userName;
        this.fullName = fullName;
        this.status = status;
        this.dateRegistered = dateRegistered;
    }
}
