package io.eagle.mongo;

import io.eagle.mongo.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
@Import(MongoInheritanceTest.TestConfig.class)
public class MongoInheritanceTest {
    @SpringBootConfiguration
    @EnableMongoInheritanceRepositories(basePackages={"io.eagle.mongo.models"})
    static class TestConfig {
    }

    @Autowired
    CarRepository carRepository;

    @Autowired
    TruckRepository truckRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeEach
    void before() {
        this.mongoTemplate.getDb().drop();
    }

    @Test
    void findByRepoType() {
        Car car = this.carRepository.save(new Car());
        Truck truck = this.truckRepository.save(new Truck());

        assertEquals(Arrays.asList(car), this.carRepository.findAll());
        assertEquals(Arrays.asList(truck), this.truckRepository.findAll());
        assertEquals(Arrays.asList(car, truck), this.vehicleRepository.findAll());
    }
}
