package com.devstaff.test.farmCollector.dao;

import com.devstaff.test.farmCollector.model.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    Optional<Farm> findByName(String name);
}

