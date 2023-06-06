package com.evox.evox.repository;

import com.evox.evox.model.AccountSynthetics;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSyntheticsRepository extends ReactiveCrudRepository<AccountSynthetics , Integer> {
}
