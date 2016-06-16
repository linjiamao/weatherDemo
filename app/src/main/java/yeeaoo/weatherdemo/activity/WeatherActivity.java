package yeeaoo.weatherdemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import yeeaoo.weatherdemo.R;
import yeeaoo.weatherdemo.model.City;

public class WeatherActivity extends BaseActivity{
    private City city;
    private String cityName;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        city = getIntent().getParcelableExtra("city");
        cityName = city.getCityName();
        initViews();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText(cityName);
    }
}
