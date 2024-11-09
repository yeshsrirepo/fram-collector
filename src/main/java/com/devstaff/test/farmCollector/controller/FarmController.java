package com.devstaff.test.farmCollector.controller;

import com.devstaff.test.farmCollector.model.Farm;
import com.devstaff.test.farmCollector.service.FarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farms")
public class FarmController {

    @Autowired
    private FarmService farmService;

    @GetMapping("/ping")
    public String pingAPI() {
        return "farm up";
    }

    @PostMapping
    public Farm createFarm(@RequestBody Farm farm) {
        return farmService.saveFarm(farm);
    }
}

