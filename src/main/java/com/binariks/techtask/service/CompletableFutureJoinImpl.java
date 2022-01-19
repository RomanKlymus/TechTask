package com.binariks.techtask.service;

import com.binariks.techtask.User;
import com.binariks.techtask.repository.MongoDBRepo;
import com.binariks.techtask.repository.MySQLRepo;
import com.binariks.techtask.util.FileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class CompletableFutureJoinImpl extends AbstractTask {
    @Autowired
    public CompletableFutureJoinImpl(MongoDBRepo mongoDBRepo, MySQLRepo mySQLRepo, FileReader fileReader) {
        super(mongoDBRepo, mySQLRepo, fileReader);
    }

    @Override
    public Set<User> run() {
        super.run();
        List<Runnable> tasks = List.of(this::processData, this::processData);
        List<Runnable> tasks2 = List.of(this::writeToMongoDB, this::writeToMySQL);
        CompletableFuture<?>[] futures = tasks.stream().map(CompletableFuture::runAsync).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        futures = tasks2.stream().map(CompletableFuture::runAsync).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();

        return getUsersFromMap();
    }
}
