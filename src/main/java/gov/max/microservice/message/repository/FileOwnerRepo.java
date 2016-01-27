package gov.max.microservice.message.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import gov.max.microservice.message.user.FileOwner;

public interface FileOwnerRepo extends MongoRepository<FileOwner, String> {
    FileOwner findById(int id);
}
