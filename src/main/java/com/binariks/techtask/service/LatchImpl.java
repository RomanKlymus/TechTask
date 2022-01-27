package com.binariks.techtask.service;

import com.binariks.techtask.User;
import com.binariks.techtask.repository.MongoDBRepo;
import com.binariks.techtask.repository.MySQLRepo;
import com.binariks.techtask.util.FileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LatchImpl extends AbstractTask {
    private final CountDownLatch processLatch = new CountDownLatch(THREADS_NUMBER);
    private final CountDownLatch writeLatch = new CountDownLatch(THREADS_NUMBER);

    @Autowired
    public LatchImpl(MongoDBRepo mongoDBRepo, MySQLRepo mySQLRepo, FileReader fileReader) {
        super(mongoDBRepo, mySQLRepo, fileReader);
    }

    @Override
    public Set<User> run() {
        super.run();
        ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);
        ExecutorService writers = Executors.newFixedThreadPool(THREADS_NUMBER);
        executorService.execute(this::processData);
        executorService.execute(this::processData);
        try {
            processLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
        writers.execute(this::writeToMongoDB);
        writers.execute(this::writeToMySQL);
        try {
            writeLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        writers.shutdown();

        return getUsersFromMap();
    }

    @Override
    public void processData() {
        super.processData();
        processLatch.countDown();
    }

    @Override
    public void writeToMongoDB() {
        super.writeToMongoDB();
        writeLatch.countDown();
    }

    @Override
    public void writeToMySQL() {
        super.writeToMySQL();
        writeLatch.countDown();
    }
}
