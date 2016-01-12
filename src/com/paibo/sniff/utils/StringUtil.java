package com.paibo.sniff.utils;

import android.text.TextUtils;

/**
 * 字符串工具类
 * 
 * @author jiangbing
 *
 */
public class StringUtil {

	/** 过滤BSSID */
	public static String filterBSSID(String BSSID) {
		if (!TextUtils.isEmpty(BSSID)) {
			return BSSID.replaceAll(":", "");
		}
		return null;
	}
}
