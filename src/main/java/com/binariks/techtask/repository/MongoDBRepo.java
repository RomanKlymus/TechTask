package com.binariks.techtask.repository;

import com.binariks.techtask.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface MongoDBRepo extends MongoRepository<User, String> {

}
