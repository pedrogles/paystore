package com.phoebuspaystore.paystore.dto;

import com.phoebuspaystore.paystore.model.CommunityCenter;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResourceUpdateResponseDTO {
    private String message;
    private CommunityCenter firstCenter;
    private CommunityCenter secondCenter;
}
