package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dell on 2017/11/24.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;//温度
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;//天气信息
    }
}
