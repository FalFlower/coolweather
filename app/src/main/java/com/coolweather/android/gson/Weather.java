package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dell on 2017/11/24.
 */

public class Weather {
    public String status;//状态
    public Basic basic;//基本信息
    public AQI aqi;//污染状况
    public Now now;//现在天气
    public Suggestion suggestion;//建议
    @SerializedName("daily_forecast")//未来天气
    public List<Forecast> forecastList;
}
