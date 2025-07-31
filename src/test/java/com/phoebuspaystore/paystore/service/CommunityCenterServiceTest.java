package com.phoebuspaystore.paystore.service;

import com.phoebuspaystore.paystore.dto.AverageResourceDTO;
import com.phoebuspaystore.paystore.dto.CommunityCenterUpdateDTO;
import com.phoebuspaystore.paystore.dto.OccupancyUpdateResponseDTO;
import com.phoebuspaystore.paystore.dto.ResourceUpdateResponseDTO;
import com.phoebuspaystore.paystore.model.CommunityCenter;
import com.phoebuspaystore.paystore.model.Resource;
import com.phoebuspaystore.paystore.repository.CommunityCenterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommunityCenterServiceTest {

    @InjectMocks
    private CommunityCenterService service;

    @Mock
    private CommunityCenterRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllCenters() {
        List<CommunityCenter> mockList = List.of(new CommunityCenter(), new CommunityCenter());
        when(repository.findAll()).thenReturn(mockList);

        List<CommunityCenter> result = service.findAll();

        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void shouldReturnCenterById() {
        CommunityCenter center = new CommunityCenter();
        center.setId("123");

        when(repository.findById("123")).thenReturn(Optional.of(center));

        CommunityCenter result = service.getById("123");

        assertEquals("123", result.getId());
        verify(repository).findById("123");
    }

    @Test
    void shouldThrowWhenCenterNotFoundById() {
        when(repository.findById("123")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.getById("123"));
    }

    @Test
    void shouldCreateCenter() {
        CommunityCenter center = new CommunityCenter();
        when(repository.save(center)).thenReturn(center);

        CommunityCenter result = service.create(center);

        assertEquals(center, result);
        verify(repository).save(center);
    }

    @Test
    void shouldUpdateCenter() {
        String id = "1";
        CommunityCenter center = new CommunityCenter();
        center.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(center));

        CommunityCenterUpdateDTO dto = new CommunityCenterUpdateDTO();
        dto.setName("Novo Nome");
        dto.setAddress("Rua A");
        dto.setCapacity(100);
        dto.setLocation("Zona Norte");

        CommunityCenterUpdateDTO result = service.update(id, dto);

        assertEquals(dto.getName(), result.getName());
        verify(repository).save(any(CommunityCenter.class));
    }

    @Test
    void shouldUpdateOccupancyWithSuccess() {
        String id = "10";
        CommunityCenter center = new CommunityCenter();
        center.setId(id);
        center.setCapacity(50);
        when(repository.findById(id)).thenReturn(Optional.of(center));
        when(repository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        OccupancyUpdateResponseDTO response = service.updateOccupancy(id, 60);

        assertEquals("Ocupação atualizada com sucesso: Capacidade máxima excedida.", response.getMessage());
        assertEquals(60, response.getCommunityCenter().getCurrentOccupancy());
    }

    @Test
    void shouldExchangeResourcesWithEqualPoints() {
        CommunityCenter c1 = new CommunityCenter();
        c1.setId("1");
        c1.setCurrentOccupancy(50);// Exemplo de ocupação
        c1.setResources(List.of(
                new Resource("Voluntário", 2, 3), // 6 pontos
                new Resource("Transporte", 1, 5)  // 5 pontos
        )); // Total: 11

        CommunityCenter c2 = new CommunityCenter();
        c2.setId("2");
        c2.setCurrentOccupancy(70);
        c2.setResources(List.of(
                new Resource("Médico", 1, 4),
                new Resource("Kit Médico", 1, 7)
        )); // Total: 11

        when(repository.findById("1")).thenReturn(Optional.of(c1));
        when(repository.findById("2")).thenReturn(Optional.of(c2));

        ResourceUpdateResponseDTO response = service.updateResources("ok", c1, c2);

        assertEquals("ok", response.getMessage());
        verify(repository, times(2)).save(any());

        // Você pode adicionar asserts extras se quiser checar recursos atualizados.
    }


    @Test
    void shouldDeleteCenterWhenFound() {
        CommunityCenter center = new CommunityCenter();
        center.setId("9");

        when(repository.findById("9")).thenReturn(Optional.of(center));

        assertDoesNotThrow(() -> service.delete("9"));
        verify(repository).deleteById("9");
    }

    @Test
    void shouldThrowWhenTryingToDeleteNonexistentCenter() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.delete("x"));
    }

    @Test
    void shouldReturnCentersAbove90() {
        List<CommunityCenter> centers = List.of(new CommunityCenter(), new CommunityCenter());
        when(repository.findCentersWithOccupancyAbove90()).thenReturn(centers);

        List<CommunityCenter> result = service.findCentersWithOccupancyAbove90();

        assertEquals(2, result.size());
        verify(repository).findCentersWithOccupancyAbove90();
    }

    @Test
    void shouldReturnAverageResources() {
        List<AverageResourceDTO> list = List.of(new AverageResourceDTO(), new AverageResourceDTO());
        when(repository.findAverageResources()).thenReturn(list);

        List<AverageResourceDTO> result = service.findAverageResources();

        assertEquals(2, result.size());
        verify(repository).findAverageResources();
    }
}
