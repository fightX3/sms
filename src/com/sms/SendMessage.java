
// SendMessage.java - Sample application.  
// 短信发送测试程序  
// This application shows you the basic procedure for sending messages.  
// You will find how to send synchronous and asynchronous messages.  
//  
// For asynchronous dispatch, the example application sets a callback  
// notification, to see what's happened with messages.  

package com.sms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.AGateway.Protocols;
import org.smslib.GatewayException;
import org.smslib.Library;
import org.smslib.Message.MessageEncodings;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.modem.SerialModemGateway;

import com.sms.notification.GatewayStatusNotification;
import com.sms.notification.OutboundNotification;
import com.sms.notification.QueueSendingNotification;

public class SendMessage {
	private static final Logger LOG = LoggerFactory.getLogger(SendMessage.class);
	public SendMessage() {
		//用于队列消息发送的回调  
		OutboundNotification outboundNotification = new OutboundNotification();
		Service.getInstance().setOutboundMessageNotification(outboundNotification);  
		//用于获取网关的状态的回调
		GatewayStatusNotification gatewayStatusNotification = new GatewayStatusNotification();
		Service.getInstance().setGatewayStatusNotification(gatewayStatusNotification);
		//用于获取队列发送中的回调
		QueueSendingNotification queueSendingNotification = new QueueSendingNotification();
		Service.getInstance().setQueueSendingNotification(queueSendingNotification);
	}
	public static String getValue(String key) throws IOException{
		Properties pro =  new Properties();
		pro.load(SendMessage.class.getClassLoader().getResourceAsStream("sms.properties") );
		return pro.getProperty(key);
	}
	//从配置文件中读取网关配置
	private SerialModemGateway initGateway() throws TimeoutException, GatewayException, IOException, InterruptedException{
		Properties pro =  new Properties();
		pro.load(SendMessage.class.getClassLoader().getResourceAsStream("sms.properties") );
		String com = (getValue("com")==null||"".equals(getValue("com")))?"COM3":getValue("com").trim();
		int bard = (getValue("bard")==null||"".equals(getValue("bard")))?115200:Integer.valueOf(getValue("bard").trim());
		String factory = (getValue("com")==null||"".equals(getValue("com")))?"wavecom":getValue("com").trim();
		return initGateway(com, bard, factory);
	}
	private SerialModemGateway initGateway(String com,int bard,String factory) throws TimeoutException, GatewayException, IOException, InterruptedException{
		//LOG.debug(Library.getLibraryDescription());
		LOG.info("SMSlib Version: " + Library.getLibraryVersion());
		/*
		 * modem.com4:网关ID（即短信猫端口编号）
		 * COM4:串口名称（在window中以COMXX表示端口名称，在linux,unix平台下以ttyS0-N或ttyUSB0-N表示端口名称
		 * ），通过端口检测程序得到可用的端口
		 * 115200：串口每秒发送数据的bit位数,必须设置正确才可以正常发送短信，可通过程序进行检测。常用的有115200、9600
		 * wavecom：短信猫生产厂商，不同的短信猫生产厂商smslib所封装的AT指令接口会不一致，必须设置正确.常见的有Huawei、
		 * wavecom等厂商 最后一个参数表示设备的型号，可选
		 */
		SerialModemGateway gateway = new SerialModemGateway("modem."+com, com, bard, factory, "");
		gateway.setInbound(true); // 设置true，表示该网关可以接收短信,根据需求修改
		gateway.setOutbound(true);// 设置true，表示该网关可以发送短信,根据需求修改
		gateway.setProtocol(Protocols.PDU);
		gateway.setSimPin("0000");// sim卡锁，一般默认为0000或1234
		// Explicit SMSC address set is required for some modems.
		// Below is for VODAFONE GREECE - be sure to set your own!
		//gateway.setSmscNumber("+8615802811764");// 短信服务中心号码
		gateway.setProtocol(Protocols.PDU);
		return gateway;
	}
	/**
	 * 发送短信（单发） 
	 * @param id 数据id
	 * @param phone  接收人  号码
	 * @param content  内容
	 * @throws Exception
	 */
	public void send(long id,String phone,String content) throws Exception {
		SerialModemGateway gateway = initGateway();
		Service.getInstance().addGateway(gateway); // 将网关添加到短信猫服务中
		Service.getInstance().startService(); // 启动服务，进入短信发送就绪状态
		if(LOG.isInfoEnabled()){
			// 打印设备信息
			LOG.info("  短信猫设备信息:");
			LOG.info("  制造商: " + gateway.getManufacturer());
			LOG.info("  型号: " + gateway.getModel());
			LOG.info("  序列号: " + gateway.getSerialNo());
			LOG.info("  SIM卡的 IMSI: " + gateway.getImsi());
			LOG.info("  信号: " + gateway.getSignalLevel() + " dBm");
			LOG.info("  电量: " + gateway.getBatteryLevel() + "%");
		}
		// Send a message synchronously. 同步发送短信  构建短信
		OutboundMessage msg1 = new OutboundMessage(phone, content); // 参数1：手机号码 // 参数2：短信内容
		msg1.setEncoding(MessageEncodings.ENCUCS2); //发送中文需要设置的编码
		Collection<OutboundMessage> msg = new ArrayList<OutboundMessage>();	
		msg1.setId(id+"");
		msg.add(msg1);
		//service.sendMessages(msg);// 执行同步发送短信
		Service.getInstance().queueMessages(msg);// 执行异步发送短信
	}
	
	/**
	 * 发送短信（群发） 
	 * @param phones  接收人  号码
	 * @param content  内容
	 * @throws Exception
	 */
	public void send(long[] ids,String[] phones,String content) throws Exception {
		if(phones != null && phones.length>0 && content != null && !"".equals(content)){
			SerialModemGateway gateway = initGateway();
			Service.getInstance().addGateway(gateway); // 将网关添加到短信猫服务中
			Service.getInstance().startService(); // 启动服务，进入短信发送就绪状态
			if(LOG.isInfoEnabled()){
				// 打印设备信息
				LOG.info("  短信猫设备信息:");
				LOG.info("  制造商: " + gateway.getManufacturer());
				LOG.info("  型号: " + gateway.getModel());
				LOG.info("  序列号: " + gateway.getSerialNo());
				LOG.info("  SIM卡的 IMSI: " + gateway.getImsi());
				LOG.info("  信号: " + gateway.getSignalLevel() + " dBm");
				LOG.info("  电量: " + gateway.getBatteryLevel() + "%");
			}
			//  构建短信
			Collection<OutboundMessage> msg = new ArrayList<OutboundMessage>();	
			for(int i=0;i<phones.length;i++){
				if(phones[i] != null && !"".equals(phones[i]) ){
					OutboundMessage msg1 = new OutboundMessage(phones[i], content); // 参数1：手机号码 // 参数2：短信内容
					msg1.setId(ids[i]+"");
					msg1.setEncoding(MessageEncodings.ENCUCS2); //发送中文需要设置的编码
					msg.add(msg1);
				}
			}
			//service.sendMessages(msg);// 执行同步发送短信
			Service.getInstance().queueMessages(msg);// 执行异步发送短信
		}
	}
	
	public static void main(String args[]) {
		SendMessage app = new SendMessage();
		try {
			app.send(2,"18108184730","Ehcache 3.0.0.rc1 发布了。EhCache 是一个纯Java的进程内缓存框架，具有快速、精干等特点，是Hibernate中默认的CacheProvider。Ehcache 3.0.0.rc1 发布了。EhCache 是一个纯Java的进程内缓存框架，具有快速、精干等特点，是Hibernate中默认的CacheProvider。");
		} catch (Exception e) {
			LOG.error("短信发送异常",e);
		}
		LOG.info("主方法完成");
	}
}