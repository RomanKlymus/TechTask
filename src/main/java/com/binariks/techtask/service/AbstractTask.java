package com.binariks.techtask.service;

import com.binariks.techtask.User;
import com.binariks.techtask.repository.MongoDBRepo;
import com.binariks.techtask.repository.MySQLRepo;
import com.binariks.techtask.util.FileReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public abstract class AbstractTask implements TaskService {
    public static final String FILE_NAME = "input.txt";
    public static final int THREADS_NUMBER = 2;
    private final MongoDBRepo mongoDBRepo;
    private final MySQLRepo mySQLRepo;
    private final Map<String, Integer> users = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final FileReader fileReader;
    private Queue<String> queue;

    public AbstractTask(MongoDBRepo mongoDBRepo, MySQLRepo mySQLRepo, FileReader fileReader) {
        this.mongoDBRepo = mongoDBRepo;
        this.mySQLRepo = mySQLRepo;
        this.fileReader = fileReader;
    }

    @Override
    public Set<User> run() {
        queue = fileReader.readQueueFromFile(FILE_NAME);
        users.clear();
        return null;
    }

    public void writeToMongoDB() {
        mongoDBRepo.saveAll(getUsersFromMap());
    }

    public void writeToMySQL() {
        mySQLRepo.saveAll(getUsersFromMap());
    }

    public Set<User> getUsersFromMap() {
        return users.entrySet().stream().map(entry -> new User(entry.getKey(), entry.getValue())).collect(Collectors.toSet());
    }

    public void processData() {
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
}
