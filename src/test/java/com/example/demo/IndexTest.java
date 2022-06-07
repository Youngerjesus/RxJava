package com.example.demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class IndexTest {

    @Test
    void index_method_test() {
        Flux.just("a", "ab", "abc")
                .index()
                .subscribe(System.out::println);
    }
}
