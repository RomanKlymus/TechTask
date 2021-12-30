package com.binariks.techtask.service;

import com.binariks.techtask.User;
import com.binariks.techtask.repository.MongoDBRepo;
import com.binariks.techtask.repository.MySQLRepo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskServiceTest {

    @ParameterizedTest
    @ArgumentsSource(ImplArgsProvider.class)
    public void whenThereAreTransactionsShouldReturnSetOfUsers(TaskService taskService) {
        final Set<User> users = Set.of(
                new User("dima", -140),
                new User("sigal", 160),
                new User("lev", 100),
                new User("shani", 400),
                new User("itay", 180),
                new User("shaul", 100),
                new User("lior", 60)
        );
        assertThat(taskService.run()).containsAll(users);
    }

}

class ImplArgsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        MongoDBRepo mongoDBRepo = Mockito.mock(MongoDBRepo.class);
        MySQLRepo mySQLRepo = Mockito.mock(MySQLRepo.class);
        List<Class<?>> impls = List.of(
                LatchImpl.class,
                AwaitImpl.class,
                IsTerminatedImpl.class,
                InvokeAllImpl.class,
                ExecutorCompletionImpl.class,
                CompletableFutureImpl.class,
                CompletableFutureCombineImpl.class,
                CompletableFutureAllOfImpl.class);
        return impls.stream().map(impl -> {
            try {
                return impl.getConstructor(MongoDBRepo.class, MySQLRepo.class).newInstance(mongoDBRepo, mySQLRepo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).map(Arguments::of);
    }

}
