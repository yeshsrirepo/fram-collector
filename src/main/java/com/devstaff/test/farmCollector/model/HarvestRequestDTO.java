package com.devstaff.test.farmCollector.model;

import lombok.Data;

@Data
public class HarvestRequestDTO {

    private String farm;
    private String crop;
    private String season;
    private double actualHarvestedProduct;

}
