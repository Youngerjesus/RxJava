package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class FluxScEx {
    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger("FluxScEx");
        Flux.interval(Duration.ofMillis(500))
            .publishOn(Schedulers.newSingle("pub"))
            .log()
            .subscribe(s -> log.debug("onNext: {}", s));

        log.debug("onExit");
    }
}
