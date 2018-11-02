package com.xhkj.upms.mq.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SendInfo {

    private final static Logger log= LoggerFactory.getLogger(SendInfo.class);


    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(SimpleMailMessage mail) {
        mailSender.send(mail);
    }
    
    public JavaMailSender getMailSender(){
        return this.mailSender;
    }

 
}
