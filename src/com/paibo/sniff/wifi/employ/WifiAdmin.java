package com.paibo.sniff.wifi.employ;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * wifi管理者
 * 
 * <p>只是获取周围的wifi信息，不包括每个wifi下连接的设备信息</p>
 * 
 * @author jiangbing
 *
 */
public class WifiAdmin {
	
	private static WifiManager mWifiManager;
	
	private static ConnectivityManager connManager;
	
	// 扫描到的网络连接信息
	private List<ScanResult> mWifiList;
	
	private Context context;
	
	// Constructor
	public WifiAdmin(Context context) {
		this.context = context;
	}
	
	/** 开始扫描 */ 
	public void startScan() {
		if (mWifiManager == null) {
			mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		}
		mWifiManager.startScan();
		// 得到扫描结果
		mWifiList = mWifiManager.getScanResults();
	}
	
	/** 获取所有扫描到的wifi信息 */
	public List<ScanResult> getWifiList() {
		return mWifiList;
	}
	
	/** wifi是否连接 */
	public boolean isWifiConnecting() {
		if (connManager == null) {
			connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		NetworkInfo netWorkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if (netWorkInfo.isConnected()) {
			return true;
		}
		return false;
	}

}
