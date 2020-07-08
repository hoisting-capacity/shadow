package com.shadow.correct.message;

import com.shadow.correct.service.request.data.template.DataTemplate;
import lombok.Data;

import java.io.Serializable;

/**
 * 企业微信消息体
 *
 * @author xiaozhao
 * @date 2020-02-25 13:33
 */
@Data
public class Send2MqMsg<T extends DataTemplate> extends AbstractMsgBody implements Serializable {

    /**
     * 指定接收消息的成员，成员ID列表（多个接收者用‘|’分隔，最多支持1000个）。
     * 特殊情况：指定为”@all”，则向该企业应用的全部成员发送
     */
    private String toUser;
    /**
     * 指定接收消息的部门，部门ID列表，多个接收者用‘|’分隔，最多支持100个。
     * 当toUser为”@all”时忽略本参数
     */
    private String toParty;
    /**
     * 指定接收消息的标签，标签ID列表，多个接收者用‘|’分隔，最多支持100个。
     * 当toUser为”@all”时忽略本参数
     */
    private String toTag;
    /**
     * 消息类型
     */
    private MsgType enterpriseWechatMsgType;
    /**
     * 具体的消息内容，随着msgType不同，此处的数据结构也不同。
     */
    private T contents;
}
