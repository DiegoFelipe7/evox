package com.evox.evox.repository;

import com.evox.evox.model.Marketing;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MarketingRepository extends ReactiveCrudRepository<Marketing , Integer> {
    Mono<Marketing> findByTransactionEqualsIgnoreCase(String transaction);

}
