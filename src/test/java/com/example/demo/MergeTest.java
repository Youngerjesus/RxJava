package com.example.demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class MergeTest {

    @Test
    void test1() {
        Flux<Long> flux1 = Flux.interval(Duration.ofMillis(100))
                .take(10);

        Flux<Long> flux2 = Flux.just(100L, 101L, 102L, 103L);

        flux1.mergeWith(flux2)
                .doOnNext(System.out::println)
                .blockLast();

    }

    @Test
    void test2() {
        Flux<Long> flux1 = Flux.interval(Duration.ofMillis(100))
                .take(10);

        Flux<Long> flux2 = Flux.just(100L, 101L, 102L, 103L);

        flux1.concatWith(flux2)
                .doOnNext(System.out::println)
                .blockLast();
    }
}
