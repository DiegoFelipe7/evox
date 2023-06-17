package com.evox.evox.repository;

import com.evox.evox.model.Payments;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsRepository extends ReactiveCrudRepository<Payments , Integer> {
}
