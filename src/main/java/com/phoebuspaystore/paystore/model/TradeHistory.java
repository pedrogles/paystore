package com.phoebuspaystore.paystore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "trade_history")
@Data
public class TradeHistory {
    @Id
    private String id;
    private String firstCenterId;
    private String secondCenterId;
    private List<Resource> resourcesFirstCenter;
    private List<Resource> resourcesSecondCenter;
    private LocalDateTime date;

    public TradeHistory(String firstCenterId, String secondCenterId, List<Resource> resourcesFirstCenter, List<Resource> resourcesSecondCenter, LocalDateTime date) {
        this.firstCenterId = firstCenterId;
        this.secondCenterId = secondCenterId;
        this.resourcesFirstCenter = resourcesFirstCenter;
        this.resourcesSecondCenter = resourcesSecondCenter;
        this.date = date;
    }
}
