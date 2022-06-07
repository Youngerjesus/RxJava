package com.example.demo;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Ob {

    @SuppressWarnings("Deprecation")
    public static void main(String[] args) {
        // List -> Collection -> Iterable 순대로 상속이 되어있다.
        // Iterable 은 for-each 구문을 표현하는게 가능하다.
        // Iterable 을 구현한 클래스가 있다면 for-each 를 구현하는게 가능하다.
        // Iterable: Pull 방식 next() 라는 메소드가 pull
        // Observable: Push 방식 notifyObservers() 는 push 방식이다.
        // Observer Pattern 은 문제가 있다.
        //
        Iterable<Integer> iterable = Arrays.asList(1, 2, 3, 4, 5);

        Iterable<Integer> iter = () ->
            new Iterator<Integer>() {
                int i = 0;
                final static int MAX = 100;

                @Override
                public boolean hasNext() {
                    return i < MAX;
                }

                @Override
                public Integer next() {
                    return ++i;
                }
            };

        for (Integer i : iterable) { // for-each
            System.out.println(i);
        }

        for (Integer i : iter) {
            System.out.println(i);
        }

        // Observable 은 event 를 발행하면 observer 에게 넘겨준느 역할을 한다.
        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName());
            }
        };

        IntObservable io = new IntObservable();
        io.addObserver(ob);
        ExecutorService es = Executors.newFixedThreadPool(3);
        es.execute(io);
        es.shutdown();
    }

    static class IntObservable extends Observable implements Runnable {

        @Override
        public void run() {
            for (int i=0; i<10; i++) {
                setChanged();
                notifyObservers(i);
            }
        }
    }
}
