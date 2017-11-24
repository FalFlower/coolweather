package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dell on 2017/11/24.
 */

public class Forecast {
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;//温度
    @SerializedName("cond")
    public More more;
    public class Temperature{
        public String max;
        public String min;
    }
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
