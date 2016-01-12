package com.paibo.sniff.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.view.animation.AnimationSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import com.paibo.sniff.R;

/**
 * <p>仿照雷达辐射控件</p>
 * 
 * @author jiangbing
 *
 */
public class RippleBackground extends RelativeLayout {

    private static final int DEFAULT_RIPPLE_COUNT = 6;
    private static final int DEFAULT_DURATION_TIME = 3000;
    private static final float DEFAULT_SCALE = 6.0f;
    private static final int DEFAULT_FILL_TYPE = 0;

    private int rippleColor;
    private float rippleStrokeWidth;
    private float rippleRadius;
    private int rippleDurationTime;
    private int rippleAmount;
    private int rippleDelay;
    private float rippleScale;
    
    private int rippleType;
    private Paint paint;
    private boolean animationRunning = false;
    private AnimatorSet animatorSet;
    private ArrayList<Animator> animatorList;
    private LayoutParams rippleParams;
    private ArrayList<RippleView> rippleViewList = new ArrayList<RippleView>();
    
    public RippleBackground(Context context) {
        super(context);
    }

    public RippleBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        if (isInEditMode())
            return;

        if (null == attrs) {
            throw new IllegalArgumentException("Attributes should be provided to this view,");
        }

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleBackground);
        rippleColor=typedArray.getColor(R.styleable.RippleBackground_rb_color, getResources().getColor(R.color.ripple_color));
        rippleStrokeWidth=typedArray.getDimension(R.styleable.RippleBackground_rb_strokeWidth, getResources().getDimension(R.dimen.rippleStrokeWidth));
        rippleRadius=typedArray.getDimension(R.styleable.RippleBackground_rb_radius,getResources().getDimension(R.dimen.rippleRadius));
        rippleDurationTime=typedArray.getInt(R.styleable.RippleBackground_rb_duration,DEFAULT_DURATION_TIME);
        rippleAmount=typedArray.getInt(R.styleable.RippleBackground_rb_rippleAmount,DEFAULT_RIPPLE_COUNT);
        rippleScale=typedArray.getFloat(R.styleable.RippleBackground_rb_scale,DEFAULT_SCALE);
        rippleType=typedArray.getInt(R.styleable.RippleBackground_rb_type,DEFAULT_FILL_TYPE);
        typedArray.recycle();

//        rippleDelay = 1000;//rippleDurationTime / rippleAmount;
        rippleDelay = rippleDurationTime / rippleAmount;

        paint = new Paint();
        paint.setAntiAlias(true);
        if (rippleType==DEFAULT_FILL_TYPE) {
            rippleStrokeWidth=0;
            paint.setStyle(Paint.Style.FILL);
        } else
            paint.setStyle(Paint.Style.STROKE);
        paint.setColor(rippleColor);

        rippleParams = new LayoutParams(
        		(int) (2 * (rippleRadius+rippleStrokeWidth)),
        		(int) (2 * (rippleRadius+rippleStrokeWidth)));
        rippleParams.addRule(CENTER_IN_PARENT, TRUE);

        animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorList = new ArrayList<Animator>();

        for (int i = 0; i <= rippleAmount; i++) {
//        	if (i >= rippleAmount) {
//        		RippleView2 tmp = new RippleView2(getContext());
//        		addView(tmp, rippleParams);
//                rippleViewList.add(tmp);
//                ObjectAnimator scaleXAnimator1 = ObjectAnimator.ofFloat(tmp, "ScaleX", 1.0f, rippleScale);
//                scaleXAnimator1.setRepeatCount(ObjectAnimator.INFINITE);
//                scaleXAnimator1.setRepeatMode(ObjectAnimator.RESTART);
//                scaleXAnimator1.setStartDelay(i * rippleDelay + 5000);
//                scaleXAnimator1.setDuration(rippleDurationTime);
//                animatorList.add(scaleXAnimator1);
//                ObjectAnimator scaleYAnimator1 = ObjectAnimator.ofFloat(tmp, "ScaleY", 1.0f, rippleScale);
//                scaleYAnimator1.setRepeatCount(ObjectAnimator.INFINITE);
//                scaleYAnimator1.setRepeatMode(ObjectAnimator.RESTART);
//                scaleYAnimator1.setStartDelay(i * rippleDelay+ 5000);
//                scaleYAnimator1.setDuration(rippleDurationTime);
//                animatorList.add(scaleYAnimator1);
//                ObjectAnimator alphaAnimator1 = ObjectAnimator.ofFloat(tmp, "Alpha", 1f, 0);
//                alphaAnimator1.setRepeatCount(ObjectAnimator.INFINITE);
//                alphaAnimator1.setRepeatMode(ObjectAnimator.RESTART);
//                alphaAnimator1.setStartDelay(i * rippleDelay+ 5000);
//                alphaAnimator1.setDuration(rippleDurationTime);
//                animatorList.add(alphaAnimator1);
//                continue;
//        	}
        	
            RippleView rippleView = new RippleView(getContext());
            addView(rippleView, rippleParams);
            rippleViewList.add(rippleView);
//            Animator at = AnimatorInflater.loadAnimator(ctx, R.anim.objanimator);
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale);
            scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            scaleXAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleXAnimator.setStartDelay(i * rippleDelay);
            scaleXAnimator.setDuration(rippleDurationTime);
            animatorList.add(scaleXAnimator);
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale);
            scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            scaleYAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleYAnimator.setStartDelay(i * rippleDelay);
            scaleYAnimator.setDuration(rippleDurationTime);
            animatorList.add(scaleYAnimator);
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 0.5f, 0f);
            alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
            alphaAnimator.setStartDelay(i * rippleDelay);
            alphaAnimator.setDuration(rippleDurationTime);
            animatorList.add(alphaAnimator);
            

//            ObjectAnimator scaleXAnimator2 = ObjectAnimator.ofFloat(rippleView, "ScaleX", 0f, rippleScale);
//            scaleXAnimator2.setRepeatCount(ObjectAnimator.INFINITE);
//            scaleXAnimator2.setRepeatMode(ObjectAnimator.RESTART);
//            scaleXAnimator2.setStartDelay(i * rippleDelay+4000);
//            scaleXAnimator2.setDuration(8000);
//            animatorList.add(scaleXAnimator2);
//
//            ObjectAnimator scaleYAnimator2 = ObjectAnimator.ofFloat(rippleView, "ScaleY", 0f, rippleScale);
//            scaleYAnimator2.setRepeatCount(ObjectAnimator.INFINITE);
//            scaleYAnimator2.setRepeatMode(ObjectAnimator.RESTART);
//            scaleYAnimator2.setStartDelay(i * rippleDelay+4000);
//            scaleYAnimator2.setDuration(8000);
//            animatorList.add(scaleYAnimator2);
            
        }
//        animatorSet.setStartDelay(2000);
        animatorSet.playTogether(animatorList);
//        animatorSet.sets
//        for (int i = 0; i < animatorList.size(); i++) {
//        	animatorSet.addAnimation(animatorList.get(i));
//        }
        
    }

    private class RippleView extends View {

        public RippleView(Context context) {
            super(context);
            this.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int radius = (Math.min(getWidth(), getHeight())) / 2;
            canvas.drawCircle(radius, radius, radius - rippleStrokeWidth, paint);
        }
    }
    
    private class RippleView2 extends RippleView {

        public RippleView2(Context context) {
            super(context);
            this.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int radius=(Math.min(getWidth(), getHeight())) / 2;
            Paint paint = new Paint();
            paint.setAntiAlias(true);
//            if (rippleType == DEFAULT_FILL_TYPE) {
//                rippleStrokeWidth = 0;
//                paint.setStyle(Paint.Style.FILL);
//            }else
//                paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getResources().getColor(R.color.main_color));
            paint.setAlpha(255);
            canvas.drawCircle(radius, radius, radius - rippleStrokeWidth, paint);
        }
    }

    public void startRippleAnimation(){
        if (!isRippleAnimationRunning()) {
        	
            for (RippleView rippleView : rippleViewList) {
                rippleView.setVisibility(VISIBLE);
            }
            animatorSet.start();
            animationRunning = true;
        }
    }

    public void stopRippleAnimation() {
        if (isRippleAnimationRunning()) {
//            animatorSet.end();
            animatorSet.cancel();
            animationRunning = false;
        }
    }

    public boolean isRippleAnimationRunning() {
        return animationRunning;
    }
}
