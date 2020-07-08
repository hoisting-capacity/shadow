package com.shadow.correct.service.request.data.template;

import lombok.Data;
import lombok.ToString;

/**
 * @author guoda
 * @date 2020/2/25 14:32
 */
@Data
@ToString
public class VideoData implements DataTemplate {
    /**
     * 图片媒体文件id，可以调用上传临时素材接口获取
     */
    private String mediaId;
    /**
     * 视频消息的标题，不超过128个字节，超过会自动截断
     */
    private String title;
    /**
     * 视频消息的描述，不超过512个字节，超过会自动截断
     */
    private String description;
}
