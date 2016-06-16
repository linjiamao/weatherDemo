package yeeaoo.weatherdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yo on 2016/6/16.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper{
    /**
     * Province表建表语句
     */
    public static final String CRETE_PROVINCE ="create table Province(" +
            "id integer primary key autoincrement ," +
            "province_name text)";
    /**
     * City表建表语句
     */
    public static final String CREATE_CITY = "create table city(" +
            "id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_name integer)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CRETE_PROVINCE);
        db.execSQL(CREATE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
