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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 探测成功fragment
 * 
 * @author jiangbing
 *
 */
public class SuccessFragment extends Fragment {
	
	public interface SuccessFragmentCallback {
		// 用户点击重新检测时回调
		void onSuccessCapturerAgain();
	}
	
	// 成功时，需要显示的文本
	public static String SUCCESS_MSG;
	
	private View view;
	
	private TextView mFailMsgTV, mReloadTV;
	
	private Context context;
	
	// 回调对象
	private SuccessFragmentCallback mCallback;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		try {
			mCallback = (SuccessFragmentCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SuccessFragmentCallback.");
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for the fregment
		view = inflater.inflate(R.layout.fragment_success, container, false);
		initView();
		initListener();
		return view;
	}
	
	private void initView() {
		// activity给什么错误信息，即显示什么错误信息
		Bundle args = getArguments();
		String successMsg = args.getString(SUCCESS_MSG);
		
		mFailMsgTV = (TextView) view.findViewById(R.id.tv_success_msg);
		mFailMsgTV.setText(TextUtils.isEmpty(successMsg) ? "" : successMsg);
		
		mReloadTV = (TextView) view.findViewById(R.id.tv_reload);
	}
	
	private void initListener() {
		mReloadTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onSuccessCapturerAgain();
			}
		});
	}
	

}
