package com.phoebuspaystore.paystore.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "community_centers")
@Data
public class CommunityCenter {
    @Id
    private String id;
    private String name;
    private String address;
    private String location;
    private int capacity;
    private int currentOccupancy;
    private List<Resource> resources;
}
