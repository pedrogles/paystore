package com.phoebuspaystore.paystore.service;

import com.phoebuspaystore.paystore.dto.ResourceUpdateResponseDTO;
import com.phoebuspaystore.paystore.model.CommunityCenter;
import com.phoebuspaystore.paystore.model.Resource;
import com.phoebuspaystore.paystore.model.TradeHistory;
import com.phoebuspaystore.paystore.repository.TradeHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeHistoryServiceTest {

    @InjectMocks
    private TradeHistoryService service;

    @Mock
    private TradeHistoryRepository tradeHistoryRepository;

    @Mock
    private CommunityCenterService communityCenterService;

    private CommunityCenter c1, c2;
    private List<Resource> r1, r2;

    @BeforeEach
    void setup() {
        r1 = List.of(new Resource("Água", 2, 3));     // 6 pontos
        r2 = List.of(new Resource("Comida", 1, 6));   // 6 pontos

        c1 = new CommunityCenter();
        c1.setId("1");
        c1.setCurrentOccupancy(45);
        c1.setCapacity(100);
        c1.setResources(new ArrayList<>(r1));

        c2 = new CommunityCenter();
        c2.setId("2");
        c2.setCurrentOccupancy(30);
        c2.setCapacity(100);
        c2.setResources(new ArrayList<>(r2));
    }

    @Test
    void shouldExchangeResourcesWhenPointsAreEqual() {
        Resource res1 = new Resource("Resource1", 5, 2); // 5 pontos * 2 qty = 10 pontos
        Resource res2 = new Resource("Resource2", 10, 1); // 10 pontos * 1 qty = 10 pontos
        List<Resource> r1 = List.of(res1);
        List<Resource> r2 = List.of(res2);

        // Mock dos centros
        when(communityCenterService.getById("1")).thenReturn(c1);
        when(communityCenterService.getById("2")).thenReturn(c2);

        // Mock do método updateCenter
        when(communityCenterService.updateResources(anyString(), any(CommunityCenter.class), any(CommunityCenter.class)))
                .thenReturn(new ResourceUpdateResponseDTO("Troca de recursos efetuada!", c1, c2));

        // Mock do save para o TradeHistory
        when(tradeHistoryRepository.save(any(TradeHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Chamada do método
        ResourceUpdateResponseDTO response = service.resourceExchange("1", "2", r1, r2);

        // Asserts
        assertEquals("Troca de recursos efetuada!", response.getMessage());

        // Verificar se o método save foi chamado 1 vez no repositório de histórico
        verify(tradeHistoryRepository, times(1)).save(any(TradeHistory.class));
    }

    @Test
    void shouldExchangeResourcesIfOneCenterAbove90Percent() {
        c1.setCurrentOccupancy(91); // força exceção da regra

        List<Resource> lowPoints = List.of(new Resource("Água", 1, 3)); // 3 pts
        List<Resource> highPoints = List.of(new Resource("Comida", 1, 6)); // 6 pts

        when(communityCenterService.getById("1")).thenReturn(c1);
        when(communityCenterService.getById("2")).thenReturn(c2);
        when(communityCenterService.updateResources(any(), any(), any()))
                .thenReturn(new ResourceUpdateResponseDTO("Troca realizada", c1, c2));

        ResourceUpdateResponseDTO response = service.resourceExchange("1", "2", lowPoints, highPoints);

        assertEquals("Troca realizada", response.getMessage());
    }

    @Test
    void shouldThrowWhenPointsAreNotEqualAndNoCenterAbove90Percent() {
        List<Resource> unequal = List.of(new Resource("Comida", 2, 5)); // 10 pts

        when(communityCenterService.getById("1")).thenReturn(c1);
        when(communityCenterService.getById("2")).thenReturn(c2);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.resourceExchange("1", "2", r1, unequal));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Pontuação dos recursos não é equivalente"));
    }

    @Test
    void shouldFindTradeHistoryByCenterAndDate() {
        List<TradeHistory> trades = List.of(
                new TradeHistory("1", "2", r1, r2, LocalDateTime.now())
        );

        when(tradeHistoryRepository.findByCenterIdAndDateAfter(anyString(), any()))
                .thenReturn(trades);

        List<TradeHistory> result = service.findByCenterIdAndDateAfter("1", LocalDateTime.now().minusDays(10));

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowIfCenterIdIsMissingInTradeHistoryFilter() {
        assertThrows(IllegalArgumentException.class,
                () -> service.findByCenterIdAndDateAfter("", LocalDateTime.now()));
    }
}
