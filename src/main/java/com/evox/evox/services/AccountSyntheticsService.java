package com.evox.evox.services;

import com.evox.evox.model.AccountSynthetics;
import com.evox.evox.repository.AccountSyntheticsRepository;
import com.evox.evox.repository.UserRepository;
import com.evox.evox.security.jwt.JwtProvider;
import com.evox.evox.utils.Response;
import com.evox.evox.utils.enums.TypeStateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountSyntheticsService {
    private final JwtProvider jwtProvider;
    private final AccountSyntheticsRepository accountSyntheticsRepository;
    private final UserRepository userRepository;


    public Mono<Response> registerSyntheticAccount(String token, AccountSynthetics accountSynthetics) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .flatMap(user ->
                        accountSyntheticsRepository.save(accountSynthetics)
                                .flatMap(savedAccountSynthetics -> {
                                    user.setAccountSynthetics(savedAccountSynthetics.getId());
                                    return userRepository.save(user);
                                })
                                .thenReturn(new Response(TypeStateResponse.Success, "Cuenta vinculada exitosamente!")));
    }
}
