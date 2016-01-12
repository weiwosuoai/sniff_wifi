package com.paibo.sniff.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.paibo.sniff.R;
import com.paibo.sniff.adapter.WifiExpandableListViewAdapter;
import com.paibo.sniff.animation.ActivityTrasitionAnimator;
import com.paibo.sniff.animation.ActivityTrasitionAnimator.Mode;
import com.paibo.sniff.base.BaseActivity;

public class WifiInfoActivity extends BaseActivity {
	
	private ImageView mFinishIV;
	private TextView mTitleTV;
	private ExpandableListView mExpandableListView;
	private WifiExpandableListViewAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_info);
		
		initView();
		initListener();
		initData();
		
	}
	
	private void initView() {
		mFinishIV = (ImageView) findViewById(R.id.iv_finish);
		mTitleTV = (TextView) findViewById(R.id.tv_title);
		mTitleTV.setText("Wifi信息列表");
		mExpandableListView = (ExpandableListView) findViewById(R.id.elv_wifi_info);
	}
	
	private void initData() {
		adapter = new WifiExpandableListViewAdapter(this, MainActivity.mSniffWifiList);
		mExpandableListView.setAdapter(adapter);
	}
	
	
	private void initListener() {
		
		// previous
		mFinishIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				// 过渡动画
				ActivityTrasitionAnimator.setAnim(WifiInfoActivity.this, Mode.LEFT2RIGHT);
			}
		});
	}
	
	
}
