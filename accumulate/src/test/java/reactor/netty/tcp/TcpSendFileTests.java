package reactor.netty.tcp;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 了解如何创建TCP服务器和客户端
 *
 * @author Shadow
 */
class TcpSendFileTests {
    public static void main(String[] args) throws CertificateException, InterruptedException, SSLException {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        SslContextBuilder sslContextBuilder =
                SslContextBuilder.forServer(cert.certificate(), cert.privateKey());
        DisposableServer server =
                TcpServer.create()   // 准备要配置的TCP服务器。
                        .port(0)    // 将端口号配置为零，这将使系统启动
                        // 绑定服务器时的临时端口。
                        .secure(spec -> spec.sslContext(sslContextBuilder))   // 使用自签名证书。
                        .wiretap()  // 应用线路记录器配置。
                        .handle((in, out) ->
                                in.receive()
                                        .asString()
                                        .flatMap(s -> {
                                            try {
                                                Path file = Paths.get(TcpSendFileTests.class.getResource(s).toURI());
                                                return out.sendFile(file)
                                                        .then();
                                            } catch (URISyntaxException e) {
                                                return Mono.error(e);
                                            }
                                        })
                                        .log("tcp-server"))
                        .bindNow(); // 以阻塞方式启动服务器，并等待其完成初始化。

        assertNotNull(server);

        CountDownLatch latch = new CountDownLatch(1);
        Connection client =
                TcpClient.create()            // 准备要配置的TCP客户端。
                        .port(server.port()) // 获取服务器的端口并将其提供为该端口
                        // 客户端应该连接。
                        // 配置SSL，以提供已配置的SslContext。
                        .secure(SslContextBuilder.forClient()
                                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                .build())
                        .wiretap()           // 应用线路记录器配置。
                        .handle((in, out) ->
                                out.sendString(Mono.just("/index.html"))
                                        .then(in.receive()
                                                .asByteArray()
                                                .doOnNext(actualBytes -> {
                                                    try {
                                                        Path file = Paths.get(TcpSendFileTests.class.getResource("/index.html").toURI());
                                                        byte[] expectedBytes = Files.readAllBytes(file);
                                                        if (Arrays.equals(expectedBytes, actualBytes)) {
                                                            System.out.println("比对信息相同");
                                                            latch.countDown();
                                                        }
                                                    } catch (URISyntaxException | IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                })
                                                .log("tcp-client")
                                                .then()))
                        .connectNow();       // 阻止客户端并返回一个Connection。

        assertNotNull(client);

        assertTrue(latch.await(30, TimeUnit.SECONDS));

        server.disposeNow(); // 停止服务器并释放资源。

        client.disposeNow(); // 停止客户端并释放资源。
    }
}
