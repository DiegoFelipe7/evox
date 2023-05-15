package com.evox.evox.utils;

import com.evox.evox.utils.enums.TypeStateResponse;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Response {
    private TypeStateResponse typeStatus;
    private String message;
}
