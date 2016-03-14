package com.sms;
import java.util.Enumeration;

import gnu.io.*;
  
/** 
 *  
 */  
public class TestGetPortList {  
  
    /** 
     * @param args 
     * @throws Exception 
     */  
    public static void main(String[] args) throws Exception {  
        // 人工加载驱动  
        // MainTest.driverInit();  
        TestGetPortList.getCommPortList();  
        // 人工加载驱动获取端口列表  
        // TestGetPortList.getPortByDriver();  
  
    }  
  
    /** 
     * 手工加载驱动<br> 
     * 正常情况下程序会自动加载驱动，故通常不需要人工加载<br> 
     * 每重复加载一次，会把端口重复注册，CommPortIdentifier.getPortIdentifiers()获取的端口就会重复 
     */  
    public static void driverManualInit() {  
        String driverName = "com.sun.comm.Win32Driver";  
        String libname = "win32com";  
        CommDriver commDriver = null;  
        try {  
            System.loadLibrary("win32com");  
            System.out.println(libname + " Library Loaded");  
  
            commDriver = (CommDriver) Class.forName(driverName)  
                    .newInstance();  
            commDriver.initialize();  
            System.out.println("comm Driver Initialized");  
  
        } catch (Exception e) {  
            System.err.println(e);  
        }  
    }  
  
    /** 
     * 获取端口列表 
     */  
    @SuppressWarnings("rawtypes")
	public static void getCommPortList() {  
        CommPortIdentifier portId;  
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();  
        while (portEnum.hasMoreElements()) {  
            portId = (CommPortIdentifier) portEnum.nextElement();  
  
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {  
                System.out.println("串口: name-" + portId.getName()  
                        + " 是否占用-" + portId.isCurrentlyOwned());  
            } else {  
                System.out.println("并口: name-" + portId.getName()  
                        + " 是否占用-" + portId.isCurrentlyOwned());  
            }  
        }  
        System.out.println("-------------------------------------");  
    }  
  
    /** 
     *  
     */  
    public static void getPortByDriver() {  
  
        String driverName = "com.sun.comm.Win32Driver";  
        String libname = "win32com";  
        CommDriver commDriver = null;  
        try {  
            System.loadLibrary("win32com");  
            System.out.println(libname + " Library Loaded");  
  
            commDriver = (CommDriver) Class.forName(driverName).newInstance();  
            commDriver.initialize();  
            System.out.println("comm Driver Initialized");  
  
        } catch (Exception e) {  
            System.err.println(e);  
        }  
        SerialPort sPort = null;  
        try {  
  
            sPort = (SerialPort) commDriver.getCommPort("COM24",  
                    CommPortIdentifier.PORT_SERIAL);  
            System.out.println("find CommPort:" + sPort.toString());  
        } catch (Exception e) {  
            System.out.println(e.getMessage());  
        }  
  
    }  
  
}  