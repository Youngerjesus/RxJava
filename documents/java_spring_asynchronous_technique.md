# 자바와 스프링의 비동기 기술 

## 스레드풀 

- 스레드 풀에서 Thread.sleep() 을 거는 경우 interrupt 를 받아서 꺨 수 있다. 
- 이 경우 InterruptException 을 발생시키므로 try-catch 로 잡아야한다. 
- 스레드 풀인 ExecutorService 에서 execute() 로 실행하는 경우에는 인자로 Runnable 을 받는다. 이는 실행 결과를 받아올 수 없다.
- 하지만 submit() 이라는 메소드를 사용하면 Callable 을 인자로 받을 수 있고 이는 실행 결과를 가지고 올 수 있다. 그리고 Exception 을 가지고 올 수도 있다.


## 비동기 작업 
- future 의 isDone() 이라는 메소드 호출을 통해서 비동기 작업이 완료되었는지, 아닌지 알 수 있다. 
- 이를 이용하면 loop 를 돌면서 작업이 해당 작업이 끝났으면 이어서 처리하고, 아직 끝나지 않았으면 다른 작업들을 먼저 처리하는 event-loop 방식으로 프로그래밍 할 수 있다. 
- 콜백을 이용해서도 비동기 작업을 수행하는게 가능하다. 
- 스레드가 인터럽트 걸렸으면 중지해라는 뜻이므로 그냥 종료하면 된다. 
- ExecutionException 은 작업을 진행하다가 예외가 발생한 것이므로 이때 해당 에러에 대해서 처리해야한다.
  - 이때 예외는 ExecutionException 으로 한번 예외를 감싼 것이므로 getCause() 를 통해 예외를 한번 까서 던져야한다.

## Spring 에서 비동기 작업 

- @Async 메소드 하나만 붙이면 비동기 작업이 가능하다. (이떄 @EnableAsync 를 해줘야한다.)
  - 이때 Async 에서 사용하는 스레드 풀은 아주 기본적인 걸 사용하므로 ThreadPoolTaskExecutor() 를 사용하는게 좋다. 
  - @Async 마다 다른 스레드 풀을 적용하는 것도 가능한데 @Async(value = "threadPoolName") 을 통해서 등록하는게 가능하다.
- 이때 결과를 가지고 올려면 Future 타입으로 받아야 하며, return 으로 AsyncResult() 를 만들어야 한다.
  - ListenableFuture 를 받도록 하고 그거에 대한 콜백을 등록시키는 식으로 처리할 수도 있다.
- 많이 걸리는 작업은 Future 에 대한 처리를 HttpSession 에다가 넣어서 DB 에다가 접속하지 않고 결과를 알 수도 있다. (기존에는 DB 에다가 결과를 넣도록 하고.)
- 스프링에서 스레드 풀을 만들 땐 ThreadPoolTaskExecutor 를 사용하면 되는데 여기에서는 setCorePoolSize() 를 통해서 기본 스레드 코어 풀 사이즈를 등록할 수 있고 setMaxPoolSize() 를 통해서 최대 풀 사이즈를 등록하는게 가능하며 setQueueCapacity() 를 통해서 큐 사이즈를 등록하는 것도 가능하다.
  - 적용되는 순서는 core -> queue -> max 이다.
- 서블릿은 기본적으로 Blocking 이고 Thread-per request 이다. HttpConnection 을 만들 때 InputStream 과 OutputStream 을 구현하는데 거기서 데이터를 읽어오는 메소드가 blocking call 이라서 그렇다.
- 비동기 서블릿에서 작업하는 방법도 있는데 요청 흐름은 이렇다. 
  - 요청을 받으면 서블릿 스레드 풀에서 꺼내고 처리 작업은 작업 스레드풀에 넘기고 자신은 블라킹 당하지 않고 풀에 다시 반환한다. 
  - 작업 스레드 풀에서 처리가 다되면 서블릿 스레드 풀에서 스레드를 다시 꺼내서 응답을 내보내고 다시 반환한다.
  - 이렇게 하면 성능상에 이점이 있다. 응답속도 측면에서. 그리고 커넥션 풀에서의 병목이 작업 스레드 풀의 병목으로 이동한다.
  - 이렇게 작업할려면 컨트롤러에서 Callable 으로 리턴을 하면 된다. 
  

## ETC

- Thread Pool 은 순간 요청이 훅 들어오면 스레드 풀이 꽉 찰 수 있다. 이 순간 이후에는 대기 시간이 길어진다. (latency 가 급격히 높아진다.)
  - Thread Pool Hell 에 걸리면 Latency 가 높아진다.
  - Linkedin 의 이 문제는 블라킹 호출 + MSA 아키텍처가 합쳐지면서 블라킹이 길어지면서 생기는 문제다.  
- 
