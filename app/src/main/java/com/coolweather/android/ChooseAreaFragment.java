package com.coolweather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by dell on 2017/11/22.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY =1;
    public static final int LEVEL_COUNTY=2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();

    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;

    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //当前选中的等级
    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.choose_area,container,false);
        titleText=(TextView) view.findViewById(R.id.title_text);
        backButton=(Button) view.findViewById(R.id.back_button);
        listView=(ListView) view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }
@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if(currentLevel==LEVEL_PROVINCE){
                selectedProvince=provinceList.get(position);
                queryCities();
            }else if (currentLevel==LEVEL_CITY){
                selectedCity=cityList.get(position);
                queryCounties();
            }else if (currentLevel==LEVEL_COUNTY){
                String weatherId=countyList.get(position).getWeatherId();
                Intent intent=new Intent(getActivity(),WeatherActivity.class);
                intent.putExtra("weather_id",weatherId);
                startActivity(intent);
                getActivity().finish();
            }
        }
    });
    backButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           if (currentLevel==LEVEL_COUNTY){
               queryCities();
           }else if (currentLevel==LEVEL_CITY){
               queryProvinces();
           }
        }
    });
    queryProvinces();
}
/*
* 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
*   View.VISIBLE View可见
    View.INVISIBLE View不可以见，但仍然占据可见时的大小和位置。
    View.GONE View不可见，且不占据空间。
* */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(Province.class);//litePal的查询接口来从数据库中读取省级数据
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();//notifyDataSetChanged是Adater的一个方法,主要用来通知ListView,告诉它Adapter的数据发生了变化,需要更新ListView的显示
            listView.setSelection(0);
            setListViewAnimation();//设置动画
            currentLevel=LEVEL_PROVINCE;
        }else {
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");//从服务器上查询数据
        }
    }


    /*
    * 查询全国所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
    * */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);//这个方法的作用就是将第0个item显示在listView的最上面一项
            setListViewAnimation();//设置动画
            currentLevel=LEVEL_CITY;
        }else {
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
//       Toast.makeText(getContext(),"code :"+selectedProvince.getProvinceCode()+" name :"+selectedProvince.getProvinceName()+" id :"+selectedProvince.getId(),Toast.LENGTH_SHORT).show();
    }
/*
* 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
* */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            setListViewAnimation();//设置动画
            currentLevel=LEVEL_COUNTY;
        }else {
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }

    }

/*
* 根据传入的地址和类型从服务器上查询省市县数据
* */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override//响应
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                if ("province".equals(type)){
                    result= Utility.handleProvinceResponse(responseText);//自定义工具Utility
                }else if ("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result=Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                               queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
            @Override//失败
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });
    }
    //关闭进度对话框
    private void closeProgressDialog() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    //显示进度对话框
    private void showProgressDialog() {
        if (progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
    }
    //设置动画
    private void setListViewAnimation(){
        LayoutAnimationController controller=new LayoutAnimationController(AnimationUtils.loadAnimation(getContext(),R.anim.listview_anim));
//        LayoutAnimationController.ORDER_NORMAL;    //顺序显示
//        LayoutAnimationController.ORDER_REVERSE;//反显示
//        LayoutAnimationController.ORDER_RANDOM//随机显示
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        controller.setInterpolator(new AccelerateInterpolator());
        listView.setLayoutAnimation(controller);
        listView.startLayoutAnimation();
    }
}


