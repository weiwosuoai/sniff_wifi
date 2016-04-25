package com.paibo.sniff.ui.activity;

import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paibo.sniff.R;
import com.paibo.sniff.animation.ActivityTrasitionAnimator;
import com.paibo.sniff.animation.ActivityTrasitionAnimator.Mode;
import com.paibo.sniff.base.ActivityCollector;
import com.paibo.sniff.base.BaseActivity;
import com.paibo.sniff.bean.Location;
import com.paibo.sniff.bean.SniffWifi;
import com.paibo.sniff.location.LocationManager;
import com.paibo.sniff.location.LocationManager.LocationListener;
import com.paibo.sniff.ui.fragment.FailFragment;
import com.paibo.sniff.ui.fragment.FailFragment.FailFragmentCallback;
import com.paibo.sniff.ui.fragment.RadarFragment;
import com.paibo.sniff.ui.fragment.RadarFragment.RadarFragmentCallback;
import com.paibo.sniff.ui.fragment.SuccessFragment;
import com.paibo.sniff.ui.fragment.SuccessFragment.SuccessFragmentCallback;
import com.paibo.sniff.wifi.SniffWifiManager;
import com.paibo.sniff.wifi.SniffWifiManager.SniffWifiListener;

public class MainActivity extends BaseActivity implements RadarFragmentCallback, 
				FailFragmentCallback, SuccessFragmentCallback, LocationListener, SniffWifiListener {
	
	/** 拿到解析的信息后，显示捕获成功的fragment */
	public final static int DISPLAY_SUCCESS_FRAGEMENT = 9;
	
	private ImageView mQuitIV;
	
	private ImageView mStartCollect;
	
	private LinearLayout mAreaStartCollect, mAreaInfoArea, mItemWifi;
	
	private ImageView mLoadingIV, mItemWifiIconIV;
	
	private TextView mSniffWiffNumTV, mLongitudeTV, mLatitudeTV, mAddressTV, mAddressDesTV;
	
	private FrameLayout mFragmentContainerFL;
	
	public static List<SniffWifi> mSniffWifiList;
	
	// Handler
	private Handler handler  = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			case SniffWifiManager.SNIFF_FAIL:
				String errorMsg = (String) msg.obj;
				// 显示捕获失败的fragment
				showCapturerFailFragment(errorMsg);
				stopItemLoadingAniamtion();
				break;

			case SniffWifiManager.SNIFF_SUCCESS:
				mSniffWifiList = SniffWifiManager
										.getInstance(MainActivity.this)
										.getSniffWifiInfos();
				
				// 捕获成功
				capturerSuccess();
				break;
				
			case DISPLAY_SUCCESS_FRAGEMENT:
				// 显示捕获成功的fragment
				showCapturerSuccessFragment();
				stopItemLoadingAniamtion();
				break;
			}
		}
	};;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();
		initData();
        initListener();
        initFragment(savedInstanceState);
	}
	
	// TODO 这里需要对radarFragment做个优化，防止第二次显示时不必要的创建
	// TODO 同理，successFragment and failFragment also need to performance optimization
	// 后续在做调整
	
	/**
	 * Init the fragment
	 */
	private void initFragment(Bundle savedInstanceState) {
		
		// Check that the activity is using the layout version with
		// the fragment_container FragmentLayout
		if (mFragmentContainerFL != null) {
			
			// However, if we're being restored from previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}
			
			// Create a Radar Fragment to be placed in the activity layout
			RadarFragment radarFragment = new RadarFragment();
			
			// In case this activity was started with special instructions from an
			// Intent, pass the Intent's extras to the fragment as arguments
			radarFragment.setArguments(getIntent().getExtras());
			
			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager().beginTransaction()
				.add(R.id.fragement_container, radarFragment).commit();
			
		}
	}

	private void initView() {
        mStartCollect = (ImageView) findViewById(R.id.btn_start_collect);
        mQuitIV = (ImageView) findViewById(R.id.iv_quit);
        mAreaStartCollect = (LinearLayout) findViewById(R.id.start_collect_area);
        mAreaInfoArea = (LinearLayout) findViewById(R.id.collect_info_area);
        mItemWifi = (LinearLayout) findViewById(R.id.item_wifi_num);
        mLoadingIV = (ImageView) findViewById(R.id.loading1);
        mItemWifiIconIV = (ImageView) findViewById(R.id.item_wifi_icon);
        mSniffWiffNumTV = (TextView) findViewById(R.id.wifi_num);
        mLongitudeTV = (TextView) findViewById(R.id.tv_longitude);
        mLatitudeTV = (TextView) findViewById(R.id.tv_latitude);
        mAddressTV = (TextView) findViewById(R.id.tv_address);
        mAddressDesTV = (TextView) findViewById(R.id.tv_address_des);
        // Fragment container
        mFragmentContainerFL = (FrameLayout) findViewById(R.id.fragement_container);
	}
	
	/**
	 * Init some data here.
	 */
	private void initData() {
	}
	
	private void initListener() {
		// 开始采集
		mStartCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	// 交叉淡入淡出动画
            	crossfade();
            	
            	RadarFragment fragment = getRadarFragment();
            	// Start the animation of radarFragment
            	fragment.startAnimation();
            	refreshUI();
                
                // 开始捕获数据
            	startCapture();
            }
        });
		
		// 退出
		mQuitIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ActivityCollector.finishAll();
			}
		});
		
		// 点击item wifi
		mItemWifi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mSniffWifiList == null || mSniffWifiList.size() == 0) 
					return;
				Intent intent = new Intent(MainActivity.this, WifiInfoActivity.class);
				startActivity(intent);
				// 过渡动画
				ActivityTrasitionAnimator.setAnim(MainActivity.this, Mode.RIGHT2LEFT);
			}
		});
	}
	
	// 定位管理者
	private LocationManager mLocManager;
	
	/**
	 * 开始捕获
	 */
	private void startCapture() {
		// 开始获取位置信息
		mLocManager = LocationManager.getInstance(this);
		mLocManager.start();

		mSniffWifiList = null;
		// 捕获wifi信息
		SniffWifiManager manager = SniffWifiManager.getInstance(MainActivity.this);
		manager.startSniffWifi();
	}
	
	/**
	 * 显示捕获成功的fragment
	 */
	private void showCapturerSuccessFragment() {
		SuccessFragment successFragment = new SuccessFragment();
		Bundle args = new Bundle();
		args.putString(SuccessFragment.SUCCESS_MSG, "探测成功");
		successFragment.setArguments(args);
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragement_container, successFragment).commit();
	}
	
	/**
	 * 显示捕获失败的fragment
	 */
	private void showCapturerFailFragment(String failMsg) {
		// Create a failFragment and give it an argument specifying the article it should show
		FailFragment failFragment = new FailFragment();
		Bundle args = new Bundle();
		args.putString(FailFragment.FAIL_MSG, failMsg);
		failFragment.setArguments(args);
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		
		// Replace whatever is in the fragment_container view with this fragment,
		// and here don't need to add the transaction to the back stack 
		// so the user can't navigate back
		transaction.replace(R.id.fragement_container, failFragment);
		
		// Commit the transaction
		transaction.commit();
	}
	
	// 标识是否第一次显示雷达fragment
	private boolean isFirstShowRadarFragment = true;
	
	/**
	 * 
	 */
	private void showRadarFragmentAndStartCapturer() {
		RadarFragment radarFragment = new RadarFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		
		// Replace whatever is in the fragment_container view with this fragment,
		// and here don't need to add the transaction to the back stack 
		// so the user can't navigate back
		transaction.replace(R.id.fragement_container, radarFragment);
		
		// Commit the transaction
		transaction.commit();
		isFirstShowRadarFragment = false;
	}
	
	// 淡入淡出动画持续时间
	private int mAnimationDuration;
	
	/**
	 * 交叉淡入淡出动画
	 */
	private void crossfade() {
		
		// Use system's default "short" animation time.
		// 淡入淡出动画持续时间
		mAnimationDuration = getResources().getInteger(
		                android.R.integer.config_shortAnimTime);
		
		mAreaStartCollect.animate()
					     .alpha(0.5f)
						 .setDuration(mAnimationDuration)
						 .setListener(new AnimatorListenerAdapter() {
							 @Override
							public void onAnimationEnd(Animator animation) {
								 mAreaStartCollect.setVisibility(View.GONE);
								 
								 mAreaInfoArea.setAlpha(0f);
								 mAreaInfoArea.setVisibility(View.VISIBLE);
								 mAreaInfoArea.animate()
											  .alpha(1f)
											  .setDuration(mAnimationDuration)
											  .setListener(null);
							}
						 });
	}

	/**
	 * 雷达探测成功
	 */
	private void capturerSuccess() {
		// 显示wifi和手机图片
		RadarFragment fragment = getRadarFragment();
    	fragment.displayWifiAndPhoneIcons();
    	// Refresh the UI Of this activity
    	refreshUI();
    	
    	Message msg = Message.obtain();
    	msg.what = DISPLAY_SUCCESS_FRAGEMENT;
    	handler.sendMessageDelayed(msg, 2000); // delay 2m
	}
	
	/**
	 * Get the radar fragment
	 * @return
	 */
	private RadarFragment getRadarFragment() {
		return (RadarFragment) getSupportFragmentManager().findFragmentById(R.id.fragement_container);
	}
	
	/**
	 * 更新主界面ui,主要是loading图片的停止与否
	 */
	private void refreshUI() {
		// 这里可能报转换异常，谨慎使用此方法
		RadarFragment fragment = getRadarFragment();
		if (fragment.isPlaying()) { // If the animation of radarFragment is playing
			startItemLoadingAniamtion();
			// 设置界面上wifi的采集数
			mSniffWiffNumTV.setText("正在采集...");
		} else {
			stopItemLoadingAniamtion();
			// 设置界面上wifi的采集数
			mSniffWiffNumTV.setText(mSniffWifiList.size() + "");
		}
	}
	
	/**
	 * 开始item中loading的图标动画
	 */
	private void startItemLoadingAniamtion() {
		mItemWifiIconIV.setVisibility(View.GONE);
    	mLoadingIV.setVisibility(View.VISIBLE);
		// 旋转loading图片
    	Animation anim = AnimationUtils.loadAnimation(
    				MainActivity.this, R.anim.rotate_animation);
    	mLoadingIV.startAnimation(anim);
	}
	
	/**
	 * 停止item中loading的图标动画
	 */
	private void stopItemLoadingAniamtion() {
		// 停止旋转的加载动画
		mLoadingIV.clearAnimation();
    	mLoadingIV.setVisibility(View.GONE);
    	mItemWifiIconIV.setVisibility(View.VISIBLE);
    	
    	// 设置界面上wifi的采集数
    	if (mSniffWifiList == null || mSniffWifiList.size() == 0) {
    		mSniffWiffNumTV.setText("0");
    	} else {
    		mSniffWiffNumTV.setText(mSniffWifiList.size() + "");
    	}
	}
	
	/**
	 * 当用户点击雷达中心停止捕获时
	 */
	@Override
	public void onStopAnimation() {
		refreshUI();
	}

	/**
	 * 当用户点击雷达中心开始捕获时
	 */
	@Override
	public void onStartAnimation() {
		refreshUI();
	}

	/**
	 * 失败后重新检测
	 */
	@Override
	public void onFailCapturerAgain() {
		showRadarFragmentAndStartCapturer();
	}

	/**
	 * 雷达fragement显示后
	 */
	@Override
	public void onShowed() {
		if (!isFirstShowRadarFragment) {
			RadarFragment fragment = getRadarFragment();
	    	// Start the animation of radarFragment
	    	fragment.startAnimation();
	    	refreshUI();
	    	startCapture();
		}
	}
	
	/**
	 * 更新位置信息视图
	 * @param location
	 */
	private void refreshLocationUI(Location location) {
		mAddressTV.setText(location.getAddress());
		String addDes = location.getAddressDes();
		mAddressDesTV.setText(TextUtils.isEmpty(addDes) ? "" : "(" + location.getAddressDes() + ")");
		mLongitudeTV.setText(location.getLongitude() + "");
		mLatitudeTV.setText(location.getLatitude() + "");
	}

	/**
	 * 成功后再次检测
	 */
	@Override
	public void onSuccessCapturerAgain() {
		showRadarFragmentAndStartCapturer();
	}

	/**
	 * 获取位置信息回调
	 */
	@Override
	public void onReceiveLocation(Location locationInfo) {
		refreshLocationUI(locationInfo);
		mLocManager.stop();
	}

	/**
	 * SniffManager捕获成功后回调
	 */
	@Override
	public void onSniffSuccess() {
		Message message = Message.obtain();
		message.what = SniffWifiManager.SNIFF_SUCCESS;
		handler.sendMessage(message);
	}

	/**
	 * SniffManager捕获失败后回调
	 */
	@Override
	public void onSniffFail(String failStr) {
		Message message = Message.obtain();
		message.what = SniffWifiManager.SNIFF_FAIL;
		message.obj = failStr;
		handler.sendMessage(message);
	}
	
}
