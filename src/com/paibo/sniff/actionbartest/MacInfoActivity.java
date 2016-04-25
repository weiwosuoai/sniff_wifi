package com.paibo.sniff.actionbartest;

import java.util.ArrayList;
import java.util.List;

import com.paibo.sniff.R;
import com.paibo.sniff.adapter.MacInfoAdapter;
import com.paibo.sniff.adapter.WifiInfoAdapter;
import com.paibo.sniff.bean.Mac;
import com.paibo.sniff.bean.Wifi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MacInfoActivity extends Activity {

	private LinearLayout mReturn;
	private ListView mListView;
	private List<Mac> mData;
	private BaseAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mac_info);
		
		initView();
		initListener();
		initData();
		
	}
	
	private void initView() {
		mReturn = (LinearLayout) findViewById(R.id.activity_return);
		mListView = (ListView) findViewById(R.id.lv_mac_info);
	}
	
	private void initData() {
		getWifiInfo();
		adapter = new MacInfoAdapter(this, mData);
		mListView.setAdapter(adapter);
	}
	
	private void getWifiInfo() {
		mData = new ArrayList<Mac>();
		
		for (int i = 0; i < 10; i++) {
			Mac mac = new Mac();
			mac.setPhoneNum("13546567895");
			mac.setMac("aa:bb:cc:dd");
			mac.setSsid("hintsoft_guest");
			mac.setLevel(1);
			mData.add(mac);
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
