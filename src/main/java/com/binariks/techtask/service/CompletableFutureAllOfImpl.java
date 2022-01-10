package com.binariks.techtask.service;

import com.binariks.techtask.User;
import com.binariks.techtask.repository.MongoDBRepo;
import com.binariks.techtask.repository.MySQLRepo;
import com.binariks.techtask.util.FileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CompletableFutureAllOfImpl extends AbstractTask {
    @Autowired
    public CompletableFutureAllOfImpl(MongoDBRepo mongoDBRepo, MySQLRepo mySQLRepo, FileReader fileReader) {
        super(mongoDBRepo, mySQLRepo, fileReader);
    }

    @Override
    public Set<User> run() {
        super.run();
        CompletableFuture<Void> future = CompletableFuture
                .allOf(CompletableFuture.runAsync(this::processData), CompletableFuture.runAsync(this::processData));
        future.thenRunAsync(this::writeToMongoDB);
        future.thenRunAsync(this::writeToMySQL);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return getUsersFromMap();
    }
}
