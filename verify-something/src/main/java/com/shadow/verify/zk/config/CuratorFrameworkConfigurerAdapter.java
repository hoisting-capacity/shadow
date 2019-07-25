package com.shadow.verify.zk.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author accumulate
 * @Description
 * @data 2019-06-05
 */
@Configuration
public class CuratorFrameworkConfigurerAdapter {
    // zookeeper服务列表：ip1:port,ip2:port,ip3:port
    private String ZOOKEEPER_SERVIC_ADDRESS = "10.7.70.164:2181,10.7.70.165:2181,10.7.70.166:2181";

    @Bean
    public CuratorFramework curatorFramework() {
        CuratorFramework zookeeperClient = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_SERVIC_ADDRESS) // 链接地址
//                .connectString("127.0.0.1:2181") // 链接地址
                .sessionTimeoutMs(10000) // 会话超时时间
                .connectionTimeoutMs(10000) // 连接超时时间
                // 重试策略：间隔5秒，最多3次
                .retryPolicy(new ExponentialBackoffRetry(5000, 3))
                .build();
        zookeeperClient.start();
        return zookeeperClient;
    }
}