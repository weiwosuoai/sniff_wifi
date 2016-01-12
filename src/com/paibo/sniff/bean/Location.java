package com.paibo.sniff.bean;

/**
 * 位置信息
 * 
 * @author jiangbing
 *
 */
public class Location {

	// 经度
	private double longitude;
	
	// 纬度
	private double latitude;
	
	// 地址
	private String address;
	
	// 位置描述信息
	private String addressDes;
	
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getAddressDes() {
		return addressDes;
	}

	public void setAddressDes(String addressDes) {
		this.addressDes = addressDes;
	}
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Location [longitude=" + longitude + ", latitude=" + latitude
				+ ", address=" + address + ", addressDes=" + addressDes + "]";
	}
}
