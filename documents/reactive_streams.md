# 토비의 봄 스프링 리액티브 프로그래밍 (1) reactive stream 

## 데이터를 push 방식으로 전송해주는 Observer Pattern 의 문제점 

리액티브 스트림을 만들 때 옵저버 패턴을 이용했지만 문제가 있다. 

1) Complete 개념이 없다.

2) 에러 처리가 없다.

## Etc

- 옵저버 패턴은 Push 방식이고 이터레이터 패턴은 Pull 방식이다.
- Subscription 을 통해서 Publisher 와 Subscriber 는 연결될 수 있다. 
- 필요한 콜백은 onSubscribe, onNext, onComplete, onError 가 있다. 여기서 onComplete 와 onError 는 둘 중 하나만 내야한다.
- Publisher 를 만들 땐 Subscriber 가 onSubscribe() 를 할 때 연결될 Subscription 객체를 전달해줘야 한다.   
  - Publisher 에 전달된 Subscriber 는 onSubscribe() 는 Subscription 을 매개로해서 Publisher 와 Subscriber 가 연결된다. 
  - Subscription 을 만듦을 통해서 Publisher 가 요청을 얼마나 줄 수 있는지, 취소할 수 있는지 를 선언할 수 있다.
  - Subscriber 안에는 onNext, onSubscribe, onComplete, onError 에 대한 정의를 해야한다.
  - Subscription.request() 메소드 안에서 데이터를 다 보냈으면 onComplete() 를 보내야한다.
