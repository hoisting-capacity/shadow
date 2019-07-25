package reactor.netty;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

/**
 * @author guoda
 * @date 2019-07-24
 */
class ReactorNettyHttpClient {
    @Test
    void httpClient() {
        HttpClient.create()
                .baseUrl("localhost:8005")
                .headers(h -> h.add("Content-Type", "text/plain"))
                .compress(true)
                .wiretap(true)
                .post()
                .send((httpClientRequest, nettyOutbound) -> nettyOutbound
                        .sendString(Flux.just("Client", "Say", "Hello", "!"))
                        .then())
                .responseContent()
                .aggregate()
                .asString()
                .log("http-client")
                .doOnNext(System.out::println)
                .block();
    }
}
