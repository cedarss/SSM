package com.xhkj.upms.service.mq;

public interface MessageProducer {
	 public void send(Object message) throws Exception;
}
