package com.paibo.sniff.base;

import android.app.Application;
import android.content.Context;

/**
 * <p>
 * 此类用来初始化应用中通用的信息
 * </p>
 * 
 * @author jiangbing
 *
 */
public class MyApplication extends Application {

	/** The global context */
	private static Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// TODO
		// you can initialize some global infos here ...
		
		// 1.init context
		mContext = getApplicationContext();
	}

	/**
	 * You can get the context anywhere.
	 * 
	 * @return Context 全局context
	 */
	public static Context getContext() {
		return mContext;
	}
}
