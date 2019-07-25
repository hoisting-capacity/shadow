package com.shadow.accumulate.reactor.netty;

import reactor.core.publisher.Flux;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

/**
 * @author Shadow
 * @date 2019-07-24
 */
public class ReactorNettyHttpServer implements Runnable {
    @Override
    public void run() {
        DisposableServer server = HttpServer.create()
                .host("localhost")
                .port(8005)
                .wiretap(true)
                .compress(true)
                .handle((httpServerRequest, httpServerResponse) ->
                        httpServerRequest.receive()
                                .asString()
                                .collectList()
                                .map(list -> String.join(" ", list))
                                .doOnNext(s -> System.out.println(s + " --- receive by http server"))
                                .log("http-server")
                                .then(httpServerResponse
                                        .sendString(Flux.just("Server ", "Answer ", "Hi", "!"))
                                        .then()))
                .bindNow();

        System.out.println("ReactorNettyHttpServer::start!!!");
        server.onDispose().block();
    }
}
