package com.shadow.trap;

public class Constants {
    /**
     * RabbitMQ exchange名称 定义
     */
    public static final String TOPIC_EXCHANGE_NAME = "topic-exchange";
    /**
     * RabbitMQ 一级 routingKey 定义
     * 通配符: #:一个或多个word *:一个word
     */
    public static final String TOPIC_ROUTING_KEY = "topic.";
    /**
     * RabbitMQ Queue名称 定义
     */
    public static final String QUEUE_NAME = "queue";
    public static final String TOPIC_QUEUE_NAME = TOPIC_ROUTING_KEY + QUEUE_NAME;
    /**
     * 路由消息类型定义
     */
    public static final String TEXT_MESSAGE = "text";
    public static final String IMAGE_MESSAGE = "image";
    public static final String VOICE_MESSAGE = "voice";
    public static final String VIDEO_MESSAGE = "video";
    public static final String TEXT_CARD_MESSAGE = "textcard";
    public static final String FILE_MESSAGE = "file";
    public static final String NEWS_MESSAGE = "news";
    public static final String MP_NEWS_MESSAGE = "mpnews";
    public static final String MARKDOWN_MESSAGE = "markdown";
    public static final String MINI_PROGRAM_NOTICE_MESSAGE = "miniprogram_notice";
    public static final String TASK_CARD_MESSAGE = "taskcard";
    public static final String OTHER_MESSAGE = "other";
}