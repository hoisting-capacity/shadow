package com.shadow.accumulate;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.shadow.accumulate.reactor.netty.ReactorNettyHttpServer;
import com.shadow.accumulate.reactor.netty.ReactorNettyTcpServer;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author guoda
 * @date 2019-07-24
 */
public class Application {
    private static ExecutorService executor = new ThreadPoolExecutor(4, 8, 0, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(512),
            new ThreadFactoryBuilder()
                    .setNameFormat("Application-Thread-%d").build(),
            new ThreadPoolExecutor.DiscardPolicy());
    private static Map<String, Runnable> map = ImmutableMap.<String, Runnable>builder()
            .put("ReactorNettyTcpServer", new ReactorNettyTcpServer())
            .put("ReactorNettyHttpServer", new ReactorNettyHttpServer())
            .build();
    public static void main(String[] args) {
        Flux.fromStream(map.values().stream())
                .parallel()
                .runOn(Schedulers.fromExecutor(executor))
                .subscribe(Runnable::run);
    }
}
