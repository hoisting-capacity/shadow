package com.shadow.correct.service;

import com.shadow.correct.message.MsgType;
import com.shadow.correct.message.Send2MqMsg;
import com.shadow.correct.service.request.MessageRequest;
import com.shadow.correct.service.request.data.template.DataTemplate;
import com.shadow.correct.service.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple5;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;

import static com.shadow.correct.Constants.*;

/**
 * @author guoda
 * @date 2020/2/28 02:55
 */
@Slf4j
@RestController
@RequestMapping("/massage")
public class MessageService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Exchange topicExchange;
    @Autowired
    Flux<Tuple3<CorrelationData, Boolean, String>> callbackHandler;
    @Autowired
    Flux<Tuple5<Message, Integer, String, String, String>> returnHandler;

    @PostConstruct
    public void init() {
        callbackHandler.subscribe(tuple -> {
            log.info("Callback :: CorrelationData:{} ack:{}, Cause:{}", tuple.getT1(), tuple.getT2(), tuple.getT3());
        });
        returnHandler.subscribe(tuple -> {
            log.info("Return :: Message:{} replyCode:{}, replyText:{}, exchange:{}, routingKey:{}", tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5());
        });
    }


    /**
     * 测试是否联通的接口
     *
     * @return say hello 信息
     */
    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "MessageService Say Hello!";
    }

    /**
     * 企业微信发送消息端口
     *
     * @param request 请求参数，必须参照{@link com.shadow.correct.service.request.MessageRequest}格式
     * @return 发送结果 {@link com.shadow.correct.service.result.ResponseResult<String>}
     */
    @PostMapping("/send")
    public ResponseResult<String> send(@RequestBody MessageRequest request) {
        Send2MqMsg<DataTemplate> send2MqMsg = new Send2MqMsg<>();
        /* 补充信息 */
        send2MqMsg.setAcceptTime(new Date());
        /* 复制信息 */
        send2MqMsg.setToUser(request.getToUser());
        send2MqMsg.setToParty(request.getToParty());
        send2MqMsg.setToTag(request.getToTag());
        /* 转换信息 */
        MsgType msgType = MsgType.getMsgTypeByCode(request.getMsgType());
        send2MqMsg.setEnterpriseWechatMsgType(msgType);
        send2MqMsg.setContents((DataTemplate) request.getContents().toJavaObject(msgType.getDataClass()));
        /* 设置唯一ID,并添加至ack监听队列 */
        String uuid = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(uuid);
        send2MqMsg.setUuid(uuid);
        /* 发送消息 */
        rabbitTemplate.convertAndSend(topicExchange.getName(), TOPIC_ROUTING_KEY + msgType.getCode(), send2MqMsg, correlationData);
        /* 等待消息返回 */
        return callbackHandler.filter(tuple -> uuid.equalsIgnoreCase(tuple.getT1().getId()))
                .map(tuple -> {
                    if (Boolean.TRUE.equals(tuple.getT2())) {
                        return new ResponseResult<>("200", "Success!", "MessageBody.");
                    } else {
                        return new ResponseResult<>("200", tuple.getT3(), tuple.getT1().toString());
                    }
                }).blockFirst();
    }
}
