package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.*;

public class FutureEx {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Logger log = LoggerFactory.getLogger("FutureEx");
        ExecutorService es = Executors.newCachedThreadPool();

        CallbackFutureTask cft = new CallbackFutureTask(() -> {
            Thread.sleep(2000);
            if (true) throw new RuntimeException("runtime Exception");
            log.info("futureTask");
            return "Hello";
        }, result -> log.info("result: " + result),
            error -> log.info("error: " + error.getMessage()));

        es.execute(cft);

        log.info("Exit");
    }

    interface SuccessCallback {
        void onSuccess(String result);
    }

    interface ExceptionCallback {
        void onError(Throwable t);
    }

    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallback sc;
        ExceptionCallback ec;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            try {
                sc.onSuccess(get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                ec.onError(e.getCause());
            }
        }
    }
}
