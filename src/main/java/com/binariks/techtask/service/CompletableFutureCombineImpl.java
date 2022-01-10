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
public class CompletableFutureCombineImpl extends AbstractTask {
    @Autowired
    public CompletableFutureCombineImpl(MongoDBRepo mongoDBRepo, MySQLRepo mySQLRepo, FileReader fileReader) {
        super(mongoDBRepo, mySQLRepo, fileReader);
    }

    @Override
    public Set<User> run() {
        super.run();
        CompletableFuture<?> firstProcess = CompletableFuture.runAsync(this::processData);
        CompletableFuture<?> secondProcess = CompletableFuture.runAsync(this::processData);
        firstProcess.thenCombineAsync(secondProcess, (o, o2) -> {
            writeToMongoDB();
            return null;
        });
        firstProcess.thenCombineAsync(secondProcess, (o, o2) -> {
            writeToMySQL();
            return null;
        });
        try {
            secondProcess.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return getUsersFromMap();
    }
}
