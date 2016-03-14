package com.sms.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.AGateway;
import org.smslib.IQueueSendingNotification;
import org.smslib.OutboundMessage;
/**
 * 用于获取队列发送中的回调
 * 
 * @author Administrator
 *
 */
public class QueueSendingNotification implements IQueueSendingNotification{
	private static final Logger LOG = LoggerFactory.getLogger(QueueSendingNotification.class);
	
	@Override
	public void process(AGateway gateway, OutboundMessage msg) {
		LOG.info("短信发送中,网关：{} ,消息状态：{}" , gateway.getGatewayId(),msg.getMessageStatus().name());
	}
	
}