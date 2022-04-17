package com.example.demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class ErrorTest {

    @Test
    void test1() {
        Mono<Object> mono = Mono.error(new RuntimeException());

        mono.log()
            .onErrorReturn(2)
            .doOnNext(System.out::println)
            .subscribe();
    }
}
