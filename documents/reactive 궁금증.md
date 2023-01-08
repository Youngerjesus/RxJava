
## Publisher 는 어떻게 Subscriber 에게 데이터를 넘겨주지

- `subscription.request(N)` 을 전달받았을 때 어떻게 주는거지? 

## Reactive 는 Push 방식인가 Pull 방식인가? 

- ~~Push 방식이 될 순 없다. oom 이 날 수 있기 떄문에. 
- push 방식은 맞는데, backpressure 를 적용한것임 

## Publisher 와 Subscriber 의 연결은 어떻게 되는건가? 

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
