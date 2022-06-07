package com.example.demo;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscription;

public class PubSub {
    public static void main(String[] args) {
        Iterable<Integer> iter = Arrays.asList(1, 2, 3, 4, 5);

        Publisher p = new Publisher() {
            Iterator<Integer> it = iter.iterator();
            ExecutorService es = Executors.newFixedThreadPool(2);

            @Override
            public void subscribe(Subscriber subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        es.execute(() -> {
                            int i = 0;
                            try {
                                while (i++ < n) {
                                    if (it.hasNext()) {
                                        subscriber.onNext(it.next());
                                    } else {
                                        subscriber.onComplete();
                                        break;
                                    }
                                }
                            } catch (RuntimeException e) {
                                subscriber.onError(e);
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Integer> s = new Subscriber<Integer>() {
            Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                this.subscription = subscription;
                subscription.request(2);
            }

            int bufferSize = 2;
            @Override
            public void onNext(Integer item) {
                System.out.println("onNext " + item);

                if (--bufferSize <= 0) {
                    bufferSize = 2;
                    this.subscription.request(2);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        p.subscribe(s);
    }
}
