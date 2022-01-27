package com.binariks.techtask.controller;

import com.binariks.techtask.User;
import com.binariks.techtask.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
@Slf4j
public class StartController {

    private final List<TaskService> list;

    @Autowired
    public StartController(List<TaskService> list) {
        this.list = list;
    }


    @GetMapping("/{version}")
    public Set<User> start(@PathVariable Integer version) {
        long startTime = System.nanoTime();
        Set<User> result = list.get(version).run();
        long endTime = System.nanoTime();
        log.info((endTime - startTime) / 1000000 + " milliseconds");
        return result;
    }

    @GetMapping("/all")
    public Map<String, Long> all() {
        Map<String, Long> map = new HashMap<>();
        for (TaskService impl : list) {
            long startTime = System.nanoTime();
            impl.run();
            long endTime = System.nanoTime();
            map.put(impl.getClass().getSimpleName(), (endTime - startTime) / 1000000);
        }
        return map;
    }

    @GetMapping("/all-avg")
    public Map<String, Long> getAllAvgTime() {
        Map<String, Long> map = new HashMap<>();
        long sumTime;
        for (TaskService impl : list) {
            sumTime = 0;
            for (int i = 0; i < 1000; i++) {
                long startTime = System.nanoTime();
                impl.run();
                long endTime = System.nanoTime();
                sumTime += (endTime - startTime) / 1000000;
            }
            map.put(impl.getClass().getSimpleName(), sumTime / 1000);
        }
        return map;
    }

}
