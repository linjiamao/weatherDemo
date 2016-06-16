package yeeaoo.weatherdemo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import yeeaoo.weatherdemo.activity.MyApplication;
import yeeaoo.weatherdemo.model.City;
import yeeaoo.weatherdemo.model.Province;

/**
 * Created by yo on 2016/6/16.
 */
public class CoolWeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "cool_weather";
    /**
     * 数据库版本
     */
    public static final int VERSION = 1;
    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     */
    private CoolWeatherDB(){
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(MyApplication.getContext(),DB_NAME,
        null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取CoolWeather的实例
     * @return
     */
    public synchronized static CoolWeatherDB getInstance(){
        if(coolWeatherDB == null){
            coolWeatherDB = new CoolWeatherDB();
        }
        return coolWeatherDB;
    }

    /**
     * 将Province实例保存到数据库
     * @param province
     */
    public void saveProvince(Province province){
        if(province!= null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    public List<Province> loadProvince(){
        List<Province> list = new ArrayList<>();
        Cursor c = db.query("Province",null,null,null,null,null,null);
        if(c.moveToNext()){
            Province province = new Province();
            province.setId(c.getInt(c.getColumnIndex("id")));
            province.setProvinceName(c.getString(c.getColumnIndex("province_name")));
            province.setProvinceCode(c.getString(c.getColumnIndex("province_code")));
            list.add(province);
        }
        return list;
    }
    /**
     * 将city实例到数据库
     */
    public void saveCity(City city){
       if(city!=null){
           ContentValues values = new ContentValues();
           values.put("city_name",city.getCityName());
           values.put("city_code",city.getCityCode());
           values.put("province_id",city.getProvince_id());
           db.insert("City",null,values);
       }

    }
    /**
     * 从数据库读取某省份下所有城市的信息
     */
    public List<City> loadCities(String province_name){
        List<City> list = new ArrayList<>();
        Cursor c = db.query("City",null,"province_name = ?",new String[]{province_name},
                null,null,null);
        if(c.moveToNext()) {
            City city = new City();
            city.setCityId(c.getInt(c.getColumnIndex("id")));
            city.setCityName(c.getString(c.getColumnIndex("city_name")));
            city.setCityCode(c.getString(c.getColumnIndex("city_code")));
            list.add(city);
        }
        return list;
        }

}
