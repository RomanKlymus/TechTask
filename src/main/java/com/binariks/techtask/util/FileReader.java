package com.binariks.techtask.util;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class FileReader {
    public Queue<String> readQueueFromFile(String fileName) {
        Queue<String> queue = new ConcurrentLinkedQueue<>();
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found");
        }
        try (Scanner scanner = new Scanner(inputStream)) {
            while (scanner.hasNextLine()) {
                queue.add(scanner.nextLine());
            }
        }
        return queue;
    }
}
