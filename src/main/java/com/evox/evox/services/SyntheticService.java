package com.evox.evox.services;

import com.evox.evox.dto.ListSyntheticUsersDto;
import com.evox.evox.dto.SyntheticAccessDto;
import com.evox.evox.exception.CustomException;
import com.evox.evox.model.Synthetics;
import com.evox.evox.model.enums.AccountState;
import com.evox.evox.repository.AccountSyntheticsRepository;
import com.evox.evox.repository.SyntheticsRepository;
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
public class SyntheticService {
    private final SyntheticsRepository syntheticsRepository;
    private final AccountSyntheticsRepository accountSyntheticsRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public Mono<Synthetics> accountActivation(String transaction) {
        return syntheticsRepository.findByTransactionEqualsIgnoreCase(transaction)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Esta codigo de transaccion no existe!", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    if (Boolean.TRUE.equals(ele.getState())) {
                        return Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "La cuenta ya está activa", TypeStateResponse.Warning));
                    } else {
                        ele.setId(ele.getId());
                        ele.setSyntheticState(AccountState.Verified);
                        ele.setActivationDate(LocalDateTime.now());
                        ele.setExpirationDate(ele.getCreatedAt().plusMonths(1));
                        ele.setState(true);
                        ele.setUpdatedAt(LocalDateTime.now());
                        return syntheticsRepository.save(ele);
                    }
                });
    }

    public Mono<Response> registrationTransaction(Synthetics synthetics, String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .flatMap(user -> syntheticsRepository.findByTransactionEqualsIgnoreCase(synthetics.getTransaction())
                        .flatMap(existingSynthetics -> Mono.error(new CustomException(HttpStatus.BAD_REQUEST,
                                "Ya existe una transacción con estos valores", TypeStateResponse.Error)))
                        .switchIfEmpty(Mono.defer(() -> {
                            synthetics.setType("Synthetic");
                            synthetics.setSyntheticState(AccountState.Pending);
                            synthetics.setUserId(user.getId());
                            return syntheticsRepository.save(synthetics);
                        })) .thenReturn(new Response(TypeStateResponse.Success, "Transacción registrada satisfactoriamente!")));


    }



    public Flux<ListSyntheticUsersDto> getAllSynthetics() {
        return syntheticsRepository.findAll()
                .filter(ele -> ele.getSyntheticState().equals(AccountState.Pending))
                .flatMap(synthetic -> userRepository.findById(synthetic.getUserId())
                        .map(user -> new ListSyntheticUsersDto(
                                synthetic.getType(),
                                synthetic.getTransaction(),
                                synthetic.getCurrency(),
                                synthetic.getPrice(),
                                synthetic.getCreatedAt(),
                                synthetic.getExpirationDate(),
                                synthetic.getState(),
                                user.getUsername(),
                                user.getEmail()
                        )));
    }

    public Mono<AccountState> getStateUser(String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .flatMap(user -> syntheticsRepository.findAll()
                        .filter(ele -> ele.getUserId().equals(user.getId()) && !ele.getSyntheticState().equals(AccountState.Completed))
                        .next()
                        .map(Synthetics::getSyntheticState)
                        .defaultIfEmpty(AccountState.Shopping));
    }

    public Mono<Response> invalidTransaction(String transaction) {
        return syntheticsRepository.findByTransactionEqualsIgnoreCase(transaction)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Esta codigo de transaccion no existe!", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    ele.setId(ele.getId());
                    ele.setSyntheticState(AccountState.Error);
                    return syntheticsRepository.save(ele)
                            .thenReturn(new Response(TypeStateResponse.Success, "Transaccion invalidada!"));
                });

    }

    public Mono<Synthetics> getIdSyntheticUser(String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST , "El usuario de filtrado es incorrecto!" , TypeStateResponse.Error)))
                .flatMap(user -> syntheticsRepository.findAll()
                        .filter(ele -> ele.getUserId().equals(user.getId()) && ele.getSyntheticState().equals(AccountState.Error))
                        .next());
    }


    public Flux<SyntheticAccessDto> syntheticAccess(){
        return userRepository.findAll().
                filter(ele->ele.getAccountSynthetics()!=null)
                .flatMap(ele->
                        accountSyntheticsRepository.findById(ele.getAccountSynthetics())
                                .map(data->new SyntheticAccessDto(ele.getUsername(),ele.getEmail(),data.getLogin(),data.getPassword(),data.getCreatedAt(),data.getState())));


    }


}
