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
 * 探测失败fragment
 * 
 * @author jiangbing
 *
 */
public class FailFragment extends Fragment {
	
	public interface FailFragmentCallback {
		// 用户点击重新检测时回调
		void onFailCapturerAgain();
	}
	
	// 失败时，需要显示的文本
	public static String FAIL_MSG;
	
	private View view;
	
	private TextView mFailMsgTV, mReloadTV;
	
	private Context context;
	
	// 回调对象
	private FailFragmentCallback mCallback;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		try {
			mCallback = (FailFragmentCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FailFragmentCallback.");
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for the fregment
		view = inflater.inflate(R.layout.fragment_fail, container, false);
		initView();
		initListener();
		return view;
	}
	
	private void initView() {
		// activity给什么错误信息，即显示什么错误信息
		Bundle args = getArguments();
		String failMsg = args.getString(FAIL_MSG);
		
		mFailMsgTV = (TextView) view.findViewById(R.id.tv_fail_msg);
		mFailMsgTV.setText(TextUtils.isEmpty(failMsg) ? "" : failMsg);
		
		mReloadTV = (TextView) view.findViewById(R.id.tv_reload);
	}
	
	private void initListener() {
		mReloadTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onFailCapturerAgain();
			}
		});
	}
	

}
