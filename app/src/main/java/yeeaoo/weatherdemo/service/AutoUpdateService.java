package yeeaoo.weatherdemo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import yeeaoo.weatherdemo.activity.MyApplication;
import yeeaoo.weatherdemo.receiver.AutoUpdateReceiver;
import yeeaoo.weatherdemo.util.HttpCallBackListener;
import yeeaoo.weatherdemo.util.HttpUtil;
import yeeaoo.weatherdemo.util.Utility;

/**
 * Created by yo on 2016/6/17.
 * 负责后台自动更新天气
 */
public class AutoUpdateService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                String city_name = prefs.getString("city_name","");
                updateWeather(city_name);
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour =8*60*60*1000;//这是8小时的毫秒值
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
        Intent i = new Intent(this,AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气
     */
    private void updateWeather(String city_name) {
        try {
            String cityName = URLEncoder.encode(city_name,"UTF-8");
            String other = "&language=zh-Hans&unit=c";
            String address = "https://api.thinkpage.cn/v3/weather/now.json?key=9nlflw6lyxl2ta03&" +
                    "location="+cityName+other ;
            HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
                @Override
                public void onFinish(String response) {
                    Utility.handleWeatherResponse(response);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    /**
     * 更新未来几天天气
     */
    private void updateFutureWeather(String cityName) {
        String city = null;
        try {
            city = URLEncoder.encode(cityName,"UTF-8");
            String other = "&language=zh-Hans&unit=c&start=0&days=5";
            String address = "https://api.thinkpage.cn/v3/weather/daily.json?key=9nlflw6lyxl2ta03&location="+city+other ;
            HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
                @Override
                public void onFinish(String response) {
                    Utility.handleFutureWeatherResponse(response);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
