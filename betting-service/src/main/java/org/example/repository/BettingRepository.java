package org.example.repository;

import org.example.entity.BettingPlayer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BettingRepository extends MongoRepository<BettingPlayer, String> {
}
