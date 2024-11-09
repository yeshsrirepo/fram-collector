package com.devstaff.test.farmCollector.controller;

import com.devstaff.test.farmCollector.dao.CropRepository;
import com.devstaff.test.farmCollector.dao.FarmRepository;
import com.devstaff.test.farmCollector.dao.ProductionRepository;
import com.devstaff.test.farmCollector.model.HarvestRequestDTO;
import com.devstaff.test.farmCollector.model.PlantedRequestDTO;
import com.devstaff.test.farmCollector.model.Production;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private ProductionRepository productionRepository;

    @BeforeEach
    void setUp() {
        // Any setup needed before each test case runs
    }

    @AfterEach
    void tearDown() {
        // Clear all data from repositories after each test
        productionRepository.deleteAll();
        cropRepository.deleteAll();
        farmRepository.deleteAll();
    }

    @Test
    void testRecordPlantedDataAndFetchReports() throws Exception {
        // Arrange: Create a new production record using the planted endpoint
        PlantedRequestDTO plantedRequest = new PlantedRequestDTO();
        plantedRequest.setFarm("MyFarm");
        plantedRequest.setCrop("Potato");
        plantedRequest.setSeason("Spring");
        plantedRequest.setPlantingArea(50.5);
        plantedRequest.setExpectedProduct(100);

        // Act: Record planted data
        MvcResult result = mockMvc.perform(post("/api/production/planted")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.season").value("spring"))
                .andExpect(jsonPath("$.plantingArea").value(50.5))
                .andExpect(jsonPath("$.expectedProduct").value(100))
                .andReturn();

        // Extract production ID for verification
        Production savedProduction = objectMapper.readValue(result.getResponse().getContentAsString(), Production.class);
        Long productionId = savedProduction.getId();

        // Act & Assert: Fetch the report to verify if the production record exists
        mockMvc.perform(get("/api/production/reports")
                        .param("season", "spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.all", hasSize(1)))
                .andExpect(jsonPath("$.all[0].id").value(productionId))
                .andExpect(jsonPath("$.all[0].farm.name").value("myfarm"))
                .andExpect(jsonPath("$.all[0].crop.name").value("potato"));
    }

    @Test
    void testRecordHarvestedDataSuccess() throws Exception {
        // Arrange: First create a planted record
        PlantedRequestDTO plantedRequest = new PlantedRequestDTO();
        plantedRequest.setFarm("HarvestFarm");
        plantedRequest.setCrop("Corn");
        plantedRequest.setSeason("Spring");
        plantedRequest.setPlantingArea(60.0);
        plantedRequest.setExpectedProduct(120);

        mockMvc.perform(post("/api/production/planted")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(plantedRequest)))
                .andExpect(status().isOk());

        // Act: Update with harvested data
        HarvestRequestDTO harvestRequest = new HarvestRequestDTO();
        harvestRequest.setFarm("HarvestFarm");
        harvestRequest.setCrop("Corn");
        harvestRequest.setSeason("Spring");
        harvestRequest.setActualHarvestedProduct(110);

        mockMvc.perform(post("/api/production/harvested")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(harvestRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actualHarvestedProduct").value(110))
                .andExpect(jsonPath("$.farm.name").value("harvestfarm"))
                .andExpect(jsonPath("$.crop.name").value("corn"));
    }

    @Test
    void testRecordHarvestedDataFailure() throws Exception {
        // Arrange: Prepare a harvest request for a non-existent production
        HarvestRequestDTO harvestRequest = new HarvestRequestDTO();
        harvestRequest.setFarm("NonExistentFarm");
        harvestRequest.setCrop("NonExistentCrop");
        harvestRequest.setSeason("Spring");
        harvestRequest.setActualHarvestedProduct(99);

        // Act & Assert: Attempt to update harvested data and expect a failure response
        mockMvc.perform(post("/api/production/harvested")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(harvestRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Farm does not exist: NonExistentFarm")));
    }

    @Test
    void testGetReportGroupedByFarmAfterPlantedData() throws Exception {
        // Arrange: Create multiple records for different farms
        PlantedRequestDTO request1 = new PlantedRequestDTO();
        request1.setFarm("FarmA");
        request1.setCrop("Wheat");
        request1.setSeason("Spring");
        request1.setPlantingArea(70.0);
        request1.setExpectedProduct(200);

        PlantedRequestDTO request2 = new PlantedRequestDTO();
        request2.setFarm("FarmB");
        request2.setCrop("Rice");
        request2.setSeason("Spring");
        request2.setPlantingArea(40.0);
        request2.setExpectedProduct(150);

        mockMvc.perform(post("/api/production/planted")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/production/planted")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        // Act & Assert: Fetch grouped report by farm
        mockMvc.perform(get("/api/production/reports")
                        .param("season", "spring")
                        .param("groupBy", "farm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.farma", hasSize(1)))
                .andExpect(jsonPath("$.farmb", hasSize(1)))
                .andExpect(jsonPath("$.farma[0].crop.name").value("wheat"))
                .andExpect(jsonPath("$.farmb[0].crop.name").value("rice"));
    }
}
