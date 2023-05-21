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

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public Mono<TokenDto> login(LoginDto dto) {
        return authRepository.findByEmailIgnoreCase(dto.getEmail())
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "user is not registered", TypeStateResponse.Error)))
                .filter(user -> passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                .flatMap(user -> {
                    if (Boolean.TRUE.equals(user.getStatus())) {
                        return entryRegister(dto).map(ele -> new TokenDto(jwtProvider.generateToken(user)));
                    }
                    return Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "inactive user", TypeStateResponse.Warning));
                })
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "bad credentials", TypeStateResponse.Error)));
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
        return authRepository.findAll()
                .any(existingUser -> existingUser.getCountry().equals(user.getCountry()) && existingUser.getIdentification().equals(user.getIdentification()))
                .flatMap(existingUser -> {
                    if (Boolean.TRUE.equals(existingUser)) {
                        return Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "A user with this ID already exists in the same country.", TypeStateResponse.Error));
                    }
                    return multilevel(user);
                });
    }


    public Mono<Response> multilevel(User user) {
        if (user.getInvitationLink() == null) {
            return authRepository.save(user)
                    .flatMap(ele -> emailService.sendEmailWelcome(ele.getEmail(), ele.getToken())
                                .then(Mono.just(new Response(TypeStateResponse.Success, "We've sent an email to verify your account!" + ele.getFullName()))));
        }
        return authRepository.findByUsernameIgnoreCase(extractUsername(user.getInvitationLink()))
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Reference link does not exist", TypeStateResponse.Warning)))
                .flatMap(parent -> {
                    user.setParentId(parent.getId());
                    return authRepository.save(user).flatMap(ele ->
                         emailService.sendEmailWelcome(ele.getEmail(), ele.getToken())
                                .then(Mono.just(new Response(TypeStateResponse.Success, "We've sent an email to verify your account!" + ele.getFullName()))));
                });

    }
    public Mono<Boolean> validateToken(String token) {
        return Mono.just(jwtProvider.validate(token));
    }


    public Mono<Response> passwordRecovery(LoginDto email) {
        return authRepository.findByEmailIgnoreCase(email.getEmail())
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "user is not registered", TypeStateResponse.Error)))
                .filter(User::getStatus)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "You do not have a verified account", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    ele.setId(ele.getId());
                    ele.setToken(UUID.randomUUID().toString());
                    return authRepository.save(ele)
                            .flatMap(data->
                                 emailService.sendEmailRecoverPassword( data.getEmail() , data.getToken())
                                        .then(Mono.just(new Response(TypeStateResponse.Success, "We sent an email with the option to update your password."))));
                });
    }

    public Mono<Boolean> tokenValidation(String token) {
        return authRepository.findByToken(token)
                .map(User::getStatus)
                .defaultIfEmpty(false);
    }

    public Mono<Response> passwordChange(String token, LoginDto dto) {
        return authRepository.findByToken(token)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "Sorry this token is invalid", TypeStateResponse.Error)))
                .flatMap(ele -> {
                    String encodedPassword = passwordEncoder.encode(dto.getPassword());
                    ele.setId(ele.getId());
                    ele.setPassword(encodedPassword);
                    ele.setToken(null);
                    return authRepository.save(ele);
                }).map(ele -> new Response(TypeStateResponse.Success, "password successfully updated"));
    }

    public Mono<Response> activateAccount(String token) {
        return authRepository.findByToken(token)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "The token is invalid", TypeStateResponse.Error)))
                .filter(user -> !user.getStatus())
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Account is already activated", TypeStateResponse.Warning)))
                .flatMap(user -> {
                    user.setStatus(true);
                    user.setEmailVerified(LocalDateTime.now());
                    return authRepository.save(user)
                            .map(data -> new Response(TypeStateResponse.Success, "Successful account activation"));
                });

    }
    private String extractUsername(String refLink) {
        int position = refLink.lastIndexOf('/');
        return refLink.substring(position + 1);
    }


}
