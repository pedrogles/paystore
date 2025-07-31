package com.phoebuspaystore.paystore.dto;

import com.phoebuspaystore.paystore.model.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TradeRequestDTO {
    private String firstCenterId;
    private String secondCenterId;
    private List<Resource> resourcesFirstCenter;
    private List<Resource> resourcesSecondCenter;
}
