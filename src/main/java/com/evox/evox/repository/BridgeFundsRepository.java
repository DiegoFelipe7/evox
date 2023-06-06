package com.evox.evox.repository;

import com.evox.evox.model.BridgeFunds;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BridgeFundsRepository extends ReactiveCrudRepository<BridgeFunds, Integer> {

    Mono<BridgeFunds> findByTransaction(String transaction);
}
