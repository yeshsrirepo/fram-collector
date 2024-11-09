CREATE TABLE IF NOT EXISTS farm (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS crop (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    season VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS production (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    farm_id BIGINT,
    crop_id BIGINT,
    season VARCHAR(50),
    planting_area DOUBLE,
    expected_product DOUBLE,
    actual_harvested_product DOUBLE,
    FOREIGN KEY (farm_id) REFERENCES farm(id),
    FOREIGN KEY (crop_id) REFERENCES crop(id)
);

