package com.evox.evox.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private String password;
    private String fullName;
    private String phone;
    private String country;
    private String city;
    private LocalDateTime emailVerified;
    private String token;
    private String photo;
    private String refLink;
    private String invitationLink;
    private String  roles;
    @JsonIgnore
    private Integer parentId;
    private Boolean status;
    private Integer level;
    private String evoxWallet;
    @JsonIgnore
    private LocalDateTime createdAt;
    @JsonIgnore
    private LocalDateTime updatedAt;

}
