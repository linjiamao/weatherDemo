package yeeaoo.weatherdemo.util;

/**
 * Created by yo on 2016/6/16.
 * 回调服务返回结果
 */
public interface HttpCallBackListener {

    void onFinish(String response);
    void onError(Exception e);
}
