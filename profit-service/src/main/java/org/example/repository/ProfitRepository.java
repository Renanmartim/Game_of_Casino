package org.example.repository;

import org.example.entity.Profit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfitRepository extends MongoRepository<Profit, Long> {
    @Query("SELECT profit_casino FROM Profit")
    Long getProfit();

    @Query("SELECT * FROM Profit")
    Profit getObject();
}
