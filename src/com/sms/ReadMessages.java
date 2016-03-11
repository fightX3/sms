
// ReadMessages.java - Sample application.  
// 短信读取程序  
// This application shows you the basic procedure needed for reading  
// SMS messages from your GSM modem, in synchronous mode.  
//  
// Operation description:  
// The application setup the necessary objects and connects to the phone.  
// As a first step, it reads all messages found in the phone.  
// Then, it goes to sleep, allowing the asynchronous callback handlers to  
// be called. Furthermore, for callback demonstration purposes, it responds  
// to each received message with a "Got It!" reply.  
//  
// Tasks:  
// 1) Setup Service object.  
// 2) Setup one or more Gateway objects.  
// 3) Attach Gateway objects to Service object.  
// 4) Setup callback notifications.  
// 5) Run  

package com.sms;

import java.util.ArrayList;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.AGateway;
import org.smslib.AGateway.GatewayStatuses;
import org.smslib.AGateway.Protocols;
import org.smslib.ICallNotification;
import org.smslib.IGatewayStatusNotification;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOrphanedMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.Library;
import org.smslib.Message.MessageTypes;
import org.smslib.Service;
import org.smslib.crypto.AESKey;
import org.smslib.modem.SerialModemGateway;

public class ReadMessages {
	private static Logger LOG = LoggerFactory.getLogger(ReadMessages.class);

	public void doIt() throws Exception {
		// Define a list which will hold the read messages.
		List<InboundMessage> msgList;
		// Create the notification callback method for inbound & status report
		// messages.
		InboundNotification inboundNotification = new InboundNotification();
		// Create the notification callback method for inbound voice calls.
		CallNotification callNotification = new CallNotification();
		// Create the notification callback method for gateway statuses.
		GatewayStatusNotification statusNotification = new GatewayStatusNotification();
		OrphanedMessageNotification orphanedMessageNotification = new OrphanedMessageNotification();
		
		Service service = Service.getInstance();
		try {
			LOG.debug("例子 ：从短信猫接收短消息。");
			LOG.debug(Library.getLibraryDescription());
			LOG.debug("Version: " + Library.getLibraryVersion());
			// Create the Gateway representing the serial GSM modem.
			SerialModemGateway gateway = new SerialModemGateway("modem.com4", "COM4", 115200, "wavecom", "");
			// Set the modem protocol to PDU (alternative is TEXT). PDU is the
			// default, anyway...
			gateway.setProtocol(Protocols.PDU);
			// Do we want the Gateway to be used for Inbound messages?
			gateway.setInbound(true);
			// Do we want the Gateway to be used for Outbound messages?
			gateway.setOutbound(true);
			// Let SMSLib know which is the SIM PIN.
			gateway.setSimPin("0000");
			// Set up the notification methods.
			service.setInboundMessageNotification(inboundNotification);
			service.setCallNotification(callNotification);
			service.setGatewayStatusNotification(statusNotification);
			service.setOrphanedMessageNotification(orphanedMessageNotification);
			// Add the Gateway to the Service object.
			service.addGateway(gateway);
			// Similarly, you may define as many Gateway objects, representing
			// various GSM modems, add them in the Service object and control
			// all of them.
			// Start! (i.e. connect to all defined Gateways)
			service.startService();
			// Printout some general information about the modem.
			// 打印设备信息
			LOG.debug("  短信猫设备信息:");
			LOG.debug("  制造商: " + gateway.getManufacturer());
			LOG.debug("  型号: " + gateway.getModel());
			LOG.debug("  序列号: " + gateway.getSerialNo());
			LOG.debug("  SIM卡的 IMSI: " + gateway.getImsi());
			LOG.debug("  信号: " + gateway.getSignalLevel() + " dBm");
			LOG.debug("  电量: " + gateway.getBatteryLevel() + "%");
			// In case you work with encrypted messages, its a good time to
			// declare your keys.
			// Create a new AES Key with a known key value.
			// Register it in KeyManager in order to keep it active. SMSLib will
			// then automatically
			// encrypt / decrypt all messages send to / received from this
			// number.
			service.getKeyManager().registerKey("+306948494037",
					new AESKey(new SecretKeySpec("0011223344556677".getBytes(), "AES")));
			// Read Messages. The reading is done via the Service object and
			// affects all Gateway objects defined. This can also be more
			// directed to a specific
			// Gateway - look the JavaDocs for information on the Service method
			// calls.
			msgList = new ArrayList<InboundMessage>();
			service.readMessages(msgList, MessageClasses.ALL);
			for (InboundMessage msg : msgList){
				LOG.debug(msg.toString());
			}
			// Sleep now. Emulate real world situation and give a chance to the
			// notifications
			// methods to be called in the event of message or voice call
			// reception.
			LOG.debug("Now Sleeping - Hit <enter> to stop service.");
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.stopService();
		}
	}

	public class InboundNotification implements IInboundMessageNotification {
		public void process(AGateway gateway, MessageTypes msgType, InboundMessage msg) {
			if (msgType == MessageTypes.INBOUND)
				LOG.debug(">>> New Inbound message detected from Gateway: " + gateway.getGatewayId());
			else if (msgType == MessageTypes.STATUSREPORT)
				LOG.debug(
						">>> New Inbound Status " + "Report message detected from Gateway: " + gateway.getGatewayId());
			LOG.debug(msg.toString());
		}
	}

	public class CallNotification implements ICallNotification {
		public void process(AGateway gateway, String callerId) {
			LOG.debug(">>> New call detected from Gateway: " + gateway.getGatewayId() + " : " + callerId);
		}
	}

	public class GatewayStatusNotification implements IGatewayStatusNotification {
		public void process(AGateway gateway, GatewayStatuses oldStatus, GatewayStatuses newStatus) {
			LOG.debug(">>> Gateway Status change for " + gateway.getGatewayId() + ", OLD: " + oldStatus + " -> NEW: " + newStatus);
		}
	}

	public class OrphanedMessageNotification implements IOrphanedMessageNotification {
		public boolean process(AGateway gateway, InboundMessage msg) {
			LOG.debug(">>> Orphaned message part detected from " + gateway.getGatewayId());
			LOG.debug(msg.toString());
			// Since we are just testing, return FALSE and keep the orphaned
			// message part.
			return false;
		}
	}

	public static void main(String args[]) {
		ReadMessages app = new ReadMessages();
		try {
			app.doIt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}