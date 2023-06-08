package com.evox.evox.repository;

import com.evox.evox.model.BridgeFundsAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BridgeFundsAccountRepository extends ReactiveCrudRepository<BridgeFundsAccount , Integer> {
}
