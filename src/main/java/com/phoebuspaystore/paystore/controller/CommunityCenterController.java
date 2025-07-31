package com.phoebuspaystore.paystore.controller;

import com.phoebuspaystore.paystore.dto.AverageResourceDTO;
import com.phoebuspaystore.paystore.dto.CommunityCenterUpdateDTO;
import com.phoebuspaystore.paystore.dto.OccupancyUpdateDTO;
import com.phoebuspaystore.paystore.dto.OccupancyUpdateResponseDTO;
import com.phoebuspaystore.paystore.model.CommunityCenter;
import com.phoebuspaystore.paystore.service.CommunityCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community-centers")
public class CommunityCenterController {

    @Autowired
    private CommunityCenterService service;

    @GetMapping
    public ResponseEntity<List<CommunityCenter>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/average-resources")
    public ResponseEntity<List<AverageResourceDTO>> findAverageResources() {
        return ResponseEntity.ok(service.findAverageResources());
    }

    @GetMapping("/occupancy/above-90")
    public ResponseEntity<List<CommunityCenter>> findCentersWithOccupancyAbove90() {
        return ResponseEntity.ok(service.findCentersWithOccupancyAbove90());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CommunityCenter center) {
        try {
            CommunityCenter created = service.create(center);
            return ResponseEntity.status(201).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erro ao criar centro: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public CommunityCenterUpdateDTO update(@PathVariable String id, @RequestBody CommunityCenterUpdateDTO updated) {
        return service.update(id, updated);
    }

    @PatchMapping("/occupancy/{id}")
    public ResponseEntity<OccupancyUpdateResponseDTO> updateOccupancy(@PathVariable String id, @RequestBody OccupancyUpdateDTO dto) {
        return ResponseEntity.ok(service.updateOccupancy(id, dto.getCurrentOccupancy()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
