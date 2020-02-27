package com.shadow.trap.message;

import lombok.Data;

import java.util.Date;

/**
 * @author guoda
 */
@Data
public abstract class AbstractMsgBody implements java.io.Serializable {
    /**
     * 随机ID
     */
    private String uuid;
    /**
     * 验证token
     */
    private String token;
    /**
     * 消息接收时间
     */
    private Date acceptTime;
    /**
     * 消息状态
     */
    private String status;
    /**
     * 租户信息
     */
    private String tenant;
}