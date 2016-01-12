package com.paibo.sniff.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.paibo.sniff.utils.LogUtil;

/**
 * 基activity，以后创建的每个activity需均继承自此类
 * 
 * @author jiangbing
 */
public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 打印当前类名
        LogUtil.d(getClass().getSimpleName().toString());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
