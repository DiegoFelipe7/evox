package com.evox.evox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReferralsDto {
    private String fullName;
    private Integer level;
    private String phone;
    private String userName;
    private LocalDateTime dateRegistered;

    public ReferralsDto(String fullName, String phone, String userName, LocalDateTime dateRegistered) {
        this.fullName = fullName;
        this.phone = phone;
        this.userName = userName;
        this.dateRegistered = dateRegistered;
    }

    public ReferralsDto(String fullName, Integer level, String userName, LocalDateTime dateRegistered) {
        this.fullName = fullName;
        this.level= level;
        this.userName = userName;
        this.dateRegistered = dateRegistered;
    }


}
