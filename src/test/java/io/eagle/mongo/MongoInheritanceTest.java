package io.eagle.mongo;

import io.eagle.mongo.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
@Import(MongoInheritanceTest.TestConfig.class)
public class MongoInheritanceTest {

    @SpringBootConfiguration
    @EnableMongoInheritanceRepositories(basePackages={"io.eagle.mongo.models"})
    static class TestConfig {}

    @Autowired
    CarRepository carRepository;

    @Autowired
    TruckRepository truckRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Test
    void findByRepoType() {
        this.carRepository.save(new Car());
        this.truckRepository.save(new Truck());

        assertEquals(1, this.carRepository.findAll().size());
        assertEquals(1, this.truckRepository.findAll().size());
        assertEquals(2, this.vehicleRepository.findAll().size());
    }
}
