package org.example.repository;

import org.example.entity.BettingPlayer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface BettingRepository extends ReactiveMongoRepository<BettingPlayer, String> {
    Mono<BettingPlayer> findByNumber(Long number);

    Mono<BettingPlayer> findByCpf(BigDecimal cpf);
}
