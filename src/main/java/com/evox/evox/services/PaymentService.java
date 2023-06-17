package com.evox.evox.services;

import com.evox.evox.model.Payments;
import com.evox.evox.repository.PaymentsRepository;
import com.evox.evox.repository.UserRepository;
import com.evox.evox.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentsRepository paymentsRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public Mono<List<Payments>> listPaymentsUser(String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .flatMap(ele -> paymentsRepository.findAll()
                        .filter(data -> data.getUserId().equals(ele.getId()))
                        .collectList());
    }
}
