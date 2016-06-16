package yeeaoo.weatherdemo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import yeeaoo.weatherdemo.R;
import yeeaoo.weatherdemo.db.CoolWeatherDB;
import yeeaoo.weatherdemo.model.City;
import yeeaoo.weatherdemo.model.Province;
import yeeaoo.weatherdemo.util.HttpCallBackListener;
import yeeaoo.weatherdemo.util.HttpUtil;

public class ChooseAreaActivity extends BaseActivity {
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private int currentLevel;
    private CoolWeatherDB coolWeatherDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);
        initViews();
    }

    private void initViews() {
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    Province selectProvince = provinceList.get(position);
                    queryCities(selectProvince);
                }else if(currentLevel == LEVEL_CITY){
                    City city = cityList.get(position);
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("city",city);
                    startActivity(intent);
                }
            }


        });
        queryProvinces();//加载省份数据
    }

    /**
     * 根据省份查询城市
     * @param selectProvince
     */
    private void queryCities(Province selectProvince) {
        cityList = coolWeatherDB.loadCities(selectProvince.getProvinceName());
        if(cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            queryCitiesFromServer(selectProvince);
        }
    }

    /**
     * 查询全国所有的省，优先从数据库查询,若果数据库没有，再初始化
     */
    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvince();
        if (provinceList.size() > 0) {
            Log.d("weatherData","size="+provinceList.size());
            dataList.clear();
            for (Province province : provinceList) {
                Log.d("weatherData","name ="+province.getProvinceName());
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryProvincesFromServer();
        }
    }

    /**
     * 添加省份
     */
    private void queryProvincesFromServer() {
        String[] provinceAttr = new String[]{"江苏", "安徽", "浙江", "福建", "上海","山东", "广东", "广西", "海南", "湖北", "湖南", "河南", "江西", "北京",
                "天津", "河北", "山西", "内蒙古", "宁夏", "新疆", "青海", "陕西", "甘肃", "四川", "云南", "贵州", "西藏", "重庆", "辽宁", "吉林", "黑龙江",
                "台湾", "香港", "澳门"};
        provinceList = new ArrayList<>();
        for (int i = 0; i < provinceAttr.length; i++) {
            Province province = new Province();
            province.setProvinceName(provinceAttr[i]);
            provinceList.add(province);
            coolWeatherDB.saveProvince(province);
        }
        queryProvinces();
    }

    /**
     * 添加城市
     * @param selectProvince
     */
    private void queryCitiesFromServer(final Province selectProvince) {
        try {
            String strUTF8 = URLEncoder.encode(selectProvince.getProvinceName(), "UTF-8");
            String address = "https://api.thinkpage.cn/v3/location/search.json?key=9nlflw6lyxl2ta03&q="+strUTF8;
            showProgressDialog();
            HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
                @Override
                public void onFinish(String response) {
                    Log.d("weatherData",response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray resultArr = jsonObject.optJSONArray("results");
                        for (int i = 0;i<resultArr.length();i++){
                            JSONObject obj = resultArr.optJSONObject(i);
                            City city  = new City();
                            city.setCityName(obj.optString("name"));
                            city.setProvince_name(selectProvince.getProvinceName());
                            coolWeatherDB.saveCity(city);
                        }
                        closeProgressDialog();
                        runOnUiThread(new Runnable() {//刷新结果一定要在主线程中执行
                            @Override
                            public void run() {
                                queryCities(selectProvince);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("weatherData",e.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                            }
                        });

                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.d("weatherData",e.toString());
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    closeProgressDialog();
                }
            });
        }
    }

}
