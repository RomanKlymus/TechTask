package com.binariks.techtask.controller;

import com.binariks.techtask.User;
import com.binariks.techtask.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin
public class StartController {

    private final LatchImpl latch;
    private final AwaitImpl await;
    private final IsTerminatedImpl isTerminated;
    private final InvokeAllImpl invokeAll;
    private final ExecutorCompletionImpl executorCompletion;
    private final CompletableFutureImpl completableFuture;
    private final CompletableFutureCombineImpl completableFutureCombine;
    private final CompletableFutureAllOfImpl completableFutureAllOf;

    @Autowired
    public StartController(LatchImpl latch, AwaitImpl await, IsTerminatedImpl isTerminated, InvokeAllImpl invokeAll, ExecutorCompletionImpl executorCompletion, CompletableFutureImpl completableFuture, CompletableFutureCombineImpl completableFutureCombine, CompletableFutureAllOfImpl completableFutureAllOf) {
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
        List<TaskService> list = List.of(
                latch,
                await,
                isTerminated,
                invokeAll,
                executorCompletion,
                completableFuture,
                completableFutureCombine,
                completableFutureAllOf);
        return list.get(version).run();
    }

}
