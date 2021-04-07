package reactor.netty;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.core.publisher.Flux;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Shadow
 * @date 2019-07-24
 */
class ReactorNettyTcpClient {
    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        // Config Event Loop Group with 1 selectCount、4 workerCount and daemon model。
        LoopResources loop = LoopResources.create("event-loop", 1, 4, true);
        // apply the following configuration by using ConnectionProvider get a "fixed" connection pool
        ConnectionProvider provider = ConnectionProvider.fixed("fixed", 50, 30000);
        // Creates a TcpClient instance that is ready for configuring.
        Connection connection = TcpClient.create(provider)
                .host("localhost")
                .port(8000)
                // Enables the wire logging
                .wiretap(true)
                .runOn(loop)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                // 	Netty pipeline is extended with ReadTimeoutHandler when the channel has been connected.
                .doOnConnected(conn -> conn
                        .addHandler(new ReadTimeoutHandler(10, TimeUnit.SECONDS)))
                .handle((nettyInbound, nettyOutbound) -> nettyOutbound
                        // Configures the flushing strategy to flush after every element emitted by the given Publisher. The flush operation is not scheduled, which means the flush operation is invoked after every write operation.
                        .options(NettyPipeline.SendOptions::flushOnEach)
                        // Sends "Client Say Hello !" string to the endpoint.
                        .sendString(Flux.just("Client ", "Say ", "Hello", "!"))
                        .then(
                                // Receives data from a given endpoint
                                nettyInbound.receive().asString()
                                        .doOnNext(System.out::println)
                                        .doOnNext(s -> latch.countDown())
                                        .log("tcp-client")
                                        .then()
                        ))
                .connectNow();

        assertTrue(latch.await(30, TimeUnit.SECONDS));
        // Connects the client in a blocking fashion and waits for it to finish initializing.
        connection.disposeNow();
    }

}
