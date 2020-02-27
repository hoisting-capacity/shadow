package com.shadow.accumulate.amqp.config;

/**
 * @author guoda
 * @date 2019-10-22
 */

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitmqConfig {
    //    注入连接工厂，spring的配置，springboot可以配置在属性文件中
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connection = new CachingConnectionFactory();
        connection.setAddresses("localhost:5672");
        connection.setUsername("guest");
        connection.setPassword("guest");
        connection.setVirtualHost("/");
        return connection;
    }

    //    配置RabbitAdmin来管理rabbit
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        //用RabbitAdmin一定要配置这个，spring加载的是后就会加载这个类================特别重要
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }
    //===========================以上结合测试rabbitAdmin部分===========================================================

    //===========================以下为AMQP配置队列绑定等，spring容器加载时候就能够注入===========================================================
    //    采用AMQP定义队列、交换器、绑定等
    @Bean(name = "direct.queue01")
    public Queue queue001() {
        return new Queue("direct.queue01", true, false, false);
    }

//    @Bean(name = "test.direct01")
//    public DirectExchange directExchange() {
//        return new DirectExchange("test.direct01", true, false, null);
//    }
//
//    @Bean
//    public Binding bind001() {
//        return BindingBuilder.bind(queue001()).to(directExchange()).with("mq.#");
//    }
//
//    @Bean(name = "topic.queue01")
//    public Queue queue002() {
//        return new Queue("topic.queue01", true, false, false);
//    }
//
//    @Bean(name = "test.topic01")
//    public TopicExchange topicExchange() {
//        return new TopicExchange("test.topic01", true, false, null);
//    }
//
//    @Bean
//    public Binding bind002() {
//        return BindingBuilder.bind(queue002()).to(topicExchange()).with("mq.topic");
//    }
//
//    @Bean(name = "fanout.queue01")
//    public Queue queue003() {
//        return new Queue("fanout.queue", true, false, false);
//    }
//
//    @Bean(name = "test.fanout01")
//    public FanoutExchange fanoutExchange() {
//        return new FanoutExchange("test.fanout01", true, false, null);
//    }
//
//    @Bean
//    public Binding bind003() {
//        return BindingBuilder.bind(queue003()).to(fanoutExchange());
//    }


    //===========================注入rabbitTemplate组件===========================================================
    //    跟spring整合注入改模板，跟springboot整合的话只需要在配置文件中配置即可
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }
}