package com.paibo.sniff.base;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * activity管理器,用来快速退出app
 * 
 * @author jiangbing
 */
public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity aty : activities) {
            if (!aty.isFinishing()) {
                aty.finish();
            }
        }
    }
}
