package com.devstaff.test.farmCollector.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "crop")
public class Crop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String season;
}

