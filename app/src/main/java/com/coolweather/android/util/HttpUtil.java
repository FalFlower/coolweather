package com.coolweather.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by dell on 2017/11/21.
 * 用来和服务器进行交互
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);//call异步的方法是通过调用Call对象的 enqueue()方法，可以避免在新开子线程
    }
}
