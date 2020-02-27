package com.shadow.trap.service.request.data.template;

import lombok.Data;
import lombok.ToString;

/**
 * @author guoda
 * @date 2020/2/25 14:34
 */
@Data
@ToString
public class TextCardData implements DataTemplate {
    /**
     * 消息的标题，不超过128个字节，超过会自动截断
     */
    private String title;
    /**
     * 消息的描述，不超过512个字节，超过会自动截断
     */
    private String description;
    /**
     * 点击后跳转的链接。
     */
    private String url;
    /**
     * 按钮文字。 默认为“详情”， 不超过4个文字，超过自动截断。
     */
    private String btnTxt;
}
