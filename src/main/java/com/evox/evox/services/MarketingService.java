package com.evox.evox.services;

import com.evox.evox.exception.CustomException;
import com.evox.evox.model.Marketing;
import com.evox.evox.model.PackagesAccounts;
import com.evox.evox.model.Synthetics;
import com.evox.evox.model.enums.AccountState;
import com.evox.evox.model.enums.Category;
import com.evox.evox.repository.MarketingRepository;
import com.evox.evox.repository.PackagesAccountsRepository;
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
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class MarketingService {
    private final MarketingRepository marketingRepository;
    private final PackagesAccountsRepository packagesAccountsRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public Mono<Marketing> accountActivation(String transaction) {
        return marketingRepository.findByTransactionEqualsIgnoreCase(transaction)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Esta codigo de transaccion no existe!", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    if (Boolean.TRUE.equals(ele.getState())) {
                        return Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "La cuenta ya está activa", TypeStateResponse.Warning));
                    } else {
                        ele.setId(ele.getId());
                        ele.setMarketingState(AccountState.Verified);
                        ele.setActivationDate(LocalDateTime.now());
                        ele.setExpirationDate(ele.getCreatedAt().plusMonths(1));
                        ele.setState(true);
                        ele.setUpdatedAt(LocalDateTime.now());
                        return marketingRepository.save(ele);
                    }
                });
    }


    public Flux<PackagesAccounts> findAllMarketingAccount(){
        return packagesAccountsRepository.findAll()
                .filter(ele->ele.getCategory().equals(Category.EvoxMarketing))
                .sort(Comparator.comparing(PackagesAccounts::getPrice));
    }


    public Mono<Response> saveTransaction(Marketing marketing, String token) {
        return packagesAccountsRepository.findById(marketing.getMarketingAccountId())
                .switchIfEmpty(Mono.error( new CustomException(HttpStatus.BAD_REQUEST, "El plan seleccionado es invalido!",TypeStateResponse.Warning)))
                .flatMap(packagesAccount -> {
                    var price = packagesAccount.getPrice();
                    String username = jwtProvider.extractToken(token);
                    return userRepository.findByUsername(username)
                            .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST,
                                    "No se encontró ningún usuario con el nombre de usuario dado", TypeStateResponse.Error)))
                            .flatMap(user -> marketingRepository.findByTransactionEqualsIgnoreCase(marketing.getTransaction())
                                    .flatMap(existingMarketing -> Mono.error(new CustomException(HttpStatus.BAD_REQUEST,
                                            "Ya existe una transacción con estos valores", TypeStateResponse.Error)))
                                    .switchIfEmpty(Mono.defer(() -> {
                                        marketing.setType("Evox Marketing");
                                        marketing.setPrice(price.intValue());
                                        marketing.setMarketingState(AccountState.Pending);
                                        marketing.setUserId(user.getId());
                                        return marketingRepository.save(marketing);
                                    })))
                            .thenReturn(new Response(TypeStateResponse.Success, "Transacción registrada satisfactoriamente!"));
                });

    }

    public Mono<AccountState> getStateMarketingUser(String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .flatMap(user -> marketingRepository.findAll()
                        .filter(ele -> ele.getUserId().equals(user.getId()) && !ele.getMarketingState().equals(AccountState.Completed))
                        .next()
                        .map(Marketing::getMarketingState)
                        .defaultIfEmpty(AccountState.Shopping));
    }
    public Mono<Response> invalidTransaction(String transaction) {
        return marketingRepository.findByTransactionEqualsIgnoreCase(transaction)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Esta codigo de transaccion no existe!", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    ele.setId(ele.getId());
                    ele.setMarketingState(AccountState.Error);
                    ele.setState(false);
                    return marketingRepository.save(ele)
                            .thenReturn(new Response(TypeStateResponse.Success, "Transaccion invalidada!"));
                });

    }
    public Mono<Marketing> getIdEvoxMarketingUser(String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST , "El usuario de filtrado es incorrecto!" , TypeStateResponse.Error)))
                .flatMap(user -> marketingRepository.findAll()
                        .filter(ele -> ele.getUserId().equals(user.getId()) && ele.getMarketingState().equals(AccountState.Error))
                        .next());
    }


}
