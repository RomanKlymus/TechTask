package com.binariks.techtask.service;

import com.binariks.techtask.User;
import com.binariks.techtask.repository.MongoDBRepo;
import com.binariks.techtask.repository.MySQLRepo;
import com.binariks.techtask.util.FileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ExecutorCompletionImpl extends AbstractTask {
    private static final String FILE_NAME = "input.txt";
    private static final int THREADS_NUMBER = 2;
    private final Queue<String> queue = new ConcurrentLinkedQueue<>();
    private final Map<String, Integer> users = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    @Autowired
    public ExecutorCompletionImpl(MongoDBRepo mongoDBRepo, MySQLRepo mySQLRepo, FileReader fileReader) {
        super(mongoDBRepo, mySQLRepo, fileReader);
    }

    @Override
    public Set<User> run() {
        super.run();
        ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);
        CompletionService<Void> completionService = new ExecutorCompletionService<>(executorService);
        completionService.submit(this::process);
        completionService.submit(this::process);
        try {
            completionService.take();
            completionService.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.execute(this::writeToMongoDB);
        executorService.execute(this::writeToMySQL);
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        return getUsersFromMap();
    }

    private Void process() {
        this.processData();
        return null;
    }
}
