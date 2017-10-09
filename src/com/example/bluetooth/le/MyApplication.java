package com.example.bluetooth.le;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application{
	public static List<Object> activitys = new ArrayList<Object>();
    private static MyApplication instance;
    //获取单例模式中唯一的MyApplication实例   
   public static MyApplication getInstance() {
        if (instance == null)
            instance = new MyApplication();
        return instance;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (!activitys.contains(activity))
            activitys.add(activity);
    }

    // 遍历所有Activity并finish
    public void destroy() {

            for (Object activity : activitys) {
                ((Activity) activity).finish();
            }
        System.exit(0);
    }
}

