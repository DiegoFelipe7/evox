package com.evox.evox.exception;

import com.evox.evox.utils.enums.TypeStateResponse;
import lombok.Data;
import org.springframework.http.HttpStatus;
@Data
public class CustomException extends Exception{

    private final HttpStatus status;
    private final TypeStateResponse typeStatus;


    public CustomException(HttpStatus status, String message, TypeStateResponse typeStatus) {
        super(message);
        this.status = status;
        this.typeStatus = typeStatus;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
