package com.devstaff.test.farmCollector.controller;

import com.devstaff.test.farmCollector.model.HarvestRequestDTO;
import com.devstaff.test.farmCollector.model.PlantedRequestDTO;
import com.devstaff.test.farmCollector.model.Production;
import com.devstaff.test.farmCollector.service.ProductionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Record planted data",
            description = "Records data about planted crops."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Planted data recorded successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Production.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content)
    })
    @PostMapping("/planted")
    public Production recordPlantedData(@RequestBody PlantedRequestDTO plantedRequestDTO) {
        return productionService.savePlantedData(plantedRequestDTO);
    }

    @Operation(
            summary = "Record harvested data",
            description = "Records data about harvested crops and updates production information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Harvested data recorded successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Production.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid harvested data",
                    content = @Content)
    })
    @PostMapping("/harvested")
    public ResponseEntity<?> recordHarvestedData(@RequestBody HarvestRequestDTO harvestRequest) {
        try {
            Production updatedProduction = productionService.updateHarvestedData(harvestRequest);
            return ResponseEntity.ok(updatedProduction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
            summary = "Get production report",
            description = "Retrieves a production report for a specified season, optionally grouped by farm or crop."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report retrieved successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid season or grouping parameter",
                    content = @Content)
    })
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

