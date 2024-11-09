package com.devstaff.test.farmCollector.service;

import com.devstaff.test.farmCollector.dao.FarmRepository;
import com.devstaff.test.farmCollector.model.Farm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FarmServiceTest {

    @Mock
    private FarmRepository farmRepository;

    @InjectMocks
    private FarmService farmService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveFarm() {
        // Arrange
        Farm farm = new Farm();
        farm.setName("MyFarm");

        // Mock the repository's save method
        when(farmRepository.save(any(Farm.class))).thenReturn(farm);

        // Act
        Farm savedFarm = farmService.saveFarm(farm);

        // Assert
        assertEquals("MyFarm", savedFarm.getName());
        verify(farmRepository, times(1)).save(farm);
    }
}
