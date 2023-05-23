package com.evox.evox.dto;
import com.evox.evox.model.enums.TypeOfIdentification;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String country;
    private String city;
    private Boolean emailVerified;
    private String token;
    private String photo;
    private String refLink;
    private String invitationLink;
    private String  roles;
    private Boolean state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
