
// SendMessage.java - Sample application.  
// 短信发送测试程序  
// This application shows you the basic procedure for sending messages.  
// You will find how to send synchronous and asynchronous messages.  
//  
// For asynchronous dispatch, the example application sets a callback  
// notification, to see what's happened with messages.  

package com.sms;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.AGateway;
import org.smslib.AGateway.Protocols;
import org.smslib.Message.MessageEncodings;
import org.smslib.IOutboundMessageNotification;
import org.smslib.Library;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;

public class SendMessage {
	private static Logger LOG = LoggerFactory.getLogger(SendMessage.class);
	public void doIt() throws Exception {
		OutboundNotification outboundNotification = new OutboundNotification();
		LOG.debug("例子 ：从短信猫发送短消息。");
		LOG.debug(Library.getLibraryDescription());
		LOG.debug("Version: " + Library.getLibraryVersion());
		/*
		 * modem.com4:网关ID（即短信猫端口编号）
		 * COM4:串口名称（在window中以COMXX表示端口名称，在linux,unix平台下以ttyS0-N或ttyUSB0-N表示端口名称
		 * ），通过端口检测程序得到可用的端口
		 * 115200：串口每秒发送数据的bit位数,必须设置正确才可以正常发送短信，可通过程序进行检测。常用的有115200、9600
		 * wavecom：短信猫生产厂商，不同的短信猫生产厂商smslib所封装的AT指令接口会不一致，必须设置正确.常见的有Huawei、
		 * wavecom等厂商 最后一个参数表示设备的型号，可选
		 */
		SerialModemGateway gateway = new SerialModemGateway("modem.com5", "COM5", 115200, "wavecom", "");
		gateway.setInbound(true); // 设置true，表示该网关可以接收短信,根据需求修改
		gateway.setOutbound(true);// 设置true，表示该网关可以发送短信,根据需求修改
		gateway.setSimPin("0000");// sim卡锁，一般默认为0000或1234
		// Explicit SMSC address set is required for some modems.
		// Below is for VODAFONE GREECE - be sure to set your own!
		gateway.setSmscNumber("+8615802811764");// 短信服务中心号码
		gateway.setProtocol(Protocols.PDU);
		Service service = Service.getInstance();
		service.setOutboundMessageNotification(outboundNotification); // 发送短信成功后的回调函方法
		service.addGateway(gateway); // 将网关添加到短信猫服务中
		service.startService(); // 启动服务，进入短信发送就绪状态
		// 打印设备信息
		LOG.debug("  短信猫设备信息:");
		LOG.debug("  制造商: " + gateway.getManufacturer());
		LOG.debug("  型号: " + gateway.getModel());
		LOG.debug("  序列号: " + gateway.getSerialNo());
		LOG.debug("  SIM卡的 IMSI: " + gateway.getImsi());
		LOG.debug("  信号: " + gateway.getSignalLevel() + " dBm");
		LOG.debug("  电量: " + gateway.getBatteryLevel() + "%");
		// Send a message synchronously. 同步发送短信  构建短信
		OutboundMessage msg1 = new OutboundMessage("18980040312", "【开心一刻】公共汽车上很拥挤，一个瘦子和一个胖子站着。瘦子说：“等一个空位置，真不容易。”胖子说：“你还好些！像我，得等到两个空位才行呢！”"); // 参数1：手机号码 // 参数2：短信内容
		msg1.setEncoding(MessageEncodings.ENCUCS2); 
		OutboundMessage msg2 = new OutboundMessage("18280398654", "【开心一刻】公共汽车上很拥挤，一个瘦子和一个胖子站着。瘦子说：“等一个空位置，真不容易。”胖子说：“你还好些！像我，得等到两个空位才行呢！”"); // 参数1：手机号码 // 参数2：短信内容
		msg2.setEncoding(MessageEncodings.ENCUCS2); 
		OutboundMessage msg3 = new OutboundMessage("18716664775", "【开心一刻】公共汽车上很拥挤，一个瘦子和一个胖子站着。瘦子说：“等一个空位置，真不容易。”胖子说：“你还好些！像我，得等到两个空位才行呢！”"); // 参数1：手机号码 // 参数2：短信内容
		msg3.setEncoding(MessageEncodings.ENCUCS2); 
		OutboundMessage msg4 = new OutboundMessage("15884586795", "【开心一刻】公共汽车上很拥挤，一个瘦子和一个胖子站着。瘦子说：“等一个空位置，真不容易。”胖子说：“你还好些！像我，得等到两个空位才行呢！”"); // 参数1：手机号码 // 参数2：短信内容
		msg4.setEncoding(MessageEncodings.ENCUCS2); 
		OutboundMessage msg5 = new OutboundMessage("18108184730", "【开心一刻】公共汽车上很拥挤，一个瘦子和一个胖子站着。瘦子说：“等一个空位置，真不容易。”胖子说：“你还好些！像我，得等到两个空位才行呢！”"); // 参数1：手机号码 // 参数2：短信内容
		msg5.setEncoding(MessageEncodings.ENCUCS2); 
		Collection<OutboundMessage> msg = new ArrayList<OutboundMessage>();	
		msg.add(msg1);
		msg.add(msg2);
		msg.add(msg3);
		service.queueMessages(msg); // 执行同步发送短信
		//service.queueMessages(msg);// 执行异步发送短信
		LOG.debug(msg.toString());
		service.stopService();
	}

	/*
	 * 短信发送成功后，调用该接口。并将发送短信的网关和短信内容对象传给process接口
	 */
	public class OutboundNotification implements IOutboundMessageNotification {
		public void process(AGateway gateway, OutboundMessage msg) {
			LOG.debug("短信发送成功后-----Outbound handler called from Gateway: " + gateway.getGatewayId());
			LOG.debug(msg.toString());
		}
	}

	public static void main(String args[]) {
		SendMessage app = new SendMessage();
		try {
			app.doIt();
		} catch (Exception e) {
			LOG.debug("短信发送异常",e);
		}
	}
}