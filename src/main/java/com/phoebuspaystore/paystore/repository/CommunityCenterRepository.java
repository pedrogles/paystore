package com.phoebuspaystore.paystore.repository;

import com.phoebuspaystore.paystore.dto.AverageResourceDTO;
import com.phoebuspaystore.paystore.model.CommunityCenter;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CommunityCenterRepository extends MongoRepository<CommunityCenter, String> {
    @Query("{ $expr: { $gte: [ { $divide: [\"$currentOccupancy\", \"$capacity\"] }, 0.9 ] } }")
    List<CommunityCenter> findCentersWithOccupancyAbove90();

    @Aggregation(pipeline = {
            "{ '$unwind': '$resources' }",
            "{ '$group': { '_id': '$resources.name', 'totalQuantity': { $sum: '$resources.quantity' }, 'count': { $sum: 1 } } }",
            "{ '$project': { '_id': 0, 'resource': '$_id', 'averageQuantity': { $divide: ['$totalQuantity', '$count'] } } }"
    })
    List<AverageResourceDTO> findAverageResources();
}
