package com.evox.evox.services;


import com.evox.evox.dto.MultiLevelDto;
import com.evox.evox.dto.TokenDto;
import com.evox.evox.dto.UserDto;
import com.evox.evox.exception.CustomException;
import com.evox.evox.repository.UserRepository;
import com.evox.evox.security.jwt.JwtProvider;
import com.evox.evox.utils.enums.TypeStateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class UserServices {
    private final UserRepository repository;
    private final JwtProvider jwtProvider;

    public Flux<MultiLevelDto> getAllMultilevel(String token) {
        var userName = jwtProvider.extractToken(token);
        return repository.findUserAndDescendants(userName)
                .map(data -> new MultiLevelDto(data.getRefLink(), data.getUsername(), data.getStatus(), data.getCreatedAt()))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "An error has occurred, please contact the administrator", TypeStateResponse.Error)));
    }

    public Mono<TokenDto> editUser(UserDto userDto) {
       return repository.findByEmail(userDto.getEmail())
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "An error occurred, please try again!", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    ele.setId(ele.getId());
                    ele.setPhoto(userDto.getPhoto());
                    ele.setPhone(userDto.getPhone());
                    ele.setCountry(userDto.getCountry());
                    ele.setCity(userDto.getCity());
                     return repository.save(ele).map(data-> new TokenDto(jwtProvider.generateToken(data)));
                });
    }

}
