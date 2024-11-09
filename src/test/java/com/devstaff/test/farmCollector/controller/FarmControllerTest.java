package com.devstaff.test.farmCollector.controller;

import com.devstaff.test.farmCollector.dao.FarmRepository;
import com.devstaff.test.farmCollector.model.Farm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FarmControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FarmRepository farmRepository;

    @AfterEach
    void tearDown() {
        // Clear the repository after each test to maintain test isolation
        farmRepository.deleteAll();
    }

    @Test
    void testPingAPI() throws Exception {
        mockMvc.perform(get("/api/farms/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("farm up"));
    }

    @Test
    void testCreateFarm() throws Exception {
        // Arrange
        Farm farm = new Farm();
        farm.setName("TestFarm");

        // Act & Assert
        mockMvc.perform(post("/api/farms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(farm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestFarm"));

        // Verify that the farm was saved in the database
        Farm savedFarm = farmRepository.findAll().get(0);
        assert(savedFarm.getName()).equals("TestFarm");
    }
}


