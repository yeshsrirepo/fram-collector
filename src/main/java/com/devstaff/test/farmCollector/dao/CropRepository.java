package com.devstaff.test.farmCollector.dao;

import com.devstaff.test.farmCollector.model.Crop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Long> {
    Optional<Crop> findByName(String name);
}


