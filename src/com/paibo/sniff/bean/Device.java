package com.paibo.sniff.bean;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 连接的设备信息
 *
 * <p>注意：由于获取不到连接设备的手机号码，故无此字段</p>
 */
public class Device implements Parcelable {

	private String mac;
	private String bssid;

	public Device() {
		super();
	}

	public Device(String mac, String bssid) {
		super();
		this.mac = mac;
		this.bssid = bssid;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	@Override
	public String toString() {
		return "Device [mac=" + mac + ", bssid=" + bssid + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mac);
		dest.writeString(bssid);
	}
	
	
	/**
	 * 推荐使用Parcleable方式序列化，虽然Serializable的方式较为简单
	 * 但是会将整个对象进行序列化，因此效率方面比Parceable要低 
	 */
	
	
	public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {

		@Override
		public Device createFromParcel(Parcel source) {
			Device device = new Device();
			// 注意，在writeToParcel()方法中写入的顺序和这里
			// 读取的顺序一定要相同
			device.mac = source.readString();
			device.bssid = source.readString();
			return device;
		}

		@Override
		public Device[] newArray(int size) {
			return new Device[size];
		}
		
	};

}
