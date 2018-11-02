package com.xhkj.upms.mq.producer;

import java.util.UUID;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.xhkj.common.annotation.SystemServiceLog;
import com.xhkj.upms.service.mq.MessageProducer;


@Service
public class RabbitMqMessageProducer implements MessageProducer{

	
	private static Logger log = LoggerFactory.getLogger(RabbitMqMessageProducer.class.getName());
	
    //自动注入RabbitTemplate模板类
    @Autowired
    private RabbitTemplate rabbitTemplate;  
    
    //回调函数: confirm确认
    final ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        	System.out.println("correlationData: " + correlationData);
        	System.out.println("ack: " + ack);
            if(!ack){
            	System.out.println("异常处理....");
            }
        }
    };
    
    //回调函数: return返回
    final ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText,
                String exchange, String routingKey) {
        	System.out.println("return exchange: " + exchange + ", routingKey: " 
                + routingKey + ", replyCode: " + replyCode + ", replyText: " + replyText);
        }
    };
    
    //发送消息方法调用: 构建Message消息
    @Override
    @SystemServiceLog(description = "邮件发送生产者")
    public void send(Object message) throws Exception {
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        //id + 时间戳 全局唯一 
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID()+(System.currentTimeMillis()+""));
        rabbitTemplate.convertAndSend("upms-user-email-exchange", "emailSendRegisterKey", message, correlationData);
    }

	
	
}
