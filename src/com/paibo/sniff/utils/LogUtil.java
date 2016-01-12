package com.paibo.sniff.utils;

import android.util.Log;

/**
 * 日志工具类
 * 
 * <p>主要是对日志的控制，项目上线后，请屏蔽所有日志输出.</p>
 * <p>原因：<br/>
 * 	  1.上线后的项目打印大量日志，影响app运行效率<br/>
 * 	  2.可能泄露机密信息</p>
 * 
 * @author jiangbing
 *
 */
public class LogUtil {
	
	public static final String TAG = "SNIFF_WIFI";
	
	public static final int VERBOSE = 1;
	
	public static final int DEBUG = 2;
	
	public static final int INFO = 3;
	
	public static final int WARN = 4;
	
	public static final int ERROR = 5;
	
	public static final int NOTHING = 6;
	
	/** 这里对日志输出做出控制，项目上线后，请将LEVEL等级设置为NOTHING */
	public static final int LEVEL = VERBOSE;
	
	public static void v(String msg) {
		if (LEVEL <= VERBOSE) {
			Log.v(TAG, msg);
		}
	}
	
	public static void d(String msg) {
		if (LEVEL <= DEBUG) {
			Log.d(TAG, msg);
		}
	}
	
	public static void i(String msg) {
		if (LEVEL <= INFO) {
			Log.i(TAG, msg);
		}
	}
	
	public static void w(String msg) {
		if (LEVEL <= WARN) {
			Log.w(TAG, msg);
		}
	}
	
	public static void e(String msg) {
		if (LEVEL <= ERROR) {
			Log.e(TAG, msg);
		}
	}

}
