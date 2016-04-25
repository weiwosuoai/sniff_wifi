package com.paibo.sniff.bean;

import java.io.Serializable;

public class Wifi implements Serializable {


	private static final long serialVersionUID = 1L;
	private String wifiName;
	private String wifiTmp;
	private Integer signallevel; // 信号强弱
	private Integer isHavePwd = 0; // 是否有密码 0：无 1：有 
	
	public String getWifiName() {
		return wifiName;
	}
	public void setWifiName(String wifiName) {
		this.wifiName = wifiName;
	}
	public String getWifiTmp() {
		return wifiTmp;
	}
	public void setWifiTmp(String wifiTmp) {
		this.wifiTmp = wifiTmp;
	}
	public Integer getSignallevel() {
		return signallevel;
	}
	public void setSignallevel(Integer signallevel) {
		this.signallevel = signallevel;
	}
}
