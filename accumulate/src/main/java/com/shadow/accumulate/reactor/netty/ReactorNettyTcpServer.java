package com.shadow.accumulate.reactor.netty;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.core.publisher.Flux;
import reactor.netty.DisposableServer;
import reactor.netty.NettyPipeline;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpServer;

import java.util.concurrent.TimeUnit;

/**
 * @author Shadow
 * @date 2019-07-24
 */
public class ReactorNettyTcpServer implements Runnable {
    @Override
    public void run() {
        // Config Event Loop Group with 1 selectCount、4 workerCount and daemon model。
        LoopResources loop = LoopResources.create("event-loop", 1, 4, true);
        DisposableServer server = TcpServer.create()
                .host("localhost")
                .port(8004)
                // Enables the wire logging
                .wiretap(true)
                .runOn(loop)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                // Netty pipeline is extended with ReadTimeoutHandler when a remote client is connected.
                .doOnConnection(connection -> connection
                        .addHandler(new ReadTimeoutHandler(10, TimeUnit.SECONDS)))
                .handle((inbound, outbound) ->
                        // 	Receives data from the connected clients
                        inbound.receive()
                                .asString()
                                .flatMap(s -> {
                                    System.out.println(s + " --- receive by tcp server");
                                    return outbound
                                            // Configures the flushing strategy to flush after every element emitted by the given Publisher. The flush operation is not scheduled, which means the flush operation is invoked after every write operation.
                                            .options(NettyPipeline.SendOptions::flushOnEach)
                                            // Sends Hello World ! string to the connected clients
                                            .sendString(Flux.just("Server ", "Answer ", "Hi", "!"))
                                            .then();
                                })
                                .log("tcp-server"))
                .bindNow();
        System.out.println("ReactorNettyTcpServer::start!!!");
        // Starts the server in a blocking fashion and waits for it to finish initializing.
        server.onDispose().block();
    }
}
