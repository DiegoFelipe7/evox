package com.evox.evox.services;

import com.evox.evox.dto.ListBridgeFundUsersDto;
import com.evox.evox.dto.ListSyntheticUsersDto;
import com.evox.evox.exception.CustomException;
import com.evox.evox.model.BridgeAccountType;
import com.evox.evox.model.BridgeFunds;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BridgeFundService {
    private final BridgeAccountTypeRepository bridgeAccountTypeRepository;
    private final UserRepository userRepository;
    private final BridgeFundsRepository bridgeFundsRepository;
    private final JwtProvider jwtProvider;
    public Flux<BridgeAccountType> getBridgeAccountType(){
        return bridgeAccountTypeRepository.findAll();
    }


    public Mono<BridgeFunds> accountActivation(String transaction) {
        return bridgeFundsRepository.findByTransaction(transaction)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Esta codigo de transaccion no existe!", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                        ele.setId(ele.getId());
                        ele.setBridgeFundsState(AccountState.Verified);
                        ele.setState(true);
                        ele.setUpdatedAt(LocalDateTime.now());
                        return bridgeFundsRepository.save(ele);

                });
    }

    public Mono<Response> registrationTransaction(BridgeFunds bridgeFunds, String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .flatMap(user -> bridgeFundsRepository.findByTransaction(bridgeFunds.getTransaction())
                        .flatMap(existingSynthetics -> Mono.error(new CustomException(HttpStatus.BAD_REQUEST,
                                "Ya existe una transacción con estos valores", TypeStateResponse.Error)))
                        .switchIfEmpty(Mono.defer(() -> {
                            bridgeFunds.setBridgeFundsState(AccountState.Pending);
                            bridgeFunds.setUserId(user.getId());
                            return bridgeFundsRepository.save(bridgeFunds);
                        })) .thenReturn(new Response(TypeStateResponse.Success, "Transacción registrada satisfactoriamente!")));


    }
    public Mono<Response> invalidTransaction(String transaction) {
        return bridgeFundsRepository.findByTransaction(transaction)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Esta codigo de transaccion no existe!", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    ele.setId(ele.getId());
                    ele.setBridgeFundsState(AccountState.Error);
                    return bridgeFundsRepository.save(ele)
                            .thenReturn(new Response(TypeStateResponse.Success, "Transaccion invalidada!"));
                });

    }

    public Flux<ListSyntheticUsersDto> getAllBridgeFunds() {
        return bridgeFundsRepository.findAll()
                .filter(ele -> ele.getBridgeFundsState().equals(AccountState.Pending))
                .zipWith()
    }

}
