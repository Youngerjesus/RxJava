# Reactive Streams - Schedulers 

## 스케줄러 지정

- publishOn 과 subscribeOn 을 통해서 스케줄러를 지정하는게 가능하다.
  - publishOn 은 consumer 를 다른 스레드풀에서 처리하도록 하는 반면 subscribeOn 은 publisher 와 subscriber 도 다른 스레드풀 에서 처리하도록 한다. 
  - publishOn 은 생성은 빠른데 소비가 느린 경우 (소비가 빠르면 굳이 다른 스레드를 사용할 필요가 없다.) 
  - subscribeOn 은 소비가 빠른 경우에 사용하면 된다고 한다.
  - 각 스레드 풀의 전략은 레이턴시나 처리량을 고려해서 고를 수 있다. 예시 중 하나로 우선순위를 낮게줘서 아주 천천히 실행되도록 하던가.  
- 이를 통해 publisher 와 subscriber 는 다른 스레드에서 처리가 된다.

## ETC

- 기술을 배우면 가장 기초적이고 기본적인 프로젝트를 해본다. 그러면 더욱 이해하기가 쉬워진다고 한다. 
- 유저가 만든 스레드는 데몬 스레드 (= 백그라운드 스레드) 가 아니라서 메인 스레드가 종료되더라도 살아있다.  
