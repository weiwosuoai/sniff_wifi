package com.paibo.sniff.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 网络连接管理者
 * 
 * @author jiangbing
 *
 */
public class HttpManager {
	
	private static HttpManager mHttpManager;
	
	private static final String METHOD = "GET";
	
	// 连接超时时间
	private static final int CONNECT_TIMEOUT = 5000;
	
	// 读取超时时间
	private static final int READ_TIMEOUT = 5000;
	
	/** 回调接口 */
	public interface HttpManagerCallBackListener {
		void onFinish(String response);
		void onError(Exception e);
	}
	
	/** get a httpManager instance */
	public static HttpManager getInstance() {
		if (mHttpManager == null) {
			mHttpManager = new HttpManager();
		}
		return mHttpManager;
	}
	
	public static void sendHttpRequest(final String address, final HttpManagerCallBackListener callback) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod(METHOD);
					connection.setConnectTimeout(CONNECT_TIMEOUT);
					connection.setReadTimeout(READ_TIMEOUT);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					
					// if finished, call back the method called onFinish()
					if (callback != null) {
						callback.onFinish(response.toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
					
					// if a exception occured, call back the method called onError()
					if (callback != null) {
						callback.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
		
	}

}
