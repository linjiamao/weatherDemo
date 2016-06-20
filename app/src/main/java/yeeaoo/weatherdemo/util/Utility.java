package yeeaoo.weatherdemo.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import yeeaoo.weatherdemo.activity.MyApplication;

/**
 * Created by yo on 2016/6/16.
 * 解析返回数据
 */
public class Utility {

    /**
     * 解析当天天气的返回结果
     * @param response
     */
    public static void handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray results = jsonObject.optJSONArray("results");
            for (int i = 0;i<results.length();i++){
                JSONObject obj = results.optJSONObject(i);
                JSONObject location = obj.optJSONObject("location");
                JSONObject now = obj.optJSONObject("now");
                String last_update = obj.optString("last_update");
                String cityName = location.optString("name");
                String weather = now.optString("text");
                String temperature = now.optString("temperature");
                String code = now.optString("code");
                saveWeatherInfo(cityName, weather, temperature,code,last_update);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *  解析未来天气的返回结果并保存到SharePreference
     * @param response
     */
    public static void handleFutureWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray results = jsonObject.optJSONArray("results");
            for(int i =0 ; i< results.length();i++){
                JSONObject obj = results.optJSONObject(i);
                JSONArray daily = obj.optJSONArray("daily");
                for (int j = 1;j<daily.length();j++){
                    JSONObject object = daily.optJSONObject(j);
                    String text_day = object.optString("text_day");
                    String code_day = object.optString("code_day");
                    String low = object.optString("low");
                    String high = object.optString("high");
                    saveWeatherFutureInfo(text_day,code_day,low,high,j);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param cityName 城市名
     * @param weather 天气晴
     * @param temperature 当前温度
     * @param last_update 最后更新时间
     * @param code 图片代号
     */
    private static void saveWeatherInfo(String cityName, String weather, String temperature,String code, String last_update) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
        editor.putString("city_name",cityName);
        editor.putBoolean("city_selected",true);
        editor.putString("weather",weather);
        editor.putString("temperature",temperature);
        editor.putString("code",code);
        editor.putString("last_update",last_update);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();

    }

    /**
     *
     * @param text_day 白天天气
     * @param code_day 代表天气的代号
     * @param low 最低温度
     * @param high 最高温度
     * @param j 未来的第几天
     */
    private static void saveWeatherFutureInfo(String text_day, String code_day, String low, String high, int j) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
        editor.putString("text_day"+j,text_day);
        editor.putString("code_day"+j,code_day);
        editor.putString("low"+j,low);
        editor.putString("high"+j,high);
        editor.commit();
    }
}
