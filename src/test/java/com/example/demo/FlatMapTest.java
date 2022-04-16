package com.example.demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Locale;

import static reactor.core.scheduler.Schedulers.parallel;

public class FlatMapTest {

    @Test
    void test1() {
        Flux.just("a", "b", "c", "d", "e", "f")
                .window(3)
                .flatMap(l -> l.map(w -> w.toUpperCase(Locale.ROOT)))
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    void test2() {
        Flux.just("a", "b", "c", "d", "e", "f")
                .window(3)
                .flatMap(l -> l.map(w -> w.toUpperCase(Locale.ROOT)).subscribeOn(parallel()))
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    void test3() {
        Flux.just("a", "b", "c", "d", "e", "f")
                .window(2)
                .flatMapSequential(l -> l.map(w -> w.toUpperCase(Locale.ROOT)))
                .subscribeOn(parallel())
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    void test4() throws InterruptedException {
        Flux.range(0, 10000)
                .map(l -> l + 1)
                .flatMap(l -> Flux.just(l).subscribeOn(parallel()))
                .doOnNext(System.out::println)
                .subscribe();

        Thread.sleep(3000);
    }
}
