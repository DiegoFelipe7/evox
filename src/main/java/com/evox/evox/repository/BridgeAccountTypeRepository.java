package com.evox.evox.repository;

import com.evox.evox.model.BridgeAccountType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BridgeAccountTypeRepository extends ReactiveCrudRepository<BridgeAccountType , Integer> {
}
