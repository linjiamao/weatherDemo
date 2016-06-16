package yeeaoo.weatherdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import yeeaoo.weatherdemo.activity.BaseActivity;
import yeeaoo.weatherdemo.activity.ChooseAreaActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, ChooseAreaActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}
