package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dell on 2017/11/24.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
