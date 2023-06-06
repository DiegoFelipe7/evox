package com.evox.evox.repository;

import com.evox.evox.model.Synthetics;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SyntheticsRepository extends ReactiveCrudRepository<Synthetics , Integer> {
    Mono<Synthetics> findByTransactionEqualsIgnoreCase(String transaction);


}
