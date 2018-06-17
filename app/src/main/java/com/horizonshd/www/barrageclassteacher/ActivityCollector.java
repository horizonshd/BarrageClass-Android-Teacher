/**
 * Activity 管理工具
 */

package com.horizonshd.www.barrageclassteacher;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<>();
    // 添加Activity
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    // 移除Activity
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    // 销毁所有Activity
    public static void finishAll(){
        MyApplication.getSocket().disconnect();//断开与服务器端的socket链接
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

}
