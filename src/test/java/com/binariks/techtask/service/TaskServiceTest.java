package com.binariks.techtask.service;

import com.binariks.techtask.User;
import com.binariks.techtask.repository.MongoDBRepo;
import com.binariks.techtask.repository.MySQLRepo;
import com.binariks.techtask.util.FileReader;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskServiceTest {
    @ParameterizedTest
    @ArgumentsSource(ImplArgsProvider.class)
    public void whenThereAreCorrectTransactionsShouldReturnSetOfUsers(TaskService taskService) {
        final Set<User> users = Set.of(
                new User("lev", 50),
                new User("shani", 200),
                new User("lior", 30)
        );
        Queue<String> strings = new ConcurrentLinkedQueue<>(
                List.of(
                        "1,lev,100",
                        "2,shani,200",
                        "3,lior,-10",
                        "3,lev,-50",
                        "9,lior,40"));
        when(ImplArgsProvider.fileReader.readQueueFromFile(AbstractTask.FILE_NAME))
                .thenReturn(strings);

        assertThat(taskService.run()).containsAll(users);
    }
}

class ImplArgsProvider implements ArgumentsProvider {
    public static final FileReader fileReader = mock(FileReader.class);
    private final MongoDBRepo mongoDBRepo = mock(MongoDBRepo.class);
    private final MySQLRepo mySQLRepo = mock(MySQLRepo.class);

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        List<Class<?>> impls = List.of(
                LatchImpl.class,
                AwaitImpl.class,
                IsTerminatedImpl.class,
                InvokeAllImpl.class,
                ExecutorCompletionImpl.class,
                CompletableFutureImpl.class,
                CompletableFutureCombineImpl.class,
                CompletableFutureAllOfImpl.class
        );
        return impls.stream().map(impl -> {
            try {
                return impl.getConstructor(MongoDBRepo.class, MySQLRepo.class, FileReader.class).newInstance(mongoDBRepo, mySQLRepo, fileReader);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).map(Arguments::of);
    }
}
