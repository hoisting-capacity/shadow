package com.shadow.trap.message;

import com.shadow.trap.service.request.data.template.*;

import java.util.stream.Stream;

import static com.shadow.trap.Constants.*;

/**
 * 消息类型
 *
 * @author guoda
 * @date 2020-02-25 13:34
 */
public enum MsgType {
    /**
     * 文本消息
     */
    TEXT(TEXT_MESSAGE, "文本", TextData.class),
    /**
     * 图片消息
     */
    IMAGE(IMAGE_MESSAGE, "图片", ImageData.class),
    /**
     * 语音消息
     */
    VOICE(VOICE_MESSAGE, "语音", VoiceData.class),
    /**
     * 视频消息
     */
    VIDEO(VIDEO_MESSAGE, "视频", VideoData.class),
    /**
     * 文本卡片消息
     */
    TEXT_CARD(TEXT_CARD_MESSAGE, "文本卡片", TextCardData.class),
    /**
     * 文件消息
     */
    FILE(FILE_MESSAGE, "文件", null),
    /**
     * 图文消息
     */
    NEWS(NEWS_MESSAGE, "图文消息", null),
    /**
     * 缓存图文消息
     */
    MP_NEWS(MP_NEWS_MESSAGE, "缓存图文消息", null),
    /**
     * MarkDown消息
     */
    MARKDOWN(MARKDOWN_MESSAGE, "markdown", null),
    /**
     * 小程序通知
     */
    MINI_PROGRAM_NOTICE(MINI_PROGRAM_NOTICE_MESSAGE, "小程序通知", null),
    /**
     * 任务卡片消息
     */
    TASK_CARD(TASK_CARD_MESSAGE, "任务卡片", null);
    /**
     * 类型编码
     */
    private String code;
    /**
     * 类型名称
     */
    private String name;
    /**
     * 类型对应Class
     */
    private Class<?> dataClass;

    MsgType(String code, String name, Class<? extends DataTemplate> dataClass) {
        this.code = code;
        this.name = name;
        this.dataClass = dataClass;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Class<?> getDataClass() {
        return dataClass;
    }

    public static MsgType getMsgTypeByCode(String code) {
        return Stream.of(MsgType.values())
                .filter(msgType -> msgType.getCode().equalsIgnoreCase(code))
                .findAny()
                .orElseThrow(() -> new NullPointerException(code + " type messages are not supported"));
    }
}
