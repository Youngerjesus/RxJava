package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@EnableAsync
@SpringBootApplication
public class DemoApplication {

	@RestController
	public static class MyController {
		Logger log = LoggerFactory.getLogger("MyController");

		@GetMapping("/async")
		public Callable<String> callable() {
			log.info("MyController/async");
			return () -> {
				log.info("async");
				Thread.sleep(2000);
				return "hello";
			};
		}

		@GetMapping("/sync")
		public String callable2() throws InterruptedException {
			log.info("MyController/sync");
			Thread.sleep(2000);
			return "hello";
		}
	}

	@Component
	public static class MyService {
		@Async
		public Future<String> hello() throws InterruptedException {
			Logger log = LoggerFactory.getLogger("MyService");
			log.info("MyService.Hello");
			Thread.sleep(10000);
			return new AsyncResult<>("Hello");
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Autowired MyService myService;

	@Bean
	ApplicationRunner run() {
		return args -> {
			Logger log = LoggerFactory.getLogger("ApplicationRunner");
			log.info("run()");
			Future<String> f = myService.hello();
			log.info("future.isDone: " + f.isDone());
			log.info("future.get(): " + f.get());
			log.info("exit");
		};
	}

	@Bean
	ThreadPoolTaskExecutor tp() {
		ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
		te.setCorePoolSize(10);
		te.setMaxPoolSize(100);
		te.setQueueCapacity(200);
		te.setThreadNamePrefix("myThread");
		te.initialize();
		return te;
	}
}
