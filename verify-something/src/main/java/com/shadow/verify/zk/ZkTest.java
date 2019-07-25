package com.shadow.verify.zk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * @author accumulate
 * @Description
 * @data 2019-06-05
 */
@Component
public class ZkTest {
    @Autowired
    Service service;
    private ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"SpringContext.xml"});
        ZkTest zkTest = (ZkTest) context.getBean("zkTest");
        zkTest.doTest();
    }

    public void doTest() {
        long startTime = System.currentTimeMillis();
        ConfigNode channelNode = this.service.getRootConfig("/channel");
        long getRootTime = System.currentTimeMillis();
        System.out.println("获取Root时间：" + (getRootTime - startTime) + "ms");
        this.service.getProductRouteConfig(channelNode, "08", "life", "804", "0")
                .ifPresent(bytes -> {
                    try {
                        System.out.println("查询时间：" + (System.currentTimeMillis() - getRootTime) + "ms");
                        RouteConfigInfo configInfo = objectMapper.readValue(bytes, RouteConfigInfo.class);
                        System.out.println(configInfo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        startTime = System.currentTimeMillis();
        Set<String> set = this.service.getContNoPaths(channelNode, "08", "life");
        System.out.println("查询时间：" + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println(set);

        startTime = System.currentTimeMillis();
        Service.ConfigInfo configInfo = this.service.getConfigInfo(channelNode, "08", "life");
        System.out.println("查询时间：" + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println(configInfo);

    }
}
