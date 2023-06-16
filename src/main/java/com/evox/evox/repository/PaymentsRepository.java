package com.evox.evox.repository;

import com.evox.evox.model.Payments;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PaymentsRepository extends ReactiveCrudRepository<Payments , Integer> {
}
