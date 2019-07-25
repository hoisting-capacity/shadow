package com.shadow.verify.zk;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author accumulate
 * @Description
 * @data 2019-06-06
 */
public class ConfigNode {
    private byte[] value = null;
    private Map<String, ConfigNode> childs = new HashMap<>();

    public void setValue(byte[] value) {
        this.value = value;
    }

    public Optional<byte[]> getValue() {
        return Optional.ofNullable(value);
    }

    public void addChild(String key, ConfigNode child) {
        childs.put(key, child);
    }

    public ConfigNode deleteChild(String key) {
        return childs.remove(key);
    }

    public ConfigNode getChild(String key) {
        return childs.get(key);
    }

    public Map<String, ConfigNode> getChilds() {
        return childs;
    }

    public boolean isEmpty() { return childs.isEmpty(); }

    public ConfigNode combined(ConfigNode configNode) {
        this.childs.putAll(configNode.childs);
        this.value = this.getValue().orElse(configNode.value);
        return this;
    }
}
