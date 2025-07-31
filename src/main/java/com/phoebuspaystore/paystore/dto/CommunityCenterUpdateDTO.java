package com.phoebuspaystore.paystore.dto;

import lombok.Data;

@Data
public class CommunityCenterUpdateDTO {
    private String name;
    private String address;
    private String location;
    private int capacity;
}
