package com.shadow.verify.test;

import lombok.Data;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author accumulate
 * @Description
 * @data 2019/4/8
 */
@Data
public class testWorkQueue {
    Consumer<String> function1;
    Consumer<String> function2;
    static volatile Function<Publisher, Publisher> onLastOperatorHook;

    public static void main(String[] args) {
//        WorkQueueProcessor<Integer> work_queue = WorkQueueProcessor.create();
//
//        Flux flux = work_queue
//                .map(value -> value + " " + Thread.currentThread().getId() + " CryptologyTransform " )
//                .map(value -> value + "RouterTransform ")
//                .map(value -> value + "ConverterTransform ")
//                .map(value -> value + "SpiTransform ");
//
//        flux.subscribe(value -> System.out.println( "subscribe 1: [" + Thread.currentThread().getId() + "] " + value ));
//        flux.subscribe(value -> System.out.println( "subscribe 2: [" + Thread.currentThread().getId() + "] " + value ));
//        flux.subscribe(value -> System.out.println( "subscribe 3: [" + Thread.currentThread().getId() + "] " + value ));
//
//        IntStream.range(1, 31).forEach(value -> work_queue.onNext(value));
//
////        FluxSink sink = work_queue.sink();
////        IntStream.range(1, 31).forEach(value -> sink.next(value));
//
////        Flux<Integer> flux_one = Flux.range(1,10).map(value -> 10000 + value);
////        Flux<Integer> flux_two = Flux.range(10, 10).map(value -> 20000 + value);
////        flux_two.subscribe(work_queue);
////        flux_one.subscribe(work_queue);
////        flux_one.mergeWith(flux_two).subscribe(work_queue);
//
//        work_queue.onComplete();
//        work_queue.blockLast();
//        testWorkQueue test = new testWorkQueue();
//        test.subscribe(value -> {
//                    System.out.println("Function1:" + value);
//                },
//                value -> {
//                    System.out.println("Function2:" + value);
//                });
//        test.next("test");
//        test.getFunction1();

//        Flux<String> flux = Flux.just("test double subscriber!");
//        flux.subscribe(s -> System.out.println("first: "+ s));
//        flux.subscribe(s -> System.out.println("second: "+ s));
//        Optional.ofNullable(null).map(s -> {
//            System.out.println("sssss");
//            return null;
//        }).orElse(null);
//        Subscriber<Integer> subscriber = new BaseSubscriber<Integer>() {
//            @Override
//            protected void hookOnNext(Integer value) {
//                System.out.println(value);
//            }
//        };
//        Subscriber<Integer> subscriber1 = new BaseSubscriber<Integer>() {
//            @Override
//            protected void hookOnNext(Integer value) {
//                subscriber.onNext(value);
//            }
//        };
//        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5).map(integer -> integer+1);
//        flux.subscribe(subscriber1);
//        Function<Publisher<Integer>, Flux<Integer>> operator = objectPublisher -> ((Flux<Integer>) objectPublisher).map(integer -> integer*integer);
//        Flux.just(1)
//                .doOnEach(integerSignal -> System.out.println("value = " + integerSignal.get()))
//                .map(integer -> ++integer)
////                .doOnEach(integerSignal -> System.out.println("+ = " + integerSignal.get()))
//                .doOnNext(integer ->  System.out.println("+ = " + integer))
//                .map(integer -> integer * integer)
//                .doOnNext(integer ->  System.out.println("* = " + integer))
//                .subscribe();

//        AtomicInteger atomicInteger = new AtomicInteger(0);
//        AtomicInteger atomicInteger2 = new AtomicInteger(0);
//        int value = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).sorted()
//                .peek(System.out::println)
//                .peek(atomicInteger::addAndGet)
//                .filter(integer -> integer < 4)
//                .peek(atomicInteger2::addAndGet)
//                .mapToInt(Integer::intValue).sum();
//        System.out.println("atomicInteger = " + atomicInteger);
//        System.out.println("atomicInteger2 = " + atomicInteger2);
//        System.out.println("value = " + value);

//        StringBuilder stringBuilder = new StringBuilder(20);
//        stringBuilder.setLength(20);
//        stringBuilder.replace(5, 10, "67890");
//        System.out.println(stringBuilder.toString());

//        Stream.of(1, 2, 3, 4, 5).filter(integer -> integer > 3).map(integer -> integer + 1).filter(integer -> integer < 6).forEach(System.out::println);

        Mono.never().subscribe(System.out::println);
        Mono.empty().subscribe(System.out::println);
    }

    public void subscribe(Consumer<String> function1, Consumer<String> function2) {
        this.function1 = function1;
        this.function2 = function2;
    }

    public void next(String string) {
        this.function1.accept(string);
        this.function2.accept(string);
    }
}
