package com.devstaff.test.farmCollector.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
public class Production {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Farm farm;

    @ManyToOne
    private Crop crop;

    private String season;
    private double plantingArea;
    private double expectedProduct;
    private double actualHarvestedProduct;
}

