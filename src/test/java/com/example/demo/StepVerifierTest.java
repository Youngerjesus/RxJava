package com.example.demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class StepVerifierTest {

    @Test
    void test() {
        Flux<String> just = Flux.just("foo", "bar");

        StepVerifier.create(just)
                .expectNext("foo")
                .expectNext("bar")
                .verifyError(RuntimeException.class);
    }

    @Test
    void test2() {
        Flux<User> just = Flux.just(new User("test1"), new User("test2"));

        StepVerifier.create(just)
                .assertNext(u -> assertThat(u.getUsername()).isEqualTo("test1"))
                .assertNext(u -> assertThat(u.getUsername()).isEqualTo("test2"))
                .verifyComplete();
    }

    @Test
    void test3() {
        Flux<Long> interval = Flux.interval(Duration.ofMillis(100))
                        .take(10);

        StepVerifier.create(interval)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void test4() throws InterruptedException {
        Mono.delay(Duration.ofMillis(100))
                .subscribe(System.out::println);

        Thread.sleep(2000);
    }

    @Test
    void test5() {
        StepVerifier.withVirtualTime(() ->
            Mono.delay(Duration.ofHours(3)))
                .expectSubscription()
                .expectNoEvent(Duration.ofHours(2))
                .thenAwait(Duration.ofHours(1))
                .expectNextCount(1)
                .verifyComplete();
    }


    public static class User {
        private String username;

        public User(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }
    }
}
