package com.evox.evox.validation;
import com.evox.evox.exception.CustomException;
import com.evox.evox.utils.enums.TypeStateResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ObjectValidator {

    private final Validator validator;

    @SneakyThrows
    public <T> T validate (T object) {
        Set<ConstraintViolation<T>> errors = validator.validate(object);
        if(errors.isEmpty())
            return object;
        else {
            String message = errors.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));
            throw new CustomException(HttpStatus.BAD_REQUEST, message , TypeStateResponse.Warning);
        }
    }
    @SneakyThrows
    public <T> List<T> validateList(List<T> objects) {
        List<String> errorMessages = new ArrayList<>();
        for (T object : objects) {
            Set<ConstraintViolation<T>> errors = validator.validate(object);
            if (!errors.isEmpty()) {
                String message = errors.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));
                errorMessages.add(message);
            }
        }

        if (!errorMessages.isEmpty()) {
            String errorMessage = String.join("; ", errorMessages);
            throw new CustomException(HttpStatus.BAD_REQUEST, errorMessage, TypeStateResponse.Warning);
        }

        return objects;
    }
}
