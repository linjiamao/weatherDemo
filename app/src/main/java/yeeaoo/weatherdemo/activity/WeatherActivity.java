package yeeaoo.weatherdemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import yeeaoo.weatherdemo.R;
import yeeaoo.weatherdemo.model.City;
import yeeaoo.weatherdemo.service.AutoUpdateService;
import yeeaoo.weatherdemo.util.HttpCallBackListener;
import yeeaoo.weatherdemo.util.HttpUtil;
import yeeaoo.weatherdemo.util.Utility;

public class WeatherActivity extends BaseActivity implements View.OnClickListener{
    private City city;
    private String cityName;
    /**
     * 显示标题
     */
    private TextView titleText;
    /**
     * 显示年月
     */
    private TextView time_text;
    /**
     * 当前天气状况
     */
    private TextView weather_text;
    /**
     * 当前天气的图片
     */
    private ImageView weather_image;
    /**
     * 当前温度
     */
    private TextView temperature_text;
    /**
     * 未来两天的天气
     */
    private TextView weather1,weather2;
    /**
     * 未来两天的最低温度和最高温度
     */
    private TextView temperature1,temperature2;
    /**
     * 未来两天的天气图片
     */
    private ImageView image1,image2;
    /**
     * 更新时间
     */
    private TextView update_time;
    /**
     * 切换城市
     */
    private Button switch_city;

    /**
     *刷新天气
     */
    private Button refresh_weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        city = getIntent().getParcelableExtra("city");
        cityName = city.getCityName();
        initViews();
        queryWeather();
        queryWeatherFuture();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        titleText = (TextView) findViewById(R.id.title_text);
        time_text = (TextView) findViewById(R.id.time_text);
        weather_text = (TextView) findViewById(R.id.weather_text);
        temperature_text = (TextView) findViewById(R.id.temperature_text);
        weather1 = (TextView) findViewById(R.id.weather1);
        weather2 = (TextView) findViewById(R.id.weather2);
        temperature1 = (TextView) findViewById(R.id.temperature1);
        temperature2 = (TextView) findViewById(R.id.temperature2);
        update_time = (TextView) findViewById(R.id.update_time);
        weather_image = (ImageView) findViewById(R.id.weather_image);
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        switch_city = (Button) findViewById(R.id.switch_city);
        refresh_weather = (Button) findViewById(R.id.refresh_weather);
        switch_city.setVisibility(View.VISIBLE);
        refresh_weather.setVisibility(View.VISIBLE);
        switch_city.setOnClickListener(this);
        refresh_weather.setOnClickListener(this);
    }

    /**
     * 查询未来几天天气
     */
    private void queryWeatherFuture() {
        String city = null;
        try {
            city = URLEncoder.encode(cityName,"UTF-8");
            String other = "&language=zh-Hans&unit=c&start=0&days=5";
            String address = "https://api.thinkpage.cn/v3/weather/daily.json?key=9nlflw6lyxl2ta03&location="+city+other ;
            queryWeatherFutureFromServer(address);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询当前天气
     */
    private void queryWeather() {
        try {
            String city = URLEncoder.encode(cityName,"UTF-8");
            String other = "&language=zh-Hans&unit=c";
            String address = "https://api.thinkpage.cn/v3/weather/now.json?key=9nlflw6lyxl2ta03&" +
                    "location="+city+other ;
            queryFromServer(address);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * 根据地址去查询天气情况
     * @param address
     */
    private void queryFromServer(String address) {
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Log.d("weatherData",response);
                Utility.handleWeatherResponse(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("weatherData",e.toString());
            }
        });
    }

    /**
     * 根据地址查询未来天气
     * @param address
     */
    private void queryWeatherFutureFromServer(String address) {
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Log.d("weatherData",response);
                Utility.handleFutureWeatherResponse(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showFutureWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("weatherData",e.toString());
            }
        });
    }

    /**
     * 从SharedPreference中读取数据用来
     * 显示当天天气的详情
     */
    private void showWeather() {
        try {
            SharedPreferences sdf = PreferenceManager.getDefaultSharedPreferences(this);
            titleText.setText(sdf.getString("city_name",""));
            time_text.setText(sdf.getString("current_date",""));
            weather_text.setText(sdf.getString("weather",""));
            temperature_text.setText(sdf.getString("temperature","")+"°");
            weather_image.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("big/"+sdf.getString("code","")+".png")));
            String time = sdf.getString("last_update","");
            String updateTime = time.substring(11,19);
            update_time.setText("今天"+updateTime+"发布");
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从SharedPreference中读取数据用来
     * 显示未来几天天气的详情
     */
    private void showFutureWeather() {
        try {
            SharedPreferences sdf = PreferenceManager.getDefaultSharedPreferences(this);
            String code_day1 = sdf.getString("code_day1","");
            String code_day2 = sdf.getString("code_day2","");
            image1.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("small/"+code_day1+".png")));
            image2.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("small/"+code_day2+".png")));
            String text_day1 = sdf.getString("text_day1","");
            String text_day2 = sdf.getString("text_day2","");
            weather1.setText(text_day1);
            weather2.setText(text_day2);
            String low1 = sdf.getString("low1","");
            String high1 = sdf.getString("high1","");
            temperature1.setText(low1+"°/"+high1+"°");
            String low2 = sdf.getString("low2","");
            String high2 = sdf.getString("high2","");
            temperature2.setText(low2+"°/"+high2+"°");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                Intent intent = new Intent();
                intent.setClass(this,ChooseAreaActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                update_time.setText("同步中...");
                queryWeather();
                queryWeatherFuture();
                break;
        }
    }
}
