package com.phoebuspaystore.paystore.controller;

import com.phoebuspaystore.paystore.dto.*;
import com.phoebuspaystore.paystore.model.TradeHistory;
import com.phoebuspaystore.paystore.service.TradeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/trade-history")
public class TradeHistoryController {
    @Autowired
    private TradeHistoryService service;

    @GetMapping("/all")
    public ResponseEntity<List<TradeHistory>> findAll() {
        try {
            return ResponseEntity.ok(service.findAll());
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(404).body(Collections.emptyList());
        }
    }

    @GetMapping
    public ResponseEntity<?> findByCenterIdAndDateAfter(
        @RequestParam String centerId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {
            try {
                return ResponseEntity.ok(service.findByCenterIdAndDateAfter(centerId, startDate));
            } catch (IllegalArgumentException i) {
                return ResponseEntity.badRequest().body(i.getMessage());
            }
    }

    @PatchMapping("/exchange")
    public ResponseEntity<ResourceUpdateResponseDTO> updateOccupancy(@RequestBody TradeRequestDTO dto) {
        return ResponseEntity.ok(service.resourceExchange(
                dto.getFirstCenterId(),
                dto.getSecondCenterId(),
                dto.getResourcesFirstCenter(),
                dto.getResourcesSecondCenter()));
    }
}
