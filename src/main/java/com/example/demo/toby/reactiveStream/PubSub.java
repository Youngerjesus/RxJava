package com.example.demo.toby.reactiveStream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Flow;

public class PubSub {

    public static void main(String[] args) {
        Iterable<Integer> iter = Arrays.asList(1, 2, 3, 4, 5);

        Flow.Publisher p = new Flow.Publisher() {
            @Override
            public void subscribe(Flow.Subscriber subscriber) {
                Iterator<Integer> it = iter.iterator();
                subscriber.onSubscribe(new Flow.Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            while (n-- > 0) {
                                if (it.hasNext()) {
                                    subscriber.onNext(it.next());
                                }
                                else {
                                    subscriber.onComplete();
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Flow.Subscriber<Integer> s = new Flow.Subscriber<Integer>() {
            Flow.Subscription subscription;
            int bufferSize = 2;
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println("onSubscribe");
                this.subscription = subscription;
                this.subscription.request(bufferSize);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("onNext: " + item);
                if (--bufferSize <= 0) {
                    bufferSize = 2;
                    this.subscription.request(bufferSize);
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
