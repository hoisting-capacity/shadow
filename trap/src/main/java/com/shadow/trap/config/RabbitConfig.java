package com.shadow.trap.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple5;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

import static com.shadow.trap.Constants.*;

/**
 * @author guoda
 * @date 2020/2/28 02:45
 */
@Slf4j
@Configuration
public class RabbitConfig {
    /**
     * 创建消息转换器
     *
     * @return 消息转换器引用
     */
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    private List<FluxSink<Tuple3<CorrelationData, Boolean, String>>> callbackSinks = new ArrayList<>();

    /**
     * 为模板添加消息转换器
     *
     * @param connectionFactory 连接工厂
     * @return 模板引用
     */
    @Bean
    @DependsOn("producerJackson2MessageConverter")
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory, Jackson2JsonMessageConverter producerJackson2MessageConverter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            correlationData = correlationData == null ? new CorrelationData() : correlationData;
            cause = cause == null ? "" : cause;
            Tuple3<CorrelationData, Boolean, String> tuple3 = Tuples.of(correlationData, ack, cause);
            callbackSinks.forEach(sink -> sink.next(tuple3));
        });
        return rabbitTemplate;
    }

    /**
     * 设置发送确认回调消息发布者
     *
     * @return 发送确认信息发布者引用
     */
    @Bean
    public Flux<Tuple3<CorrelationData, Boolean, String>> callbackHandler() {
        return Flux.create(tuple3FluxSink -> callbackSinks.add(tuple3FluxSink));
    }

    /**
     * 创建拒绝退回回调消息发布者
     *
     * @return 拒绝退回信息发布者引用
     */
    @Bean
    @DependsOn("rabbitTemplate")
    public Flux<Tuple5<Message, Integer, String, String, String>> returnHandler(RabbitTemplate rabbitTemplate) {
        return Flux.create(tuple5FluxSink -> rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            Tuple5<Message, Integer, String, String, String> tuple5 = Tuples.of(message, replyCode, replyText, exchange, routingKey);
            tuple5FluxSink.next(tuple5);
        }));
    }

    /**
     * 声明消息队列
     *
     * @return 消息队列引用
     */
    @Bean
    Queue queue() {
        return new Queue(TOPIC_QUEUE_NAME, true, false, false);
    }

    /**
     * 声明主题交换机
     * @return 主题交换机引用
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    /**
     * 绑定主题交换机和消息队列
     *
     * @return 消息Binding引用
     */
    @Bean
    @DependsOn({"queue", "topicExchange"})
    Binding binding(Queue queue, TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(TOPIC_ROUTING_KEY + "#");
    }
}
