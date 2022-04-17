package com.example.demo;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BackpressureTest {

    @Test
    void test1() {
        Flux<Long> flux = Flux.interval(Duration.ofMillis(100));

        flux.doOnNext(System.out::println)
                .subscribe();
    }

    @Test
    void test2() throws InterruptedException {
        List<Integer> values = new ArrayList<>();

        for (int i = 0; i < 100000; i++ ) {
            values.add(i);
        }

        Flux.fromIterable(values)
                .log()
                .onBackpressureBuffer(1000)
                .subscribe(System.out::println);

        Thread.sleep(1000);
    }

    @Test
    void test3() {
        Flux<Integer> range = Flux.range(0, 10).log();

        StepVerifier.create(range, 1)
                .expectNext(0)
                .thenRequest(1)
                .expectNext(1)
                .thenCancel()
                .verify();
    }

    @Test
    void test4() throws InterruptedException {
        Flux.range(0, 100).log()
                .doOnNext(System.out::println)
                .subscribe(new Subscriber<Integer>() {
                    private Subscription subscription;
                    private int count = 0;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.subscription = subscription;
                        subscription.request(10);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        count++;
                        if (count % 5 == 0) {
                            subscription.request(5);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("count: " + count);
                    }
                });

        Thread.sleep(2000);
    }
}
