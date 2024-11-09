package com.devstaff.test.farmCollector.model;

import lombok.Data;

@Data
public class PlantedRequestDTO {

    private String farm;
    private String crop;
    private String season;
    private double plantingArea;
    private double expectedProduct;

}
