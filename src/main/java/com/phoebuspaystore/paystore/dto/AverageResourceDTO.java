package com.phoebuspaystore.paystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AverageResourceDTO {
    private String resource;
    private Double averageQuantity;
}
