package com.binariks.techtask.service;

import com.binariks.techtask.User;
import com.binariks.techtask.repository.MongoDBRepo;
import com.binariks.techtask.repository.MySQLRepo;
import com.binariks.techtask.util.FileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AwaitImpl extends AbstractTask {


    private static final int THREADS_NUMBER = 2;

    @Autowired
    public AwaitImpl(MongoDBRepo mongoDBRepo, MySQLRepo mySQLRepo, FileReader fileReader) {
        super(mongoDBRepo, mySQLRepo, fileReader);
    }

    @Override
    public Set<User> run() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);
        ExecutorService writers = Executors.newFixedThreadPool(THREADS_NUMBER);
        executorService.execute(this::processData);
        executorService.execute(this::processData);
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        writers.execute(this::writeToMongoDB);
        writers.execute(this::writeToMySQL);
        writers.shutdown();
        return super.getUsersFromMap();
    }

}
