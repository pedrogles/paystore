package com.phoebuspaystore.paystore.service;

import com.phoebuspaystore.paystore.dto.AverageResourceDTO;
import com.phoebuspaystore.paystore.dto.CommunityCenterUpdateDTO;
import com.phoebuspaystore.paystore.dto.OccupancyUpdateResponseDTO;
import com.phoebuspaystore.paystore.dto.ResourceUpdateResponseDTO;
import com.phoebuspaystore.paystore.model.CommunityCenter;
import com.phoebuspaystore.paystore.repository.CommunityCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CommunityCenterService {

    @Autowired
    private CommunityCenterRepository repository;

    public List<CommunityCenter> findAll() {
        return repository.findAll();
    }

    public CommunityCenter getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Centro comunitário não encontrado"));
    }

    public List<CommunityCenter> findCentersWithOccupancyAbove90() {
        return repository.findCentersWithOccupancyAbove90();
    }

    public List<AverageResourceDTO> findAverageResources() {
        return repository.findAverageResources();
    }

    public CommunityCenter create(CommunityCenter center) {
        return repository.save(center);
    }

    public CommunityCenterUpdateDTO update(String id, CommunityCenterUpdateDTO updated) {
        CommunityCenter center = getById(id);
        center.setName(updated.getName());
        center.setAddress(updated.getAddress());
        center.setLocation(updated.getLocation());
        center.setCapacity(updated.getCapacity());
        repository.save(center);
        return updated;
    }

    public OccupancyUpdateResponseDTO updateOccupancy (String id, int newOccupancy) {
        String message = "";
        CommunityCenter center = getById(id);
        center.setCurrentOccupancy(newOccupancy);
        CommunityCenter centerUpdated = repository.save(center);
        message = "Ocupação atualizada com sucesso!";
        if(newOccupancy > center.getCapacity()) {
            message = "Ocupação atualizada com sucesso: Capacidade máxima excedida.";
        };
        return new OccupancyUpdateResponseDTO(message, centerUpdated);
    }

    public ResourceUpdateResponseDTO updateResources (String message, CommunityCenter firstCenter, CommunityCenter secondCenter) {
        CommunityCenter fCenter = getById(firstCenter.getId());
        CommunityCenter sCenter = getById(secondCenter.getId());
        fCenter.setResources(firstCenter.getResources());
        sCenter.setResources(secondCenter.getResources());
        repository.save(fCenter);
        repository.save(sCenter);
        return new ResourceUpdateResponseDTO(message, firstCenter, secondCenter);
    }

    public void delete(String id) {
        Optional<CommunityCenter> center = repository.findById(id);
        if (center.isEmpty()) throw new RuntimeException("Centro não encontrado");

        repository.deleteById(id);
    }
}
