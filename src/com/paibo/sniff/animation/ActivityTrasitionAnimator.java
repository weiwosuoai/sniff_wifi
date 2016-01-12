package com.paibo.sniff.animation;



import com.paibo.sniff.R;

import android.content.Context;
import android.app.Activity;

/**
 * activity过渡动画管理者
 * 
 * @author jiangbing
 *
 */
public class ActivityTrasitionAnimator {
	
	public enum Mode {
		RIGHT2LEFT, LEFT2RIGHT, BOTTOM2TOP
	}

	/**
	 * @param context context
	 * @param mode    activity间转换的过渡动画类型<br/>  
	 * 									RIGHT2LEFT : 渐入动画：从右边过渡到左边<br/>
	 * 									LEFT2RIGHT : 渐出动画：从左边过渡到右边，用作返回用<br/>
	 * 									BOTTOM2TOP : 渐入动画：从下面过渡到上面<br/>
	 */
	public static void setAnim(Context context, Mode mode) {
		Activity activity = (Activity) context;
		switch (mode) {
		case RIGHT2LEFT: // 渐入动画：从右边过渡到左边
			activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
			break;
		
		case LEFT2RIGHT: // 渐出动画：从左边过渡到右边，用作返回用
			activity.overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
			break;
		case BOTTOM2TOP: // 渐入动画：从下面过渡到上面
			activity.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_from_top);
			break;
		}
	}
}
