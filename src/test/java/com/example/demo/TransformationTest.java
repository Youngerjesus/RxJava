package com.example.demo;

import io.reactivex.internal.schedulers.ExecutorScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransformationTest {

    @Test
    void test1() {
        Mono<String> user = Mono.just(new User("test1"))
                .map(u -> u.getName());

        StepVerifier.create(user)
                .expectNext("test1")
                .verifyComplete();
    }

    @Test
    void test2() throws InterruptedException {
        Flux.just(new User("test1"), new User("test2"), new User("test3"))
                .map(u -> u.getName())
                .subscribe(System.out::println);

        Flux.just(new User("test1"), new User("test2"), new User("test3"), new User("test4"))
                .flatMap(u -> Flux.just(u.getName()))
                .delayElements(Duration.ofMillis(100))
                .subscribe(System.out::println);

        Thread.sleep(2000);
    }

    public static class User {
        private String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
