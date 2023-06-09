package com.evox.evox.repository;

import com.evox.evox.model.Support;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportRepository extends ReactiveCrudRepository<Support , Integer> {
}
