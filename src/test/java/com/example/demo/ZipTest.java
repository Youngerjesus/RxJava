package com.example.demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple3;

public class ZipTest {

    @Test
    void test1() throws InterruptedException {
        Flux<Integer> flux1 = Flux.range(0, 10);
        Flux<Integer> flux2 = Flux.range(10, 10);
        Flux<Integer> flux3 = Flux.range(20, 10);

        Flux.zip(flux1, flux2, flux3)
            .log()
            .subscribe((Tuple3<Integer, Integer, Integer> data) -> {
                System.out.println(data.getT1() + data.getT2() + data.getT3());
            });

        Thread.sleep(2000);
    }
}
