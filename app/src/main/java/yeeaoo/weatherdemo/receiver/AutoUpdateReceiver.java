package yeeaoo.weatherdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import yeeaoo.weatherdemo.service.AutoUpdateService;

/**
 * Created by yo on 2016/6/17.
 */
public class AutoUpdateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i= new Intent(context,AutoUpdateService.class);
        context.startService(i);
    }
}
