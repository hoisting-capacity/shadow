package com.shadow.verify.zk;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * @author accumulate
 * @Description
 * @data 2019-06-05
 */
@Slf4j
@Component
public class Service {
    @Autowired
    private CuratorFramework zookeeperClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * zk数据为json格式转换为java对象
     *
     * @param path      路径
     * @param valueType 返回类型
     * @return 对象
     * @throws Exception 异常
     */
    <T> T getAccessObject(String path, Class<T> valueType) throws Exception {
        Stat stat = zookeeperClient.checkExists().forPath(path);
        if (stat == null) {
            throw new RuntimeException("Node not exists! error path:" + path);
        }
        byte[] b = zookeeperClient.getData().forPath(path);
        return objectMapper.readValue(b, valueType);
    }

    /**
     * zk访问path通用拼接方法
     *
     * @param rootPath 根目录
     * @param args     子目录列表
     * @return 最终目录
     */
    private String getPath(String rootPath, String... args) {
        StringBuilder stringBuffer = new StringBuilder(rootPath);
        for (String string : args) {
            stringBuffer.append("/").append(string);
        }
        return stringBuffer.toString();
    }

    public List<String> getChannelConfigs() throws Exception {
        Map map = new HashMap(10);
        List<String> channels = zookeeperClient.getChildren().forPath("/channel");
        channels.stream().flatMap(channel -> {
            List<String> tenants = getChildInfo("/channel", channel);
            map.put(channel, tenants);
            return tenants.stream();
        });
        return channels;
    }

    public List<String> getChildInfo(String parentPath, String... args) {
        String path = getPath(parentPath, args);
        try {
            return zookeeperClient.getChildren().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
            return asList(String.format("Error in %s!!", path));
        }
    }

    public ConfigNode getRootConfig(String parentPath) {
        try {
            ConfigNode root = zookeeperClient.getChildren().forPath(parentPath)
                    .stream()
                    .collect(ConfigNode::new, (configNode, child) -> {
                        configNode.addChild(child, getRootConfig(getPath(parentPath, child)));
                    }, ConfigNode::combined);
            if (root.isEmpty()) {
                Stat stat = zookeeperClient.checkExists().forPath(parentPath);
                if (stat == null) {
                    throw new RuntimeException("Node not exists! error path:" + parentPath);
                }
                byte[] b = zookeeperClient.getData().forPath(parentPath);
                root.setValue(b);
            }
            return root;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Optional<byte[]> getProductRouteConfig(ConfigNode channelNode, String channel, String tenant, String transaction, String terminal) {
        return channelNode.getChild(channel)
                .getChild("tenant").getChild(tenant)
                .getChild("transaction").getChild(transaction)
                .getChild("terminal").getChild(terminal)
                .getChild("route").getValue();
    }

    public Set<String> getContNoPaths(ConfigNode channelNode, String channel, String tenant) {
        return channelNode.getChild(channel)
                .getChild("tenant").getChild(tenant)
                .getChild("transaction").getChilds()
                .entrySet().stream()
//                .parallel()
                .map(Map.Entry::getValue)
                .map(transaction -> transaction.getChild("terminal").getChilds())
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .map(Map.Entry::getValue)
                .map(terminal -> terminal.getChild("route"))
                .map(ConfigNode::getValue)
                .collect(TreeSet::new, (set, optBytes) -> {
                    optBytes.ifPresent(bytes -> {
                        try {
                            String contNoPath = objectMapper.readValue(bytes, RouteConfigInfo.class).getContNoPath();
                            if (!contNoPath.isEmpty()) {
                                set.add(contNoPath);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }, TreeSet::addAll);
    }

    public ConfigInfo getConfigInfo(ConfigNode channelNode, String channel, String tenant) {
        ConfigInfo configInfo = new ConfigInfo();
        ConfigNode tenantNode = channelNode.getChild(channel)
                .getChild("tenant").getChild(tenant);
        tenantNode.getChild("common").getValue().ifPresent(bytes -> {
            try {
                CommonConfig commonConfig = objectMapper.readValue(bytes, CommonConfig.class);
                configInfo.setTerminalExp(commonConfig.getTerminalExp());
                configInfo.setTransactionExp(commonConfig.getTransactionExp());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tenantNode.getChild("transaction").getChilds()
                .entrySet().stream()
//                .parallel()
                .map(Map.Entry::getValue)
                .map(transaction -> transaction.getChild("terminal").getChilds())
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .map(Map.Entry::getValue)
                .map(terminal -> terminal.getChild("route"))
                .map(ConfigNode::getValue)
                .forEach(optBytes -> optBytes.ifPresent(bytes -> {
                    try {
                        RouteConfigInfo routeConfigInfo = objectMapper.readValue(bytes, RouteConfigInfo.class);
                        configInfo.readRouteConfig(routeConfigInfo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
        return configInfo;
    }

    @Data
    public class ConfigInfo {
        String terminalExp;
        String transactionExp;
        Set<String> contNoPathSet = new TreeSet<>();
        Set<String> productPathSet = new TreeSet<>();
        Set<String> applyNoPathSet = new TreeSet<>();

        public void readRouteConfig(RouteConfigInfo routeConfigInfo) {
            if (!ObjectUtils.isEmpty(routeConfigInfo.getApplyNoPath())) {
                applyNoPathSet.add(routeConfigInfo.getApplyNoPath());
            }
            if (!ObjectUtils.isEmpty(routeConfigInfo.getContNoPath())) {
                contNoPathSet.add(routeConfigInfo.getContNoPath());
            }
            if (!ObjectUtils.isEmpty(routeConfigInfo.getProductPath())) {
                productPathSet.add(routeConfigInfo.getProductPath());
            }
        }
    }
}
