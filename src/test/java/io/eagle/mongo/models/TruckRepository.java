package io.eagle.mongo.models;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TruckRepository extends MongoRepository<Truck, ObjectId> {
}
