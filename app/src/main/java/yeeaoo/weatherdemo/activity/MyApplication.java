package yeeaoo.weatherdemo.activity;

import android.app.Application;
import android.content.Context;

/**
 * Created by yo on 2016/6/16.
 */
public class MyApplication extends Application{
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
