package com.devstaff.test.farmCollector.service;

import com.devstaff.test.farmCollector.dao.CropRepository;
import com.devstaff.test.farmCollector.dao.FarmRepository;
import com.devstaff.test.farmCollector.dao.ProductionRepository;
import com.devstaff.test.farmCollector.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductionServiceTest {

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private CropRepository cropRepository;

    @Mock
    private ProductionRepository productionRepository;

    @InjectMocks
    private ProductionService productionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSavePlantedDataCreatesNewFarmAndCropIfNotExists() {
        // Arrange
        PlantedRequestDTO requestDTO = new PlantedRequestDTO();
        requestDTO.setFarm("NewFarm");
        requestDTO.setCrop("NewCrop");
        requestDTO.setSeason("Spring");
        requestDTO.setPlantingArea(50.5);
        requestDTO.setExpectedProduct(100);

        when(farmRepository.findByName("newfarm")).thenReturn(Optional.empty());
        when(cropRepository.findByName("newcrop")).thenReturn(Optional.empty());
        when(farmRepository.save(any(Farm.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cropRepository.save(any(Crop.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productionRepository.save(any(Production.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Production result = productionService.savePlantedData(requestDTO);

        // Assert
        verify(farmRepository, times(1)).save(any(Farm.class));
        verify(cropRepository, times(1)).save(any(Crop.class));
        verify(productionRepository, times(1)).save(any(Production.class));

        assertEquals("spring", result.getSeason());
        assertEquals(50.5, result.getPlantingArea());
        assertEquals(100, result.getExpectedProduct());
    }

    @Test
    void testUpdateHarvestedDataUpdatesProductIfProductionExists() {
        // Arrange
        HarvestRequestDTO requestDTO = new HarvestRequestDTO();
        requestDTO.setFarm("ExistingFarm");
        requestDTO.setCrop("ExistingCrop");
        requestDTO.setSeason("Spring");
        requestDTO.setActualHarvestedProduct(99);

        Farm farm = new Farm();
        farm.setId(1L);
        farm.setName("existingfarm");

        Crop crop = new Crop();
        crop.setId(1L);
        crop.setName("existingcrop");
        crop.setSeason("spring");

        Production production = new Production();
        production.setId(1L);
        production.setFarm(farm);
        production.setCrop(crop);
        production.setSeason("spring");
        production.setExpectedProduct(100);
        production.setActualHarvestedProduct(0);

        when(farmRepository.findByName("existingfarm")).thenReturn(Optional.of(farm));
        when(cropRepository.findByName("existingcrop")).thenReturn(Optional.of(crop));
        when(productionRepository.findByFarmAndCropAndSeason(farm, crop, "spring")).thenReturn(Optional.of(production));
        when(productionRepository.save(any(Production.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Production result = productionService.updateHarvestedData(requestDTO);

        // Assert
        verify(productionRepository, times(1)).save(production);
        assertEquals(99, result.getActualHarvestedProduct());
    }

    @Test
    void testUpdateHarvestedDataThrowsExceptionIfFarmDoesNotExist() {
        // Arrange
        HarvestRequestDTO requestDTO = new HarvestRequestDTO();
        requestDTO.setFarm("NonExistentFarm");
        requestDTO.setCrop("ExistingCrop");
        requestDTO.setSeason("Spring");
        requestDTO.setActualHarvestedProduct(99);

        when(farmRepository.findByName("nonexistentfarm")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                productionService.updateHarvestedData(requestDTO));
        assertEquals("Farm does not exist: NonExistentFarm", exception.getMessage());
    }

    @Test
    void testUpdateHarvestedDataThrowsExceptionIfCropDoesNotExist() {
        // Arrange
        HarvestRequestDTO requestDTO = new HarvestRequestDTO();
        requestDTO.setFarm("ExistingFarm");
        requestDTO.setCrop("NonExistentCrop");
        requestDTO.setSeason("Spring");
        requestDTO.setActualHarvestedProduct(99);

        Farm farm = new Farm();
        farm.setId(1L);
        farm.setName("existingfarm");

        when(farmRepository.findByName("existingfarm")).thenReturn(Optional.of(farm));
        when(cropRepository.findByName("nonexistentcrop")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                productionService.updateHarvestedData(requestDTO));
        assertEquals("Crop does not exist: NonExistentCrop", exception.getMessage());
    }

    @Test
    void testUpdateHarvestedDataThrowsExceptionIfProductionRecordDoesNotExist() {
        // Arrange
        HarvestRequestDTO requestDTO = new HarvestRequestDTO();
        requestDTO.setFarm("ExistingFarm");
        requestDTO.setCrop("ExistingCrop");
        requestDTO.setSeason("Spring");
        requestDTO.setActualHarvestedProduct(99);

        Farm farm = new Farm();
        farm.setId(1L);
        farm.setName("existingfarm");

        Crop crop = new Crop();
        crop.setId(1L);
        crop.setName("existingcrop");
        crop.setSeason("spring");

        when(farmRepository.findByName("existingfarm")).thenReturn(Optional.of(farm));
        when(cropRepository.findByName("existingcrop")).thenReturn(Optional.of(crop));
        when(productionRepository.findByFarmAndCropAndSeason(farm, crop, "spring")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                productionService.updateHarvestedData(requestDTO));
        assertEquals("Production record not found for farm: ExistingFarm, crop: ExistingCrop, season: Spring",
                exception.getMessage());
    }

    @Test
    void testGetReportsReturnsProductionRecordsForSeason() {
        // Arrange
        String season = "spring";
        Production production1 = new Production();
        production1.setId(1L);
        production1.setSeason("spring");

        Production production2 = new Production();
        production2.setId(2L);
        production2.setSeason("spring");

        when(productionRepository.findBySeason(season)).thenReturn(List.of(production1, production2));

        // Act
        List<Production> result = productionService.getReports(season);

        // Assert
        assertEquals(2, result.size());
        verify(productionRepository, times(1)).findBySeason(season);
    }
}
