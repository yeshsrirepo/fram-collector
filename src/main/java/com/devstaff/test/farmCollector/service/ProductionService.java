package com.devstaff.test.farmCollector.service;

import com.devstaff.test.farmCollector.dao.CropRepository;
import com.devstaff.test.farmCollector.dao.FarmRepository;
import com.devstaff.test.farmCollector.dao.ProductionRepository;
import com.devstaff.test.farmCollector.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductionService {
    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private ProductionRepository productionRepository;

    public Production savePlantedData(PlantedRequestDTO plantedRequestDTO) {
        // Find or create the Farm by name
        Farm farm = farmRepository.findByName(plantedRequestDTO.getFarm())
                .orElseGet(() -> {
                    Farm newFarm = new Farm();
                    newFarm.setName(plantedRequestDTO.getFarm().toLowerCase());
                    return farmRepository.save(newFarm);
                });

        System.out.println("farm id and name is : "+farm.getId());

        // Find or create the Crop by name
        Crop crop = cropRepository.findByName(plantedRequestDTO.getCrop())
                .orElseGet(() -> {
                    Crop newCrop = new Crop();
                    newCrop.setName(plantedRequestDTO.getCrop().toLowerCase());
                    newCrop.setSeason(plantedRequestDTO.getSeason().toLowerCase());
                    return cropRepository.save(newCrop);
                });
        // Create a new Production entity
        Production production = new Production();
        production.setFarm(farm);
        production.setCrop(crop);
        production.setSeason(plantedRequestDTO.getSeason().toLowerCase());
        production.setPlantingArea(plantedRequestDTO.getPlantingArea());
        production.setExpectedProduct(plantedRequestDTO.getExpectedProduct());

        // Save the Production entity
        return productionRepository.save(production);
    }
    public Production updateHarvestedData(HarvestRequestDTO harvestRequest) {
        // Look up Farm by name
        Farm farm = farmRepository.findByName(harvestRequest.getFarm().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Farm does not exist: " + harvestRequest.getFarm()));

        // Look up Crop by name
        Crop crop = cropRepository.findByName(harvestRequest.getCrop().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Crop does not exist: " + harvestRequest.getCrop()));

        // Find the Production record by farm, crop, and season
        Production production = productionRepository.findByFarmAndCropAndSeason(farm, crop, harvestRequest.getSeason().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Production record not found for farm: " + harvestRequest.getFarm() +
                                ", crop: " + harvestRequest.getCrop() + ", season: " + harvestRequest.getSeason()));

        // Update the actual harvested product value
        production.setActualHarvestedProduct(harvestRequest.getActualHarvestedProduct());

        // Save and return the updated Production record
        return productionRepository.save(production);
    }

    public List<Production> getReports(String season) {
        return productionRepository.findBySeason(season);
    }
}

