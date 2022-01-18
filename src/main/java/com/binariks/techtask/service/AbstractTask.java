package com.binariks.techtask.service;

import com.binariks.techtask.User;
import com.binariks.techtask.repository.MongoDBRepo;
import com.binariks.techtask.repository.MySQLRepo;
import com.binariks.techtask.util.FileReader;

import java.util.*;
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
        Set<User> users;
        lock.lock();
        try {
            Set<User> set = new HashSet<>();
            for (Map.Entry<String, Integer> entry : this.users.entrySet()) {
                User user = new User(entry.getKey(), entry.getValue());
                set.add(user);
            }
            users = set;
        } finally {
            lock.unlock();
        }
        return users;
    }

    public void processData() {
        String str;
        String[] strings;
        String name;
        Integer value;
        while (!queue.isEmpty()) {
            str = queue.poll();
            if (str != null) {
                strings = str.split(",");
                try {
                    name = strings[1];
                    value = Integer.valueOf(strings[2]);
                } catch (IndexOutOfBoundsException | NumberFormatException e) {
                    continue;
                }
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
