package com.evox.evox.services;

import com.evox.evox.dto.*;
import com.evox.evox.exception.CustomException;
import com.evox.evox.model.*;
import com.evox.evox.model.enums.AccountState;
import com.evox.evox.model.enums.Category;
import com.evox.evox.model.enums.PaymentsState;
import com.evox.evox.repository.*;
import com.evox.evox.security.jwt.JwtProvider;
import com.evox.evox.utils.Response;
import com.evox.evox.utils.Utils;
import com.evox.evox.utils.enums.TypeStateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class BridgeFundService {
    private final PackagesAccountsRepository packagesAccountsRepository;
    private final BridgeFundsAccountRepository bridgeFundsAccountRepository;
    private final UserRepository userRepository;
    private final BridgeFundsRepository bridgeFundsRepository;
    private final PaymentsRepository paymentsRepository;
    private final JwtProvider jwtProvider;
    public static final Integer bonus = 4;

    public Flux<PackagesAccounts> getBridgeAccountType() {
        return packagesAccountsRepository.findAll()
                .filter(ele->ele.getCategory().equals(Category.BridgeFunds))
                .sort(Comparator.comparing(PackagesAccounts::getPrice));
    }


    public Mono<BridgeFunds> accountActivation(String transaction) {
        return bridgeFundsRepository.findByTransactionEqualsIgnoreCase(transaction)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Este código de transacción no existe!", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    ele.setId(ele.getId());
                    ele.setBridgeFundsState(AccountState.Verified);
                    ele.setState(true);
                    ele.setUpdatedAt(LocalDateTime.now());
                    return registerPayment(ele.getUserId(), ele.getTransaction(), ele.getTotal())
                            .then(bridgeFundsRepository.save(ele))
                            .onErrorMap(throwable -> new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error activando la cuenta.", TypeStateResponse.Error));
                });
    }

    public Mono<Void> registerPayment(Integer userId, String transaction, BigDecimal total) {
        BigDecimal size = total.divide(BigDecimal.TEN, 0, RoundingMode.HALF_UP);
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "El usuario es incorrecto", TypeStateResponse.Error)))
                .flatMapMany(user -> userRepository.findUserAndParents(user.getUsername(), this.bonus))
                .flatMap(data -> {
                    BigDecimal payment = size.multiply(BigDecimal.valueOf(Utils.bonus(data.getLevel()))).setScale(0, RoundingMode.HALF_UP);
                    Payments payments = new Payments(Utils.uid(), transaction, "Bridge Funds", data.getId(), payment, PaymentsState.Pending);
                    return paymentsRepository.save(payments)
                            .onErrorResume(throwable -> Mono.empty());
                })
                .then();
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

    public Mono<BridgeFunds> getTransaction(String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .flatMap(ele -> bridgeFundsRepository.findAll()
                        .filter(data -> data.getUserId().equals(ele.getId()) && data.getBridgeFundsState().equals(AccountState.Error))
                        .next());
    }

    public Mono<Response> registrationTransaction(BridgeFunds bridgeFunds, String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .flatMap(user -> bridgeFundsRepository.findByTransactionEqualsIgnoreCase(bridgeFunds.getTransaction())
                        .flatMap(existingSynthetics -> Mono.error(new CustomException(HttpStatus.BAD_REQUEST,
                                "Ya existe una transacción con estos valores", TypeStateResponse.Error)))
                        .switchIfEmpty(packagesAccountsRepository.findById(bridgeFunds.getBridgeAccountId())
                                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No se seleccionó un tipo de cuenta", TypeStateResponse.Warning)))
                                .map(ele -> {
                                    bridgeFunds.setType("Bridge Funds");
                                    bridgeFunds.setTitle(ele.getTitle());
                                    bridgeFunds.setTotal(new BigDecimal(bridgeFunds.getQuantity()).multiply(ele.getPrice()));
                                    bridgeFunds.setBridgeFundsState(AccountState.Pending);
                                    bridgeFunds.setUserId(user.getId());
                                    return bridgeFunds;
                                })
                                .flatMap(bridgeFundsRepository::save)
                                .map(savedBridgeFunds -> new Response(TypeStateResponse.Success, "Transacción registrada satisfactoriamente!"))

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
                .flatMap(bridgeFunds -> {
                    Mono<User> userMono = userRepository.findById(bridgeFunds.getUserId());
                    Mono<PackagesAccounts> bridgeAccountTypeMono = packagesAccountsRepository.findById(bridgeFunds.getBridgeAccountId());
                    return Mono.zip(userMono, bridgeAccountTypeMono)
                            .map(tuple -> {
                                User user = tuple.getT1();
                                PackagesAccounts packagesAccounts = tuple.getT2();
                                ListBridgeFundUsersDto dto = new ListBridgeFundUsersDto();
                                dto.setId(bridgeFunds.getId());
                                dto.setTransaction(bridgeFunds.getTransaction());
                                dto.setTitle(packagesAccounts.getTitle());
                                dto.setPrice(packagesAccounts.getPrice());
                                dto.setQuantity(bridgeFunds.getQuantity());
                                dto.setTotal(bridgeFunds.getTotal());
                                dto.setCurrency(packagesAccounts.getCurrency());
                                dto.setUsername(user.getUsername());
                                dto.setEmail(user.getEmail());
                                dto.setState(bridgeFunds.getState());
                                return dto;
                            });
                }).sort(Comparator.comparing(ListBridgeFundUsersDto::getId));
    }

    public Mono<Response> saveAccountBridgeFunds(List<BridgeFundsAccount> bridgeFundsAccounts, Integer id) {
        return bridgeFundsRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "El id de la transaccio es invalido, intentalo nuevamente", TypeStateResponse.Error)))
                .flatMap(ele -> {
                    if (ele.getQuantity().equals(bridgeFundsAccounts.size())) {
                        return bridgeFundsAccountRepository.saveAll(bridgeFundsAccounts)
                                .collectList()
                                .map(savedAccounts -> new Response(TypeStateResponse.Success, "Cuentas registradas"));
                    }
                    return Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "La cantidad de cuentas enviadas es invalida", TypeStateResponse.Error));
                });

    }

    public Mono<Boolean> getValidateRegistration(Integer id) {
        return bridgeFundsRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "El id de la transaccio es invalido, intentalo nuevamente", TypeStateResponse.Error)))
                .flatMap(ele -> bridgeFundsAccountRepository.findAll()
                        .filter(data -> data.getBridgeFundsId().equals(ele.getId()))
                        .count()
                        .map(size -> !Objects.equals(ele.getQuantity(), Math.toIntExact(size))));

    }

    public Flux<ListAccountBridgeFundsDto> getAccountsBridgeFunds(String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .flatMapMany(ele -> bridgeFundsRepository.findAll()
                        .filter(data -> data.getUserId().equals(ele.getId()))
                        .flatMap(res -> bridgeFundsAccountRepository.findAll()
                                .filter(account -> account.getBridgeFundsId().equals(res.getId()))
                                .collectList()
                                .map(accounts -> new ListAccountBridgeFundsDto(res.getId(), res.getTitle(), accounts, res.getState()))
                                .flux()
                        )
                );
    }

}
