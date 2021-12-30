package com.binariks.techtask.service;

import com.binariks.techtask.User;
import com.binariks.techtask.repository.MongoDBRepo;
import com.binariks.techtask.repository.MySQLRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CompletableFutureImpl implements TaskService {

    private static final String FILE_NAME = "input.txt";
    private static final int THREADS_NUMBER = 2;
    private final Queue<String> queue = new ConcurrentLinkedQueue<>();
    private final Map<String, Integer> users = new HashMap<>();
    private final MongoDBRepo mongoDBRepo;
    private final MySQLRepo mySQLRepo;
    private final ReentrantLock lock = new ReentrantLock();

    @Autowired
    public CompletableFutureImpl(MongoDBRepo mongoDBRepo, MySQLRepo mySQLRepo) {
        this.mongoDBRepo = mongoDBRepo;
        this.mySQLRepo = mySQLRepo;
    }


    @Override
    public Set<User> run() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);
        readFile();
        List<Runnable> tasks = List.of(this::processData, this::processData);
        CompletableFuture<?>[] futures = tasks.stream().map(task -> CompletableFuture.runAsync(task, executorService)).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        executorService.execute(this::writeToMongoDB);
        executorService.execute(this::writeToMySQL);
        executorService.shutdown();
        return getUsersFromMap();
    }

    private void writeToMongoDB() {
        mongoDBRepo.saveAll(getUsersFromMap());
    }

    private void writeToMySQL() {
        mySQLRepo.saveAll(getUsersFromMap());
    }

    private Set<User> getUsersFromMap() {
        return users.entrySet().stream().map(entry -> new User(entry.getKey(), entry.getValue())).collect(Collectors.toSet());
    }

    private void processData() {
        String str;
        String[] strings;
        while (!queue.isEmpty()) {
            str = queue.poll();
            if (str != null) {
                strings = str.split(",");
                String name = strings[1];
                Integer value = Integer.valueOf(strings[2]);
                lock.lock();
                try {
                    users.put(name, users.containsKey(name) ? users.get(name) + value : value);
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    private void readFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(FILE_NAME);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found");
        }
        try (Scanner scanner = new Scanner(inputStream)) {
            while (scanner.hasNextLine()) {
                queue.add(scanner.nextLine());
            }
        }
    }

}
