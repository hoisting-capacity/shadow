package com.shadow.trap.service.request.data.template;

import lombok.Data;
import lombok.ToString;

/**
 * @author guoda
 * @date 2020/2/25 14:31
 */
@Data
@ToString
public class VoiceData implements DataTemplate {
    /**
     * 图片媒体文件id，可以调用上传临时素材接口获取
     */
    private String mediaId;
}
