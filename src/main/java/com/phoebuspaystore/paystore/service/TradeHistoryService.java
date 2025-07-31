package com.phoebuspaystore.paystore.service;

import com.phoebuspaystore.paystore.dto.ResourceUpdateResponseDTO;
import com.phoebuspaystore.paystore.model.CommunityCenter;
import com.phoebuspaystore.paystore.model.Resource;
import com.phoebuspaystore.paystore.model.TradeHistory;
import com.phoebuspaystore.paystore.repository.TradeHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class TradeHistoryService {
    @Autowired
    private TradeHistoryRepository tradeHistoryRepository;

    @Autowired
    private CommunityCenterService communityCenterService;

    public List<TradeHistory> findAll() {
        List<TradeHistory> trades = tradeHistoryRepository.findAll();
        if (trades.isEmpty())
            throw new EmptyResultDataAccessException("Nenhuma troca encontrado", 1);

        return trades;
    }

    public List<TradeHistory> findByCenterIdAndDateAfter(String centerId, LocalDateTime startDate) {
        if (centerId == null || centerId.isBlank()) {
            throw new IllegalArgumentException("O campo 'id' é obrigatório.");
        }

        LocalDateTime date =
                Objects.requireNonNullElseGet(startDate, () -> LocalDateTime.now().minusDays(30));

        return tradeHistoryRepository.findByCenterIdAndDateAfter(centerId, date);
    }

    private TradeHistory create(TradeHistory trade) {
        TradeHistory newTrade  = new TradeHistory(
            trade.getFirstCenterId(),
            trade.getSecondCenterId(),
            trade.getResourcesFirstCenter(),
            trade.getResourcesSecondCenter(),
            LocalDateTime.now()
        );
        return tradeHistoryRepository.save(newTrade);
    }

    public ResourceUpdateResponseDTO resourceExchange(String firstCenterId, String secondCenterId, List<Resource> resourcesFirstCenter, List<Resource> resourcesSecondCenter) {

        CommunityCenter firstCenter = communityCenterService.getById(firstCenterId);
        CommunityCenter secondCenter = communityCenterService.getById(secondCenterId);

        CommunityCenter firstCenterUpdated = null;
        CommunityCenter secondCenterUpdated = null;

        String message = "";

        int firstCenterPoints = 0;
        int secondCenterPoints = 0;

        for (Resource resouce : resourcesFirstCenter)
            firstCenterPoints += resouce.getPoints() * resouce.getQuantity();
        for (Resource resouce : resourcesSecondCenter)
            secondCenterPoints += resouce.getPoints() * resouce.getQuantity();

        boolean firstOccupancyPercentageCenter = occupancyPercentageMoreThan90(firstCenter);
        boolean secondOccupancyPercentageCenter = occupancyPercentageMoreThan90(secondCenter);

        if (firstCenterPoints == secondCenterPoints || (firstOccupancyPercentageCenter || secondOccupancyPercentageCenter)) {

            firstCenterUpdated = updateCenter(
                firstCenter,
                resourcesFirstCenter,
                resourcesSecondCenter);

            secondCenterUpdated = updateCenter(
                secondCenter,
                resourcesSecondCenter,
                resourcesFirstCenter);

            message = "Troca de recursos efetuada!";

            TradeHistory trade = new TradeHistory(
                firstCenterId,
                secondCenterId,
                resourcesFirstCenter,
                resourcesSecondCenter,
                LocalDateTime.now());

            create(trade);
        } else {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Pontuação dos recursos não é equivalente para troca. Verifique os itens ofertados por ambos os centros.");
        }

        return communityCenterService.updateResources(message, firstCenterUpdated, secondCenterUpdated);
    }

    private static boolean occupancyPercentageMoreThan90(CommunityCenter center) {
        return ((double) center.getCurrentOccupancy() / center.getCapacity()) * 100 >= 90;
    }

    private CommunityCenter updateCenter( CommunityCenter currentCenter, List<Resource> resourcesCurrentCenter, List<Resource> resourcesSecondCenter) {

        for (Resource currentResource : currentCenter.getResources()) {
            Resource newFirstCenterResource = resourcesCurrentCenter
                .stream().filter(r -> r.getName().equals(currentResource.getName()))
                .findFirst()
                .orElse(null);
            Resource newSecondCenterResource = resourcesSecondCenter
                .stream().filter(r -> r.getName().equals(currentResource.getName()))
                .findFirst()
                .orElse(null);
            if (newFirstCenterResource != null) {
                if (currentResource.getQuantity() - newFirstCenterResource.getQuantity() <= 0) {
                    throw new IllegalStateException("Quantidade do recurso não pode ser menor ou igual a zero após troca.");
                }
                currentResource.setQuantity(currentResource.getQuantity() - newFirstCenterResource.getQuantity());
            }
            else if (newSecondCenterResource != null) {
                currentResource.setQuantity(currentResource.getQuantity() + newSecondCenterResource.getQuantity());
            }
        }

        return currentCenter;
    }
}
