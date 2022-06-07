package com.example.demo;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Function;
import java.util.stream.Stream;

public class PubSub {
    public static void main(String[] args) {
        Stream<Integer> iter2 = Stream.iterate(1, a -> a + 1).limit(10);

        Publisher p = iterPub(iter2);
        Publisher<Integer> mapPub = mapPub(p, s -> s * 10);
        Publisher<Integer> mapPub2 = mapPub(mapPub, s -> -s);

        mapPub2.subscribe(logSub());
    }

    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> f) {
        return new Publisher<Integer>() {

            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                pub.subscribe(new DelegateSub(subscriber) {
                    @Override
                    public void onNext(Integer item) {
                        subscriber.onNext(f.apply(item));
                    }
                });
            }
        };
    }

    private static Subscriber<Integer> logSub() {
        Subscriber<Integer> s = new Subscriber<Integer>() {
            Subscription subscription;
            int bufferSize = 2;

            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                this.subscription = subscription;
                subscription.request(2);
            }

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
        return s;
    }

    private static Publisher iterPub(Stream<Integer> iter) {
        return new Publisher() {
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
    }
}
