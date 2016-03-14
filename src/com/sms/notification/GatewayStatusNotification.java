package com.sms.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.AGateway;
import org.smslib.IGatewayStatusNotification;
import org.smslib.AGateway.GatewayStatuses;
/**
 * 用于获取网关的状态的回调
 * @author Administrator
 *
 */
public class GatewayStatusNotification implements IGatewayStatusNotification{
	private static final Logger LOG = LoggerFactory.getLogger(GatewayStatusNotification.class);
	
	@Override
	public void process(AGateway gateway, GatewayStatuses oldStatus, GatewayStatuses newStatus) {
		LOG.info("网关："+gateway.getGatewayId()+",旧状态："+oldStatus.name()+",新状态："+newStatus.name());
	}
}