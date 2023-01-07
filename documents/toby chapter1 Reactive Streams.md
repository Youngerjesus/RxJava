# 토비의 봄 스프링 리액티브 프로그래밍 (1) reactive stream 

## 데이터를 push 방식으로 전송해주는 Observer Pattern 의 문제점 

리액티브 스트림을 만들 때 옵저버 패턴을 이용했지만 문제가 있다. 

1) Complete 개념이 없다.

2) 에러 처리가 없다.

## Iterable 을 통한 데이터 전송 

```kotlin
while(iter.hasNext()) {
    println(iter.next())
}
```

- iterable 은 pull 방식이다. 가지고 오는 거니까. next() 메소드를 통해서.
- 이와 대조적인게 Observable

## Observable 을 이용한 실행 

```java
public static void main(String[]args){
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
```

- observable 을 이용하면 다른 스레드로 바꾸는 것은 쉽다. 왔을 때 다른 스레드풀을 이용하면 되니까. 
- 다만 complete 와 error 를 내지 못하는 문제가 생긴다.

## 리액티브 과정 

1) `Publisher` 에게 `subscriber` 가 등록된다.

- `publisher.subscrbe(mySubscriber)`

2) `publisher` 는 `subscription` 이라는 객체를 만들어서 `subscriber` 의 `onSubscribe()` 메소드를 호출해줘서 연결되었음을 알려준다.  

3) `publisher` 는 `subscriber` 에게 이벤트를 막 던져주지 않는다. `subscriber` 쪽에서 요청한다. 이걸 `backpressure` 라고함. 

- 요청은 `subscriber` 가 전달받은 `subscription` 객체의 `request` 메소드를 통해서 이뤄진다. 
  - 처음에는 `onSubscribe()` 에서 호출하고, 그 다음부터는 `onNext` 에서 적절한 시점마다 호출한다. 
- 그러면 `publisher` 에서 생성한 `subscription` 객체에서 `request` 메소드가 일어났을 때 `request` 메소드 내부에서 `subscriber` 의 `onNext` 를 호출해준다. 

- 그리고 완료가 되면 subscriber 의 onComplete 를 호출해주고, 에러가 났으면 onError 도 호출해주는 식으로 이뤄진다.

- 요청의 수는 `onSubscribe()` 타임에 이벤트를 달라고 요청하고, `onNext()` 타임에도 적절할 때 요청한다.

- Subcriber 쪽에 buffer 개념을 줄 수 있다. bufferSize 만큼 한번에 요청을 하고 (이 정돈 처리할 수 있다고 판단해서.) 버퍼가 어느정도 소요되면 다시 요청을 하는 식으로.

- 에러가 나면 subscriber 쪽에 onError 를 던져주도록. 


## Etc

- List 로 한번에 데이터를 보내는게 아니라 하나씩 iterable 방식으로 보내는 방식을 이용하는 것이 Reactive Streams
  - Flux 의 정의가 이게 아닐까. 
- 옵저버 패턴은 Push 방식이고 이터레이터 패턴은 Pull 방식이다.
- Subscription 을 통해서 Publisher 와 Subscriber 는 연결될 수 있다. 
- 필요한 콜백은 onSubscribe, onNext, onComplete, onError 가 있다. 여기서 onComplete 와 onError 는 둘 중 하나만 내야한다.
- Publisher 를 만들 땐 Subscriber 가 onSubscribe() 를 할 때 연결될 Subscription 객체를 전달해줘야 한다.   
  - Publisher 에 전달된 Subscriber 는 onSubscribe() 는 Subscription 을 매개로해서 Publisher 와 Subscriber 가 연결된다. 
  - Subscription 을 만듦을 통해서 Publisher 가 요청을 얼마나 줄 수 있는지, 취소할 수 있는지 를 선언할 수 있다.
  - Subscriber 안에는 onNext, onSubscribe, onComplete, onError 에 대한 정의를 해야한다.
  - Subscription.request() 메소드 안에서 데이터를 다 보냈으면 onComplete() 를 보내야한다.
