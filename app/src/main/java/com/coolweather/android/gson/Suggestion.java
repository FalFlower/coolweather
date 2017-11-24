package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dell on 2017/11/24.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;//舒适程度
    @SerializedName("cw")
    public CarWash carWash;//洗车建议
    public Sport sport;//运动建议
    public class Comfort{
        @SerializedName("txt")
        public String info;//信息
    }
    public class CarWash{
        @SerializedName("txt")
        public String info;//信息
    }
    public class Sport{
        @SerializedName("txt")
        public String info;//信息
    }
}
