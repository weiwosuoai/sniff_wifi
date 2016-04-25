package com.paibo.sniff.actionbartest;

import java.util.ArrayList;
import java.util.List;

import com.paibo.sniff.R;
import com.paibo.sniff.adapter.WifiInfoAdapter;
import com.paibo.sniff.bean.Wifi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class WifiInfoActivity extends Activity {
	
	private LinearLayout mReturn;
	private ListView mListView;
	private List<Wifi> mData;
	private BaseAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_info);
		
		initView();
		initListener();
		initData();
		
	}
	
	private void initView() {
		mReturn = (LinearLayout) findViewById(R.id.activity_return);
		mListView = (ListView) findViewById(R.id.lv_wifi_info);
	}
	
	private void initData() {
		getWifiInfo();
		adapter = new WifiInfoAdapter(this, mData);
		mListView.setAdapter(adapter);
	}
	
	private void getWifiInfo() {
		mData = new ArrayList<Wifi>();
		
		for (int i = 0; i < 10; i++) {
			Wifi wifi = new Wifi();
			wifi.setWifiName("hintsoft_guest");
			wifi.setWifiTmp("aa:bb:cc:dd");
			wifi.setSignallevel(1);
			mData.add(wifi);
		}
	}
	
	private void initListener() {
		
		// ·µ»Ø
		mReturn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}
