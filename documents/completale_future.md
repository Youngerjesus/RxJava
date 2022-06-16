# Completable Future 

## CompletableFuture 

- 간단하게 비동기 작업을 수행할 수 있다. 
  - 리스트의 모든 값이 안료될 때까지 기다리거나, 하나만 완료될 때까지 기다리거나 할 수 있다. 
  - 그리고 람다 표현식과 파이프라이닝을 활용하면 구조적으로 이쁘게 만들 수 있다는 장점도 있다.
- complete(), completeExceptionally() 를 통해 작업을 완료시킬 수 있다. 
- runAsync() 로 비동기 작업을 실행하면 CompletableFuture 은 ForkjoinPool.commonPool 에서 기본적으로 동작한다. 
- CompletableFuture 는 CompletionStage 를 구현하고 있다.
  - CompletionStage 는 하나의 비동기 작업을 완료하면 그거를 받고 또 다른 작업을 수행할 수 있도록 해준다. 
  - 읽어보는 걸 추천. 
  - 이때는 같은 작업을 이어서 시작한다.
- supplyAsync() 는 결과를 내야한다.
- thenAccept() 는 컨슈머 역할을 하며, thenApply 는 결과를 바탕으로 이어서 코딩을 할 때 사용할 수 있다.
- thenCompose() 를 통해서 CompletableFuture 를 결합하는게 가능하다. 결과를 CompletableFuture 로 내야하는 경우가 있는데 이 경우에 thenCompose 를 사용하지 않는 경우에는 넘겨지는 타입이 달라져서 곤란해질 수 있는데 이를 도와준다.
- exceptionally() 를 통해서 실행 중간에 나오는 예외를 처리할 수 있다.

## ETC 

- 리액티브 스타일을 명령형으로 작성하게 되면 콜백 구조를 만들어야 한다. 지나치게 복잡한 콜백 구조가 생길 수 있음.
  - 네트워크 호출은 성공과 실패 두 가지 경우를 모두 다뤄야 한다. 
  - 실패의 경우는 어떤 단계든 간에 똑같은 방식으로 처리하게 될텐데 이게 중복 로직으로 포함될 수 있다. 
- 믈라킹 호출은 두 번의 컨택스트 스위칭이 필요해진다. 
