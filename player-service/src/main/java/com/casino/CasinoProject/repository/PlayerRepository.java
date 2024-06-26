package com.casino.CasinoProject.repository;

import com.casino.CasinoProject.entity.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PlayerRepository extends ReactiveMongoRepository<Player, String> {

    Mono<Player> findByCpf(BigDecimal cpf);
}
