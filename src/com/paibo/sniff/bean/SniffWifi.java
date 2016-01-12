package com.paibo.sniff.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Parcel;
import android.os.Parcelable;


public class SniffWifi implements Parcelable {

	/** The name of network */
	private String SSID;
	
	/** The address of the access point. */
	private String BSSID; // wifi的mac地址
	
	/**
     * Describes the authentication, key management, and encryption schemes
     * supported by the access point.
     * 
     * <p>如我们连接WiFi时，显示的：“通过WPA/WPA2 PSK进行保护”</p>
     */
    private String capabilities;
	
	private int signallevel; // 信号的强弱等级
	
	private boolean isHavePwd = false; // wifi是否有密码  false:无密码 true：有密码 
	
	private int SSIDNum;
	
	/** wifi下正在连接的设备 */
	private List<Device> connectingDevices;
	
	private Map<String, Device> devices;
	
	public SniffWifi() {
		super();
		this.devices = new HashMap<String, Device>();
	}
	
	public String getCapabilities() {
		return capabilities;
	}
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}
	public String getSSID() {
		return SSID;
	}
	public void setSSID(String sSID) {
		SSID = sSID;
	}
	public String getBSSID() {
		return BSSID;
	}
	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}
	public List<Device> getConnectingDevices() {
		return connectingDevices;
	}
	public void setConnectingDevices(List<Device> connectingDevices) {
		this.connectingDevices = connectingDevices;
	}
	public Map<String, Device> getDevices() {
		return devices;
	}

	public void setSignallevel(short signallevel) {
		this.signallevel = signallevel;
	}

	public boolean isHavePwd() {
		return isHavePwd;
	}

	public void setHavePwd(boolean isHavePwd) {
		this.isHavePwd = isHavePwd;
	}
	public int getSignallevel() {
		return signallevel;
	}

	public void setSignallevel(int signallevel) {
		this.signallevel = signallevel;
	}

	public int getSSIDNum() {
		return SSIDNum;
	}

	public void setSSIDNum(int sSIDNum) {
		SSIDNum = sSIDNum;
	}
	
	@Override
	public String toString() {
		return "SniffWifi [SSID=" + SSID + ", BSSID=" + BSSID
				+ ", capabilities=" + capabilities + ", signallevel="
				+ signallevel + ", isHavePwd=" + isHavePwd + ", SSIDNum="
				+ SSIDNum + ", connectingDevices=" + connectingDevices + "]";
	}
	
	public void addDevices(Map<String, Device> devices) {
		for(Entry<String, Device> d: devices.entrySet()) {
			if(!d.getKey().equals("ffffffffffff") && !this.devices.containsKey(d.getKey())) {
				this.devices.put(d.getKey(), d.getValue());
			}
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(SSID);
		dest.writeString(BSSID);
		dest.writeString(capabilities);
		dest.writeInt(signallevel);
		dest.writeByte((byte) (isHavePwd ? 1 : 0));
		dest.writeInt(SSIDNum);
		dest.writeList(connectingDevices);
	}
	
	public final static Parcelable.Creator<SniffWifi> CREATOR = new Parcelable.Creator<SniffWifi>() {

		@Override
		public SniffWifi createFromParcel(Parcel source) {
			SniffWifi sniffWifi = new SniffWifi();
			sniffWifi.SSID = source.readString();
			sniffWifi.BSSID = source.readString();
			sniffWifi.capabilities = source.readString();
			sniffWifi.signallevel = source.readInt();
			sniffWifi.isHavePwd = source.readByte() == 1;
			return sniffWifi;
		}

		@Override
		public SniffWifi[] newArray(int size) {
			return new SniffWifi[size];
		}
	};
	
}
