package com.paibo.sniff.ui.fragment;

import java.util.ArrayList;

import com.paibo.sniff.R;
import com.paibo.sniff.ui.activity.MainActivity;
import com.paibo.sniff.view.RippleBackground;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable.Callback;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 雷达fragment
 * 
 * @author jiangbing
 *
 */
public class RadarFragment extends Fragment {
	
	public interface RadarFragmentCallback {
		// 视图显示后回调
		void onShowed();
		// 停止辐射动画时，回调
		void onStopAnimation();
		// 开始辐射动画时，回调
		void onStartAnimation();
	}
	
	private ImageView mRadarCenterPointIV;
	
	private RippleBackground mRippleBackground;
	
	private View view;
	
	private RelativeLayout mRadarRL;
	
	private Context context;
	
	// 回调对象
	private RadarFragmentCallback mCallback;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		try {
			mCallback = (RadarFragmentCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement RadarFragmentCallback.");
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for the fregment
		view = inflater.inflate(R.layout.fragment_radar, container, false);
		initView();
		initListener();
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mCallback.onShowed();
	}
	
	private void initView() {
		mRadarCenterPointIV = (ImageView) view.findViewById(R.id.iv_centerImage2);
		mRippleBackground = (RippleBackground) view.findViewById(R.id.content);
		mRadarRL = (RelativeLayout) view.findViewById(R.id.rl_tmp);
		foundDevice = (ImageView) view.findViewById(R.id.foundDevice);
        foundDevice2 = (ImageView) view.findViewById(R.id.foundDevice2);
        foundWifi = (ImageView) view.findViewById(R.id.foundWifi);
        foundWifi2 = (ImageView) view.findViewById(R.id.foundWifi2);
	}
	
	private void initListener() {
		// 点击雷达中心，开始采集及停止采集
		mRadarRL.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isPlaying()) { // 若正在播放则停止动画
					stopAnimation();
			        mCallback.onStopAnimation();
				} else { // 若停止，则播放
					startAnimation();
					mCallback.onStartAnimation();
				}
			}
		});
	}
	
	/**
	 * Start the animation of radar.
	 */
	public void startAnimation() {
		Animation mAnim = AnimationUtils.loadAnimation(
    			context, R.anim.rotate_animation2);
		// 开始旋转中间图片
		mRadarCenterPointIV.startAnimation(mAnim);
    	// 开始辐射波纹
		mRippleBackground.startRippleAnimation();
	}
	
	/**
	 * Stop the animation of radar.
	 */
	public void stopAnimation() {
		if (isPlaying()) { // 若正在播放则停止动画
			mRippleBackground.stopRippleAnimation();
			
			// 消除屏幕上的手机，WiFi图标
			foundDevice.setVisibility(View.GONE);
	        foundDevice2.setVisibility(View.GONE);
	        foundWifi.setVisibility(View.GONE);
	        foundWifi2.setVisibility(View.GONE);
	        mRadarCenterPointIV.clearAnimation();
		}
	}
	
	/* wifi和手机图标 */
	private ImageView foundDevice, foundDevice2, foundWifi, foundWifi2;
	
	/**
	 * 探测成功,动画显示wifi和手机icon
	 */
	public void displayWifiAndPhoneIcons() {
		// 判断雷达动画是否正在播放,不在播放，则退出
		if (!isPlaying()) 
			return; 
		
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        
        ObjectAnimator scaleXAnimator2 = ObjectAnimator.ofFloat(foundDevice2, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator2);
        ObjectAnimator scaleYAnimator2 = ObjectAnimator.ofFloat(foundDevice2, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator2);
        
        ObjectAnimator scaleXWifi = ObjectAnimator.ofFloat(foundWifi, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXWifi);
        ObjectAnimator scaleYWifi = ObjectAnimator.ofFloat(foundWifi, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYWifi);
        
        
        ObjectAnimator scaleXWifi2 = ObjectAnimator.ofFloat(foundWifi2, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXWifi2);
        ObjectAnimator scaleYWifi2 = ObjectAnimator.ofFloat(foundWifi2, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYWifi2);
        
        animatorSet.playTogether(animatorList);
        foundDevice.setVisibility(View.VISIBLE);
        foundDevice2.setVisibility(View.VISIBLE);
        foundWifi.setVisibility(View.VISIBLE);
        foundWifi2.setVisibility(View.VISIBLE);
        animatorSet.start();
	}
	
	/**
	 * 雷达动画是否正在播放
	 * @return
	 */
	public boolean isPlaying() {
		return mRippleBackground.isRippleAnimationRunning();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		stopAnimation();
	}
	
}
