package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class SchedulerEx {
    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger("Log");
        Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    log.debug("request");
                    sub.onNext(1);
                    sub.onNext(2);
                    sub.onNext(3);
                    sub.onNext(4);
                    sub.onNext(5);
                    sub.onComplete();
                }

                @Override
                public void cancel() {

                }
            });
        };

        Publisher<Integer> subOnPub = sub -> {
            ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                @Override
                public String getThreadNamePrefix() {
                    return "subOn-";
                }
            });
            es.execute(() -> pub.subscribe(sub));
        };

        Publisher<Integer> pubOnSub = sub -> {
            subOnPub.subscribe(new Subscriber<Integer>() {
                ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                    @Override
                    public String getThreadNamePrefix() {
                        return "pubOn-";
                    }
                });

                @Override
                public void onSubscribe(Subscription subscription) {
                    sub.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer item) {
                    es.execute(() -> sub.onNext(item));
                }

                @Override
                public void onError(Throwable throwable) {
                    es.execute(() -> sub.onError(throwable));
                }

                @Override
                public void onComplete() {
                    es.execute(() -> sub.onComplete());
                }
            });
        };

        pubOnSub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.debug("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer item) {
                log.debug("onNext");
            }

            @Override
            public void onError(Throwable throwable) {
                log.debug("onError");
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        });

        log.debug("onExit");
    }
}
