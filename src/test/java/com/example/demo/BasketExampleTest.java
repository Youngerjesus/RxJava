package com.example.demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BasketExampleTest {
    @Test
    void simpleVersion() throws InterruptedException {
        List<String> basket1 = Arrays.asList(new String[]{"kiwi", "orange", "lemon", "orange", "lemon", "kiwi"});
        List<String> basket2 = Arrays.asList(new String[]{"banana", "lemon", "lemon", "kiwi"});
        List<String> basket3 = Arrays.asList(new String[]{"strawberry", "orange", "lemon", "grape", "strawberry"});
        List<List<String>> baskets = Arrays.asList(basket1, basket2, basket3);
        Flux<List<String>> basketFlux = Flux.fromIterable(baskets);

        basketFlux.concatMap(basket -> {
            Mono<List<String>> distinctFruits = Flux.fromIterable(basket).distinct().collectList();
            Mono<Map<String, Long>> countFruitsMono = Flux.fromIterable(basket)
                    .groupBy(fruit -> fruit)
                    .concatMap(groupedFlux -> groupedFlux.count()
                            .map(count -> {
                                final Map<String, Long> fruitCount = new LinkedHashMap<>();
                                fruitCount.put(groupedFlux.key(), count);
                                return fruitCount;
                            })
                    )
                    .reduce((accumulatedMap, currentMap) -> new LinkedHashMap<>() {{
                        putAll(accumulatedMap);
                        putAll(currentMap);
                    }});
            return Flux.zip(distinctFruits, countFruitsMono, (distinct, count) -> new FruitInfo(distinct, count));
        }).subscribe(fruitInfo -> {
            System.out.println(fruitInfo);
            System.out.println(Thread.currentThread().getName());
        });

        Thread.sleep(3000);
    }

    @Test
    void parallelVersion() throws InterruptedException {
        List<String> basket1 = Arrays.asList(new String[]{"kiwi", "orange", "lemon", "orange", "lemon", "kiwi"});
        List<String> basket2 = Arrays.asList(new String[]{"banana", "lemon", "lemon", "kiwi"});
        List<String> basket3 = Arrays.asList(new String[]{"strawberry", "orange", "lemon", "grape", "strawberry"});
        List<List<String>> baskets = Arrays.asList(basket1, basket2, basket3);
        Flux<List<String>> basketFlux = Flux.fromIterable(baskets);

        basketFlux.concatMap(basket -> {
            Mono<List<String>> distinctFruits = Flux.fromIterable(basket).distinct().collectList().subscribeOn(Schedulers.parallel());
            Mono<Map<String, Long>> countFruitsMono = Flux.fromIterable(basket)
                    .groupBy(fruit -> fruit)
                    .concatMap(groupedFlux -> groupedFlux.count()
                            .map(count -> {
                                final Map<String, Long> fruitCount = new LinkedHashMap<>();
                                fruitCount.put(groupedFlux.key(), count);
                                return fruitCount;
                            })
                    )
                    .reduce((accumulatedMap, currentMap) -> new LinkedHashMap<>() {{
                        putAll(accumulatedMap);
                        putAll(currentMap);
                    }})
                    .subscribeOn(Schedulers.parallel());
            return Flux.zip(distinctFruits, countFruitsMono, (distinct, count) -> new FruitInfo(distinct, count));
        }).subscribe(fruitInfo -> {
            System.out.println(fruitInfo);
            System.out.println(Thread.currentThread().getName());
        });

        Thread.sleep(3000);
    }

    @Test
    void connectableFluxSyncVersion() throws InterruptedException {
        List<String> basket1 = Arrays.asList(new String[]{"kiwi", "orange", "lemon", "orange", "lemon", "kiwi"});
        List<String> basket2 = Arrays.asList(new String[]{"banana", "lemon", "lemon", "kiwi"});
        List<String> basket3 = Arrays.asList(new String[]{"strawberry", "orange", "lemon", "grape", "strawberry"});
        List<List<String>> baskets = Arrays.asList(basket1, basket2, basket3);
        Flux<List<String>> basketFlux = Flux.fromIterable(baskets);

        basketFlux.concatMap(basket -> {
            Flux<String> source = Flux.fromIterable(basket).log().publish().autoConnect(2);
            Mono<List<String>> distinctFruits = source.distinct().collectList();
            Mono<Map<String, Long>> countFruitsMono = source
                    .groupBy(fruit -> fruit)
                    .concatMap(groupedFlux -> groupedFlux.count()
                            .map(count -> {
                                final Map<String, Long> fruitCount = new LinkedHashMap<>();
                                fruitCount.put(groupedFlux.key(), count);
                                return fruitCount;
                            })
                    )
                    .reduce((accumulatedMap, currentMap) -> new LinkedHashMap<>() {{
                        putAll(accumulatedMap);
                        putAll(currentMap);
                    }});
            return Flux.zip(distinctFruits, countFruitsMono, (distinct, count) -> new FruitInfo(distinct, count));
        }).subscribe(fruitInfo -> {
            System.out.println(fruitInfo);
            System.out.println(Thread.currentThread().getName());
        });

        Thread.sleep(3000);
    }

    @Test
    void connectableFluxAsyncVersion() throws InterruptedException {
        List<String> basket1 = Arrays.asList(new String[]{"kiwi", "orange", "lemon", "orange", "lemon", "kiwi"});
        List<String> basket2 = Arrays.asList(new String[]{"banana", "lemon", "lemon", "kiwi"});
        List<String> basket3 = Arrays.asList(new String[]{"strawberry", "orange", "lemon", "grape", "strawberry"});
        List<List<String>> baskets = Arrays.asList(basket1, basket2, basket3);
        Flux<List<String>> basketFlux = Flux.fromIterable(baskets);

        basketFlux.concatMap(basket -> {
            Flux<String> source = Flux.fromIterable(basket).log().publish().autoConnect(2);
            Mono<List<String>> distinctFruits = source.publishOn(Schedulers.parallel()).distinct().collectList();
            Mono<Map<String, Long>> countFruitsMono = source
                    .publishOn(Schedulers.parallel())
                    .groupBy(fruit -> fruit)
                    .concatMap(groupedFlux -> groupedFlux.count()
                            .map(count -> {
                                final Map<String, Long> fruitCount = new LinkedHashMap<>();
                                fruitCount.put(groupedFlux.key(), count);
                                return fruitCount;
                            })
                    )
                    .reduce((accumulatedMap, currentMap) -> new LinkedHashMap<>() {{
                        putAll(accumulatedMap);
                        putAll(currentMap);
                    }});
            return Flux.zip(distinctFruits, countFruitsMono, (distinct, count) -> new FruitInfo(distinct, count));
        }).subscribe(fruitInfo -> {
            System.out.println(fruitInfo);
            System.out.println(Thread.currentThread().getName());
        });

        Thread.sleep(3000);
    }

    static class FruitInfo {
        private final List<String> distinctFruits;
        private final Map<String, Long> countFruits;

        public FruitInfo(List<String> distinctFruits, Map<String, Long> countFruits) {
            this.distinctFruits = distinctFruits;
            this.countFruits = countFruits;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FruitInfo fruitInfo = (FruitInfo) o;

            if (distinctFruits != null ? !distinctFruits.equals(fruitInfo.distinctFruits) : fruitInfo.distinctFruits != null)
                return false;
            return countFruits != null ? countFruits.equals(fruitInfo.countFruits) : fruitInfo.countFruits == null;
        }

        @Override
        public int hashCode() {
            int result = distinctFruits != null ? distinctFruits.hashCode() : 0;
            result = 31 * result + (countFruits != null ? countFruits.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "FruitInfo{" +
                    "distinctFruits=" + distinctFruits +
                    ", countFruits=" + countFruits +
                    '}';
        }
    }
}
