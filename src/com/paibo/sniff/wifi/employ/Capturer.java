package com.paibo.sniff.wifi.employ;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paibo.sniff.bean.Device;
import com.paibo.sniff.bean.SniffWifi;
import com.paibo.sniff.utils.LogUtil;
import com.paibo.sniff.utils.ShellUtil;
import com.paibo.sniff.wifi.model.Libcap;
import com.paibo.sniff.wifi.model.Libcap.Packet;
import com.paibo.sniff.wifi.model.Radiotap;
import com.paibo.sniff.wifi.model.WlanFrame;

/**
 * 捕获者
 * 
 * <p>捕获周围的wifi下的设备连接信息</p>
 * 
 * @author jiangbing
 *
 */
public class Capturer {
	
	public interface CapturerCallBackListener {
		
		// 创建libcap文件成功时，回调
		void onCreateLibcapSuccess();
		
		// 解析libcap文件失败时，回调
		void onParseLibcapFail(String failStr);
	}
	
	// 捕获周围wifi信息，创建libcap文件，默认的时间，若不设置则默认为最大数65535m
	// 这里我们默认为2分钟：2*60
	// 注意：单位时间为秒，不是毫秒
//	private static int DEFAULT_TIME = 120;
	
	private static int PACKAGE_NUM = 600; // 600，设置1000的时候出现过内存溢出的情况
	
//	private static int CHECK_LIBCAP_FILT_CYCLE = 5000; // 5秒
//	
//	// libcap文件大小
//	private long libcapSize = 0;
//	
//	// 查询的次数
//	private short checkTimes = 0;
	
	// 回调对象
	private Object callback;
	
	// libcap文件放置目录
	public static String LIBCAP_DIR = "/sdcard/Download/tmp.pcap";
	
	private Map<String, SniffWifi> wifis;
	
	// Constructor
	public Capturer(Object callback) {
		super();
		this.callback = callback;
	}

	/**
	 * 开始捕获信息
	 */
	public void start() {
		
		/** 此线程用来创建libcap文件 */
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// 删除已有的libcap文件
				deleteLibcapFile();
				createLibcapFile();
			}
		}).start();
		
		/** 此线程用来查看libcap文件是否已经生成完毕 */
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						// 等待7秒后，回调成功
						Thread.sleep(7 * 1000);
						// 关闭无线网卡
						shutDownDeviceIw();
						// 创建libcap格式文件完成后，回调onCreateLibcapSuccess()
						((CapturerCallBackListener) callback).onCreateLibcapSuccess();
						break;
						
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
//					File libcapFile = new File(LIBCAP_DIR);
//					long size = libcapFile.length();
//					if (checkTimes == 3 && 
//							libcapFile.exists()
//							&& size != 0
//							&& size >= libcapSize) { // 文件创建完成
//						
//						// 关闭无线网卡
//						shutDownDeviceIw();
//						
//						// 创建libcap格式文件完成后，回调onCreateLibcapSuccess()
//						((CapturerCallBackListener) callback).onCreateLibcapSuccess();
//						break;
//					} else {
//						checkTimes++;
//						
//						libcapSize = libcapFile.length();
//						
//						if (checkTimes == 3 && libcapSize == 0) {
//							LogUtil.e("The libcap file has not been created !!!");
//							// 关闭无线网卡
//							shutDownDeviceIw();
//							((CapturerCallBackListener) callback).onParseLibcapFail("数据文件创建失败");
//							break;
//						}
//						
//						try {
//							Thread.sleep(CHECK_LIBCAP_FILT_CYCLE);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
					
				}
			}
		}).start(); 
	}
	
	/** 获取解析出来的wifi信息 */
	public Map<String, SniffWifi> getWifis() {
		parseLibcapFile();
		return wifis;
	}
	
	/**
	 * 解析libcap文件
	 */
	private void parseLibcapFile() {
		
		wifis = new HashMap<String, SniffWifi>();
		
		try {
			Libcap libcap = new Libcap(LIBCAP_DIR);
			List<Packet> dPackets = new ArrayList<Packet>();
			List<Packet> packets = libcap.getPackets();
			Radiotap r = null;
			WlanFrame wf = null;
			String ssidMac = null;
//			SSID ssid = null;
			SniffWifi sniffWiff = null;
			Map<String, Device> devices = new HashMap<String, Device>();
			Device d = null;
			int i = 0;
			for(Packet p: packets) {
				i++;
				r = new Radiotap(p.getData());
//				System.out.println(i + " - " + p.getData().length + Arrays.toString(p.getData()));
				wf = new WlanFrame(r.getData());
//				System.out.println(i + " - " + r.getData().length + " - " + Arrays.toString(r.getData()));
				wf.parse();
				// parse frame - type:data, subtype:data
				if(wf.isNorDataFrame()) {
					// add ssid
					if(!wifis.containsKey(ssidMac = wf.getBSSID())) {
						sniffWiff = new SniffWifi();
						sniffWiff.setBSSID(ssidMac);
						wifis.put(ssidMac, sniffWiff);
					} else {
						sniffWiff = wifis.get(ssidMac);
					}
					// associate devices with ssid
					if(!wf.transToAp() && !wf.transFromAp()) {
						devices.put(wf.getAddr1(), new Device(wf.getAddr1(), wf.getBSSID()));
						devices.put(wf.getAddr2(), new Device(wf.getAddr2(), wf.getBSSID()));
					} else if (!wf.transToAp() &&!wf.transFromAp()) {
						devices.put(wf.getAddr2(), new Device(wf.getAddr1(), wf.getBSSID()));
					} else if (wf.transToAp() && !wf.transFromAp()) {
						devices.put(wf.getAddr3(), new Device(wf.getAddr3(), wf.getBSSID()));
					}
					sniffWiff.addDevices(devices);
					
					dPackets.add(p);
				}
			}
			
//			System.out.println("数据帧总数：" + dPackets.size());
//			for(Entry<String, SniffWifi> e : wifis.entrySet()) {
//				System.out.print("key=" + e.getKey() + ", value=");
//				for(Entry<String, Device> dev: e.getValue().getDevices().entrySet()) {
//					System.out.println("device: " + dev);
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
			
			LogUtil.e("Parse the libcap file error !!!");
			// 解析libcap格式文件失败，回调onParseLibcapFail()
			((CapturerCallBackListener) callback).onParseLibcapFail("数据解析失败");
		}
	}
	
	/**
	 * 生成libcap格式文件，此文件用来解析出周围的wifi信息以及wifi下连接的设备信息
	 * 
	 * <p>注意：这是一个极为耗时的操作</p>
	 * 
	 */
	private boolean createLibcapFile() {
		
		LogUtil.d("start to create libcap file ...");
		
		List<String> commandList = new ArrayList<String>();
		
		/**
		 * 需要执行的命令行：
		 * 
		 * 	su
		 *	iw phy phy0 interface add mon0 type monitor flags none / iw dev phy0 interface add mon0 type monitor flags none
		 *	ifconfig mon0 up
		 *	tcpdump -c 100 -w /sdcard/Download/test.pcap -vv -i mon0 -s 0  注：-c后面设置抓的包数， -w后面设置文件存储路径
		 *	ifconfig mon0 down
		 *	iw dev mon0 del
		 * 
		 */
		
		String command = "su";
		commandList.add(command);
		
		command = "iw phy phy0 interface add mon0 type monitor flags none";
		commandList.add(command);
		
		command = "ifconfig mon0 up";
		commandList.add(command);
		
		command = "tcpdump -c " + PACKAGE_NUM + " -w " + LIBCAP_DIR + " -vv -i mon0 -s 0";
		commandList.add(command);
//		command = "tcpdump -G " + DEFAULT_TIME + " -vv -i mon0 -s 0 -w " + LIBCAP_DIR;
		return ShellUtil.execommand(commandList, true, true).getRsCode() == 0 ? true : false;
	}
	
	/**
	 * 关闭命令行中启动的无线网卡
	 */
	private boolean shutDownDeviceIw() {
		List<String> commandList = new ArrayList<String>();
		
		String command = "ifconfig mon0 down";
		commandList.add(command);
		
		command = "iw dev mon0 del";
		commandList.add(command);
		
		return ShellUtil.execommand(commandList, true, true).getRsCode() == 0 ? true : false;
	}
	
	/**
	 * 重新获取wifi信息前，请删除libcap文件
	 */
	private void deleteLibcapFile() {
		File file = new File(LIBCAP_DIR);
		if (file.exists()) {
			file.delete();
		}
	}
	
	/**
	 * libcap文件是否存在
	 * @return
	 */
	private boolean isLibcapFileExists() {
		File file = new File(LIBCAP_DIR);
		if (file.exists()) {
			return true;
		}
		return false;
	}

}
