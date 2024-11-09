package com.devstaff.test.farmCollector.controller;

import com.devstaff.test.farmCollector.model.HarvestRequestDTO;
import com.devstaff.test.farmCollector.model.PlantedRequestDTO;
import com.devstaff.test.farmCollector.model.Production;
import com.devstaff.test.farmCollector.service.ProductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/production")
public class ProductionController {

    @Autowired
    private ProductionService productionService;

    @PostMapping("/planted")
    public Production recordPlantedData(@RequestBody PlantedRequestDTO plantedRequestDTO) {
        return productionService.savePlantedData(plantedRequestDTO);
    }

    @PostMapping("/harvested")
    public ResponseEntity<?> recordHarvestedData(@RequestBody HarvestRequestDTO harvestRequest) {
        try {
            Production updatedProduction = productionService.updateHarvestedData(harvestRequest);
            return ResponseEntity.ok(updatedProduction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/reports")
    public Map<String, List<Production>> getReport(@RequestParam String season, @RequestParam(required = false) String groupBy) {
        List<Production> productions = productionService.getReports(season.toLowerCase());

        if ("farm".equalsIgnoreCase(groupBy)) {
            return productions.stream().collect(Collectors.groupingBy(p -> p.getFarm().getName()));
        } else if ("crop".equalsIgnoreCase(groupBy)) {
            return productions.stream().collect(Collectors.groupingBy(p -> p.getCrop().getName()));
        } else {
            return Map.of("all", productions);
        }
    }
}

