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

    private final LatchImpl latch;
    private final AwaitImpl await;
    private final IsTerminatedImpl isTerminated;
    private final InvokeAllImpl invokeAll;
    private final ExecutorCompletionImpl executorCompletion;
    private final CompletableFutureJoinImpl completableFuture;
    private final CompletableFutureCombineImpl completableFutureCombine;
    private final CompletableFutureAllOfImpl completableFutureAllOf;

    @Autowired
    public StartController(LatchImpl latch, AwaitImpl await, IsTerminatedImpl isTerminated, InvokeAllImpl invokeAll, ExecutorCompletionImpl executorCompletion, CompletableFutureJoinImpl completableFuture, CompletableFutureCombineImpl completableFutureCombine, CompletableFutureAllOfImpl completableFutureAllOf) {
        this.latch = latch;
        this.await = await;
        this.isTerminated = isTerminated;
        this.invokeAll = invokeAll;
        this.executorCompletion = executorCompletion;
        this.completableFuture = completableFuture;
        this.completableFutureCombine = completableFutureCombine;
        this.completableFutureAllOf = completableFutureAllOf;
    }


    @GetMapping("/{version}")
    public Set<User> start(@PathVariable Integer version) {
        List<TaskService> list = getListOfImpl();
        long startTime = System.nanoTime();
        Set<User> result = list.get(version).run();
        long endTime = System.nanoTime();
        log.info((endTime - startTime) / 1000000 + " milliseconds");
        return result;
    }

    @GetMapping("/all")
    public Map<String, Long> all() {
        List<TaskService> list = getListOfImpl();
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
        List<TaskService> list = getListOfImpl();
        Map<String, Long> map = new HashMap<>();
        long sumTime;
        for (TaskService impl : list) {
            sumTime = 0;
            for (int i = 0; i < 100; i++) {
                long startTime = System.nanoTime();
                impl.run();
                long endTime = System.nanoTime();
                sumTime += (endTime - startTime) / 1000000;
            }
            map.put(impl.getClass().getSimpleName(), sumTime / 100);
        }
        return map;
    }

    private List<TaskService> getListOfImpl() {
        return List.of(
                latch,
                await,
                isTerminated,
                invokeAll,
                executorCompletion,
                completableFuture,
                completableFutureCombine,
                completableFutureAllOf);
    }

}
