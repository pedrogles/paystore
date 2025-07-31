package com.phoebuspaystore.paystore.repository;

import com.phoebuspaystore.paystore.model.TradeHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TradeHistoryRepository extends MongoRepository<TradeHistory, String> {
    @Query("{ '$or': [ { 'firstCenterId': ?0 }, { 'secondCenterId': ?0 } ], 'date': { $gte: ?1 } }")
    List<TradeHistory> findByCenterIdAndDateAfter(String centerId, LocalDateTime startDate);
}
