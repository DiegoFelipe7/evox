package com.evox.evox.services;

import com.evox.evox.dto.ListBridgeFundUsersDto;
import com.evox.evox.dto.ListSyntheticUsersDto;
import com.evox.evox.exception.CustomException;
import com.evox.evox.model.BridgeAccountType;
import com.evox.evox.model.BridgeFunds;
import com.evox.evox.model.Synthetics;
import com.evox.evox.model.User;
import com.evox.evox.model.enums.AccountState;
import com.evox.evox.repository.BridgeAccountTypeRepository;
import com.evox.evox.repository.BridgeFundsRepository;
import com.evox.evox.repository.UserRepository;
import com.evox.evox.security.jwt.JwtProvider;
import com.evox.evox.utils.Response;
import com.evox.evox.utils.enums.TypeStateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BridgeFundService {
    private final BridgeAccountTypeRepository bridgeAccountTypeRepository;
    private final UserRepository userRepository;
    private final BridgeFundsRepository bridgeFundsRepository;
    private final JwtProvider jwtProvider;

    public Flux<BridgeAccountType> getBridgeAccountType() {
        return bridgeAccountTypeRepository.findAll();
    }


    public Mono<BridgeFunds> accountActivation(String transaction) {
        return bridgeFundsRepository.findByTransactionEqualsIgnoreCase(transaction)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Esta codigo de transaccion no existe!", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    ele.setId(ele.getId());
                    ele.setBridgeFundsState(AccountState.Verified);
                    ele.setState(true);
                    ele.setUpdatedAt(LocalDateTime.now());
                    return bridgeFundsRepository.save(ele);
                });
    }

    public Mono<AccountState> getStateUser(String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .flatMap(user -> bridgeFundsRepository.findAll()
                        .filter(ele -> ele.getUserId().equals(user.getId()) && !ele.getBridgeFundsState().equals(AccountState.Completed))
                        .next()
                        .map(BridgeFunds::getBridgeFundsState)
                        .defaultIfEmpty(AccountState.Shopping));
    }

    public Mono<Response> registrationTransaction(BridgeFunds bridgeFunds, String token) {
        String username = jwtProvider.extractToken(token);
       return userRepository.findByUsername(username)
                .flatMap(user -> bridgeFundsRepository.findByTransactionEqualsIgnoreCase(bridgeFunds.getTransaction())
                        .flatMap(existingSynthetics -> Mono.error(new CustomException(HttpStatus.BAD_REQUEST,
                                "Ya existe una transacción con estos valores", TypeStateResponse.Error)))
                        .switchIfEmpty(bridgeAccountTypeRepository.findById(bridgeFunds.getBridgeAccountId())
                                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No se seleccionó un tipo de cuenta", TypeStateResponse.Warning)))
                                .map(ele -> {
                                    bridgeFunds.setTotal(new BigDecimal(bridgeFunds.getQuantity()).multiply(ele.getPrice()));
                                    bridgeFunds.setBridgeFundsState(AccountState.Pending);
                                    bridgeFunds.setUserId(user.getId());
                                    return bridgeFunds;
                                })
                                .flatMap(bridgeFundsRepository::save).map(savedBridgeFunds -> new Response(TypeStateResponse.Success, "Transacción registrada satisfactoriamente!"))

                        ).cast(Response.class)
                );
    }


    public Mono<Response> invalidTransaction(String transaction) {
        return bridgeFundsRepository.findByTransactionEqualsIgnoreCase(transaction)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Esta codigo de transaccion no existe!", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    ele.setId(ele.getId());
                    ele.setBridgeFundsState(AccountState.Error);
                    return bridgeFundsRepository.save(ele)
                            .thenReturn(new Response(TypeStateResponse.Success, "Transaccion invalidada!"));
                });

    }

    public Flux<ListBridgeFundUsersDto> getAllBridgeFunds() {
        return bridgeFundsRepository.findAll()
                .filter(ele -> ele.getBridgeFundsState().equals(AccountState.Pending))
                .flatMap(bridgeFunds -> {
                    Mono<User> userMono = userRepository.findById(bridgeFunds.getUserId());
                    Mono<BridgeAccountType> bridgeAccountTypeMono = bridgeAccountTypeRepository.findById(bridgeFunds.getBridgeAccountId());
                    return Mono.zip(userMono, bridgeAccountTypeMono)
                            .map(tuple -> {
                                User user = tuple.getT1();
                                BridgeAccountType bridgeAccountType = tuple.getT2();
                                ListBridgeFundUsersDto dto = new ListBridgeFundUsersDto();
                                dto.setTransaction(bridgeFunds.getTransaction());
                                dto.setTitle(bridgeAccountType.getTitle());
                                dto.setPrice(bridgeAccountType.getPrice());
                                dto.setQuantity(bridgeFunds.getQuantity());
                                dto.setTotal(bridgeFunds.getTotal());
                                dto.setCurrency(bridgeAccountType.getCurrency());
                                dto.setUsername(user.getUsername());
                                dto.setEmail(user.getEmail());
                                dto.setState(bridgeFunds.getState());
                                return dto;
                            });
                });
    }
}