package com.devstaff.test.farmCollector.dao;

import com.devstaff.test.farmCollector.model.Crop;
import com.devstaff.test.farmCollector.model.Farm;
import com.devstaff.test.farmCollector.model.Production;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductionRepository extends JpaRepository<Production, Long> {
    List<Production> findBySeason(String season);
    Optional<Production> findByFarmAndCropAndSeason(Farm farm, Crop crop, String season);
}


