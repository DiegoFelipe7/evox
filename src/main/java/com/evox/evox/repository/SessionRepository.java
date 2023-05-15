package com.evox.evox.repository;

import com.evox.evox.model.Session;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository  extends ReactiveCrudRepository<Session, Integer> {
}
