package com.shadow.correct.service.request.data.template;

import lombok.Data;
import lombok.ToString;

/**
 * @author guoda
 * @date 2020/2/25 14:26
 */
@Data
@ToString
public class TextData implements DataTemplate {
    /**
     * 报文内容
     * 最长不超过2048个字节，超过将截断
     */
    private String content;
}
