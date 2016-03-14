package com.sms.notification;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.AGateway;
import org.smslib.GatewayException;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.OutboundMessage.MessageStatuses;

import com.sms.SendMessage;

/**
 *  用于队列消息发送的回调  
 * 	短信发送完成后，调用该接口。并将发送短信的网关和短信内容对象传给process接口
 */
public class OutboundNotification implements IOutboundMessageNotification {

	private static final Logger LOG = LoggerFactory.getLogger(OutboundNotification.class);
	public void process(AGateway gateway, OutboundMessage msg) {
		String status = msg.getMessageStatus().name();
		if("SENT".equalsIgnoreCase(status)){
			if(LOG.isInfoEnabled()){
				LOG.info("短信发送结果,网关：{} ,消息状态：{}" ,gateway.getGatewayId(),msg.getMessageStatus().name());
			}
			try {
				delTempFile(msg.getId(), msg.getRecipient());
			} catch (IOException e) {
				LOG.error("删除临时文件异常");
			}
		}else if("UNSENT".equalsIgnoreCase(status)){
			if(LOG.isInfoEnabled()){
				LOG.info("短信发送结果,网关：{} ,消息状态：{}" ,gateway.getGatewayId(),msg.getMessageStatus().name());
			}
			try {
				createTemp( msg);
			} catch (IOException e) {
				LOG.error("创建临时UNSENT文件异常");
			}
		}else{
			if(LOG.isInfoEnabled()){
				LOG.info("短信发送结果,网关：{} ,消息状态：{}" ,gateway.getGatewayId(),msg.getMessageStatus().name());
			}
			try {
				createTemp( msg);
			} catch (IOException e) {
				LOG.error("创建临时FAILED文件异常");
			}
		}
		try {
			Service.getInstance().stopService();
		} catch (TimeoutException e) {
			LOG.error("超时异常",e);
		} catch (GatewayException e) {
			LOG.error("网关异常",e);
		} catch (SMSLibException e) {
			LOG.error("SMSlib异常",e);
		} catch (IOException e) {
			LOG.error("IO异常",e);
		} catch (InterruptedException e) {
			LOG.error("中断异常",e);
		}
	}
	private void createTemp(OutboundMessage msg) throws IOException{
		if(  msg != null   ){
			File f = new File(SendMessage.getValue("temp")+File.separator+msg.getId()+"-"+msg.getRecipient()+"-"+msg.getMessageStatus().name());
			if(!f.exists()){
				if(!f.getParentFile().exists()){
					f.getParentFile().mkdirs();
				}
				f.createNewFile();
			}
		}
	}
	private void delTempFile(String id,String phone) throws IOException{
		File f = new File(SendMessage.getValue("temp")+File.separator+id+"-"+phone+"-"+MessageStatuses.FAILED.name());
		if(f.exists()){
			f.delete();
		}
		File ff = new File(SendMessage.getValue("temp")+File.separator+id+"-"+phone+"-"+MessageStatuses.UNSENT.name());
		if(ff.exists()){
			ff.delete();
		}
	}
	
}
