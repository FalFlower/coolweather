package com.coolweather.android.gson;

/**
 * Created by dell on 2017/11/24.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;//污染指数
        public String pm25;
    }
}
