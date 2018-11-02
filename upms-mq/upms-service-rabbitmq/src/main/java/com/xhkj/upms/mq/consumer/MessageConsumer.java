package com.xhkj.upms.mq.consumer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.xhkj.common.annotation.SystemServiceLog;
import com.xhkj.upms.dto.fron.DtoUser;
import com.xhkj.upms.mq.email.SendInfo;

import cn.hutool.json.JSONUtil;

@Component
public class MessageConsumer implements ChannelAwareMessageListener  {
     private static Logger log = LoggerFactory.getLogger(MessageConsumer.class);  
     
     @Autowired
     private SendInfo sendInfo;
     
     @Value("${EMAIL_USERNAME}")
     private String userName;
     
     /**
 	 * 延迟消息监听并处理
 	 * 
 	 * @param message 消息实体
 	 * @param channel channel 就是当前的会话通道
 	 * @throws Exception 备注： 手动ack就是在当前channel里面调用basicAsk的方法，并传入当前消息的tagId就可以了。
 	 */
 	@Override
 	@SystemServiceLog(description = "邮件发送消费者")
 	public void onMessage(Message message, Channel channel) throws Exception {
 		
 		long deliveryTag = message.getMessageProperties().getDeliveryTag();
 		System.out.println("deliveryTag= " + deliveryTag);
 		try {
 			
 			System.out.println("------消费者处理消息------");
 			System.out.println("receive message" + message.getMessageProperties().getAppId());
 			System.out.println("receive channel" + channel.getChannelNumber() + "----");
 			System.out.println("receive message" + message.toString());
 			
 			
 			
 			// 获取消息
 			if (null != message.getBody()) {
 				String jsonStr = new String(message.getBody());
 				
 				System.out.println(jsonStr);
 				
 				DtoUser user = JSONUtil.toBean(jsonStr, DtoUser.class);
 				
 				System.out.println(user.toString());
 				
 				StringBuffer content = new StringBuffer();
 				content.append("<h4>亲爱的用户：</h4> ");
 				content.append(" <div>您好！感谢您使用测试公司服务，您申请注册的账户已成功：</div> ");
 				content.append("<div> ");
 				content.append("<span style='color: #FF9900;font-size: 22px;font-weight: 600;'>");
 				content.append("帐户名 "+user.getAccount());
 				content.append("</span>");
 				content.append("<span style='font-size: 16px;color: #BBBBBB;'>(为了保障您账号的安全性，请在5分钟内完成验证.)</span>");
 				content.append("</div> ");
 				content.append("<br></br> ");
 				content.append("<div>测试公司团队</div> ");
 				content.append("<div>");
 				content.append(cn.hutool.core.date.DateUtil.today());
 				content.append("</div>");
 				
 			// TODO 业务处理
 				SimpleMailMessage mailMessage = new SimpleMailMessage();

 				mailMessage.setFrom("cedar_s@163.com");
 	 			mailMessage.setTo(user.getEmail());
 	 			mailMessage.setSubject("用户注册成功");
 	 			mailMessage.setText(content.toString());
 	 			
 	 			sendInfo.sendMail(mailMessage); 
 				
 			}
 			// 手动确认 - false只确认当前一个消息收到，true确认所有consumer获得的消息
 			channel.basicAck(deliveryTag, false);
 		} catch (Exception e) {
 			System.out.println("message consume failed: " + e.getMessage());
 			if (message.getMessageProperties().getRedelivered()){
 				System.out.println("消息已重复处理失败,拒绝再次接收...");
                //拒绝消息
                channel.basicReject(deliveryTag, false); 
//                channel.basicReject(deliveryTag, true); 
            }else{
                //requeue为是否重新回到队列
            	System.out.println("消息即将再次返回队列处理...");
                channel.basicNack(deliveryTag, false, true); 
            }
 		}
 	}

}
