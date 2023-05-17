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
    private String refLink;
    private String userName;
    private Boolean status;
    private LocalDateTime dateRegistered;


}
