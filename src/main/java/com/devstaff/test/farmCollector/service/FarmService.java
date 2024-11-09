package com.devstaff.test.farmCollector.service;

import com.devstaff.test.farmCollector.dao.FarmRepository;
import com.devstaff.test.farmCollector.model.Farm;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class FarmService {
    @Autowired
    private FarmRepository farmRepository;

    public Farm saveFarm(Farm farm) {
        return farmRepository.save(farm);
    }
}

