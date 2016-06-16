package yeeaoo.weatherdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yo on 2016/6/16.
 */
public class City implements Parcelable {
    private String province_name;
    private String cityName;
    private String cityCode;
    private int cityId;

    public City() {
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            City city = new City();
            city.province_name = in.readString();
            city.cityName = in.readString();
            city.cityCode = in.readString();
            city.cityId = in.readInt();
            return city;
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(province_name);
        dest.writeString(cityName);
        dest.writeString(cityCode);
        dest.writeInt(cityId);
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

}
