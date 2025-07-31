package com.phoebuspaystore.paystore.dto;

import com.phoebuspaystore.paystore.model.Resource;
import lombok.Data;

import java.util.List;

@Data
public class ResourceUpdateDTO {
    private List<Resource> resouces;
}
