package com.paibo.sniff.wifi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.paibo.sniff.R;
import com.paibo.sniff.bean.Device;
import com.paibo.sniff.bean.SniffWifi;
import com.paibo.sniff.utils.ShellUtil;
import com.paibo.sniff.utils.StringUtil;
import com.paibo.sniff.wifi.employ.Capturer;
import com.paibo.sniff.wifi.employ.Capturer.CapturerCallBackListener;
import com.paibo.sniff.wifi.employ.WifiAdmin;

/**
 * 嗅探Wifi信息管理者
 * 
 * <p>职责：此类用来封装获取附近wifi的详细信息</p>
 * 
 * <p>能够返回的详细信息包括：</p>
 * <p>1.附近的wifi名称，信号强度，mac地址...</p>
 * <p>2.以及每个wifi下链接的设备mac,bssid...</p>
 * 
 * @author jiangbing
 *
 */
public class SniffWifiManager implements CapturerCallBackListener {
	
	public interface SniffWifiListener {
		// 捕获成功回调
		void onSniffSuccess();
		
		// 捕获失败回调
		void onSniffFail(String failStr);
	}
	
	// 捕获信息失败
	public final static int SNIFF_FAIL = 0;
		
	// 捕获信息成功
	public final static int SNIFF_SUCCESS = 1;
	
	private static SniffWifiManager wifiManager;
	
	private static SniffWifiListener mListener;
	
	// 捕获者
	public static Capturer capturer;
	
	private static Context context;
	
	// 扫描到的wifi信息，不包括所属的连接设备信息
	private List<ScanResult> mWifiList;
	
	private Map<String, SniffWifi> mCapturerMap;
	
	private List<SniffWifi> mSniffWiffList;
	
	// Constructor
	public SniffWifiManager(Context context) {
		super();
		this.context = context;
	}
	
	// Get the instance of wifiManager
	public static SniffWifiManager getInstance(Context context) {
		if (wifiManager == null) {
			wifiManager = new SniffWifiManager(context);
		}
		mListener = (SniffWifiListener) context;
		return wifiManager;
	}
	

	/**
	 * 获取包装后的wifi信息，每条wifi信息有连接的设备信息
	 * 
	 * @return
	 */
	public void startSniffWifi() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// 1.给此apk文件申请ROOT权限
				if (!makeAPKHaveRootPermission()) 
					return;
						
				// 2.查看设备是否通过第三方ROOT
				if (!checkDeviceRootStatu()) 
					return;
				
				// 3.检查wifi连接状态，并通过android系统服务获取周围wifi信息
				if (!checkWifiConnectStatu()) 
					return;
				getWifiInfo();
				
				// 4.扫描每条wifi热点下连接设备的信息
				capturer = new Capturer(SniffWifiManager.this);
				
				// 5.开始捕获
				capturer.start();
			}
		}).start();
	}
	
	/**
	 * 获取捕获到的wifi信息
	 * @return
	 */
	public List<SniffWifi> getSniffWifiInfos() {
		return mSniffWiffList;
	}
	
	/**
	 * 整合数据
	 * 
	 * @return
	 */
	private List<SniffWifi> bricolageData() {
		List<SniffWifi> sniffWifiList = new ArrayList<SniffWifi>();
		
		short num = 0;
		for (ScanResult result : mWifiList) {
			SniffWifi sniffWifi = new SniffWifi();
			sniffWifi.setSSID(result.SSID);
			sniffWifi.setSignallevel(result.level); // signal level
			sniffWifi.setBSSID(result.BSSID);
			// TODO 这里描述wifi的加密形式
			sniffWifi.setCapabilities(result.capabilities);
			sniffWifi.setHavePwd(TextUtils.isEmpty(result.capabilities));
			sniffWifi.setSSIDNum(++num);
			
			// Search devices by bssid
			SniffWifi tmpWifi = (SniffWifi) mCapturerMap.get(StringUtil.filterBSSID(sniffWifi.getBSSID()));
			
			List<Device> connectingDevices = new ArrayList<Device>();
			if (tmpWifi != null) {
				Map<String, Device> tmpMap = tmpWifi.getDevices();
				
				for (Device device : tmpMap.values()) {
					connectingDevices.add(device);
				}
			}
			sniffWifi.setConnectingDevices(connectingDevices);
			sniffWifiList.add(sniffWifi);
		}
		
		return sniffWifiList;
	}
	
	/**
	 * 获取扫描到的wifi信息，不包括所属的连接设备信息
	 * 
	 * @return
	 */
	public List<ScanResult> getWifiInfo() {
		WifiAdmin wifiAdmin = new WifiAdmin(context);
		wifiAdmin.startScan();
		mWifiList = wifiAdmin.getWifiList();
		return mWifiList;
	}
	
	/**
	 * 检查wifi的连接状态，若无，回调失败接口
	 * 
	 * @return
	 */
	private boolean checkWifiConnectStatu() {
		WifiAdmin wifiAdmin = new WifiAdmin(context);
		if (!wifiAdmin.isWifiConnecting()) {
			String failStr = context.getResources().getString(R.string.wifi_no_connect);
			mListener.onSniffFail(failStr);
			return false;
		}
		return true;
	}
	
	/**
	 * 查看设备是否通过第三方ROOT,若无，回调失败接口
	 */
	private boolean checkDeviceRootStatu() {
		if (!ShellUtil.hasRootPermission()) {
			String failStr = context.getResources().getString(R.string.device_no_root);
			mListener.onSniffFail(failStr);
			return false;
		}
		return true;
	}
	
	/**
	 * 给此apk文件申请ROOT权限,若失败，回调失败接口
	 */
	private boolean makeAPKHaveRootPermission() {
		// 1.获取应用包名
		String pkgName = context.getPackageName();
		// 2.申请Root权限
		if (!ShellUtil.upgradeRootPermission(pkgName)) {
			
			String failStr = context.getResources().getString(R.string.get_apk_root_permission_fail);
			mListener.onSniffFail(failStr);
			return false;
		}
		return true;
	}

	@Override
	public void onCreateLibcapSuccess() {
		// 创建成功，则解析之
		if (capturer != null) {
			mCapturerMap = capturer.getWifis();
			// 拼装数据
			mSniffWiffList = bricolageData();
			// 回调成功接口
			mListener.onSniffSuccess();
		}
	}
	
	@Override
	public void onParseLibcapFail(String failStr) {
		// 解析失败，回调失败接口
		mListener.onSniffFail(failStr);
	}

}
