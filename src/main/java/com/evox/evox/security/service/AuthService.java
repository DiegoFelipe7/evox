package com.evox.evox.security.service;


import com.evox.evox.dto.LoginDto;
import com.evox.evox.dto.TokenDto;
import com.evox.evox.exception.CustomException;
import com.evox.evox.model.Session;
import com.evox.evox.model.User;
import com.evox.evox.model.enums.Role;
import com.evox.evox.repository.SessionRepository;
import com.evox.evox.security.jwt.JwtProvider;
import com.evox.evox.security.repository.AuthRepository;
import com.evox.evox.utils.Response;
import com.evox.evox.utils.Utils;
import com.evox.evox.utils.enums.TypeStateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final SessionRepository sessionRepository;
    private final EmailService emailService;
    @Value("${evox.url}")
    private String url;
    private Utils utils;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public Mono<TokenDto> login(LoginDto dto) {
        return authRepository.findByEmailIgnoreCase(dto.getEmail())
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "El usuario no esta registrado!", TypeStateResponse.Error)))
                .filter(user -> passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                .flatMap(user -> {
                    if (Boolean.TRUE.equals(user.getStatus())) {
                        return entryRegister(dto).map(ele -> new TokenDto(jwtProvider.generateToken(user)));
                    }
                    return Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "El usuario tiene la cuenta inactiva", TypeStateResponse.Warning));
                })
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Contraseña invalida!", TypeStateResponse.Error)));
    }

    public Mono<Session> entryRegister(LoginDto dto) {
        return authRepository.findByEmailIgnoreCase(dto.getEmail())
                .flatMap(ele -> {
                    Session session = new Session(ele.getId(), dto.getIpAddress(), dto.getCountry(), dto.getBrowser(), LocalDateTime.now());
                    return sessionRepository.save(session);
                });
    }

    public Mono<Response> accountRegistration(User user) {
        user.setRefLink(url + user.getUsername());
        user.setRoles(Role.ROLE_ADMIN.name());
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setToken(UUID.randomUUID().toString());
        if (user.getInvitationLink() == null) {
            return authRepository.save(user)
                    .flatMap(ele -> emailService.sendEmailWelcome(ele.getFullName(),ele.getEmail(), ele.getToken())
                            .then(Mono.just(new Response(TypeStateResponse.Success, "Hemos enviado un correo electrónico para verificar su tu cuenta!" + ele.getFullName()))));
        }
        return referral(user);
    }


    public Mono<Response> referral(User user) {
        return authRepository.findByUsernameIgnoreCase(Utils.extractUsername(user.getInvitationLink()))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "El link de referido no existe!", TypeStateResponse.Warning)))
                .flatMap(parent -> {
                    user.setParentId(parent.getId());
                    return authRepository.save(user).flatMap(ele ->
                         emailService.sendEmailWelcome(ele.getFullName(),ele.getEmail(), ele.getToken())
                                .then(Mono.just(new Response(TypeStateResponse.Success, "Hemos enviado un correo electrónico para verificar su tu cuenta!!" + ele.getFullName()))));
                });

    }
    public Mono<Boolean> validateToken(String token) {
        return Mono.just(jwtProvider.validate(token));
    }


    public Mono<Response> passwordRecovery(LoginDto email) {
        return authRepository.findByEmailIgnoreCase(email.getEmail())
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "El usuario no se encuentra registrado!", TypeStateResponse.Error)))
                .filter(User::getStatus)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No tienes tu cuenta verificada", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    ele.setId(ele.getId());
                    ele.setToken(UUID.randomUUID().toString());
                    return authRepository.save(ele)
                            .flatMap(data->
                                 emailService.sendEmailRecoverPassword(data.getFullName(), data.getEmail() , data.getToken())
                                        .then(Mono.just(new Response(TypeStateResponse.Success, "se envió un correo electrónico con la opción de actualizar su contraseña."))));
                });
    }

    public Mono<Boolean> tokenValidation(String token) {
        return authRepository.findByToken(token)
                .map(User::getStatus)
                .defaultIfEmpty(false);
    }

    public Mono<Response> passwordChange(String token, LoginDto dto) {
        return authRepository.findByToken(token)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "Lo sentimos, el token es invalido!", TypeStateResponse.Error)))
                .flatMap(ele -> {
                    String encodedPassword = passwordEncoder.encode(dto.getPassword());
                    ele.setId(ele.getId());
                    ele.setPassword(encodedPassword);
                    ele.setToken(null);
                    return authRepository.save(ele);
                }).map(ele -> new Response(TypeStateResponse.Success, "Contraseña actualizada!"));
    }

    public Mono<Response> activateAccount(String token) {
        return authRepository.findByToken(token)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "El token es invalido!", TypeStateResponse.Error)))
                .filter(user -> !user.getStatus())
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "La cuenta ya esta activa!", TypeStateResponse.Warning)))
                .flatMap(user -> {
                    user.setStatus(true);
                    user.setToken(null);
                    user.setEmailVerified(LocalDateTime.now());
                    return authRepository.save(user)
                            .map(data -> new Response(TypeStateResponse.Success, "Activación correcta de la cuenta"));
                });

    }



}
