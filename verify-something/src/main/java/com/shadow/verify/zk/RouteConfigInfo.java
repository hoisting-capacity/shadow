package com.shadow.verify.zk;

import lombok.Data;

import java.io.Serializable;

/**
 * @author accumulate
 * @Description
 * @data 2019-06-05
 */
@Data
public class RouteConfigInfo implements Serializable {
    private String productPath; // 产品编号表达式
    private String contNoPath; // 保单号表达式
    private String applyNoPath; // 投保单号表达式
}
