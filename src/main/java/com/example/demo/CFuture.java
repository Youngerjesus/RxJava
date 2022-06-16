package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class CFuture {
    private static Logger log = LoggerFactory.getLogger("CFuture");
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f = CompletableFuture.completedFuture(1);
        f.complete(2);
        f.completeExceptionally(new RuntimeException("test"));
        System.out.println(f.get());


        CompletableFuture.runAsync(() -> {
            log.info("runAsync");
        })
        .thenRun(() -> {
            log.info("runAsync2");
        });

        log.info("exit");

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }
}
