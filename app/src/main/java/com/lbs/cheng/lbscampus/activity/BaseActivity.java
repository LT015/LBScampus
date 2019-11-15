package com.lbs.cheng.lbscampus.activity;

import android.Manifest;
import android.app.Activity;
import com.baidu.location.BDAbstractLocationListener;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.PositionBean;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lbs.cheng.lbscampus.fragment.MapFragment;
import com.lt.common.util.HttpUtil;
import com.lbs.cheng.lbscampus.util.LocationService;
import com.lbs.cheng.lbscampus.util.LocationUtil;
import com.lbs.cheng.lbscampus.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by rjq on 2017/11/29 0029.
 */

public class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int BAIDU_READ_PHONE_STATE =10;
    LatLng startPt;
    private LocationService locService;
    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>();
    Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        locService = new LocationService(getApplicationContext());
        showContacts();

    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        fullScreen(this);
        initData();
        initView();
    }

    protected void initView(){

    }

    protected void initData(){
        unbinder=ButterKnife.bind(this);
    }



    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
//沉浸式
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }
    public void showContacts(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"没有权限,请手动开启定位权限",Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, BAIDU_READ_PHONE_STATE);
        }else{
            locService.registerListener(listener);
            locService.start();
        }
    }

    //Android6.0申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理
                    locService.registerListener(listener);
                    locService.start();
                } else {
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(this, "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 实现定位回调
     */
    BDAbstractLocationListener listener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            // TODO Auto-generated method stub
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明 location.getLocType()
            if (location != null && (location.getLocType() == 161 || location.getLocType() == 66 || location.getLocType() == 61)) {
                Message locMsg = locHander.obtainMessage();
                Bundle locData;
                locData = Algorithm(location);
                if (locData != null) {
                    locData.putParcelable("loc", location);
                    locMsg.setData(locData);
                    locHander.sendMessage(locMsg);
                }
            }
        }

    };
    class LocationEntity {
        BDLocation location;
        long time;
    }
    /***
     * 平滑策略代码实现方法，主要通过对新定位和历史定位结果进行速度评分，
     * 来判断新定位结果的抖动幅度，如果超过经验值，则判定为过大抖动，进行平滑处理,若速度过快，
     * 则推测有可能是由于运动速度本身造成的，则不进行低速平滑处理 ╭(●｀∀´●)╯
     *
     * @param location
     * @return Bundle
     */
    private Bundle Algorithm(BDLocation location) {
        Bundle locData = new Bundle();
        double curSpeed = 0;
        if (locationList.isEmpty() || locationList.size() < 2) {
            LocationEntity temp = new LocationEntity();
            temp.location = location;
            temp.time = System.currentTimeMillis();
            locData.putInt("iscalculate", 0);
            locationList.add(temp);
        } else {
            if (locationList.size() > 5)
                locationList.removeFirst();
            double score = 0;
            for (int i = 0; i < locationList.size(); ++i) {
                LatLng lastPoint = new LatLng(locationList.get(i).location.getLatitude(),
                        locationList.get(i).location.getLongitude());
                LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
                double distance = DistanceUtil.getDistance(lastPoint, curPoint);
                curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).time) / 1000;
                score += curSpeed * Utils.EARTH_WEIGHT[i];
            }
            if (score > 0.00000999 && score < 0.00005) { // 经验值,开发者可根据业务自行调整，也可以不使用这种算法
                location.setLongitude(
                        (locationList.get(locationList.size() - 1).location.getLongitude() + location.getLongitude())
                                / 2);
                location.setLatitude(
                        (locationList.get(locationList.size() - 1).location.getLatitude() + location.getLatitude())
                                / 2);
                locData.putInt("iscalculate", 1);
            } else {
                locData.putInt("iscalculate", 0);
            }
            LocationEntity newLocation = new LocationEntity();
            newLocation.location = location;
            newLocation.time = System.currentTimeMillis();
            locationList.add(newLocation);
        }
        return locData;
    }

    private Handler locHander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            try {
                BDLocation location = msg.getData().getParcelable("loc");
                int iscal = msg.getData().getInt("iscalculate");
                if (location != null) {
                    startPt = new LatLng(location.getLatitude(), location.getLongitude());
                    LocationUtil.setMyLocation(startPt);
                    UserBean userBean= DataSupport.findLast(UserBean.class);
                    if(userBean!=null){
                        final PositionBean position=new PositionBean();
                       // Log.d("location11", "学号: "+userBean.getUserId());
                        position.setUserId(userBean.getUserId());
                        position.setLatitude(location.getLatitude());
                        position.setLongitude( location.getLongitude());
                        String json = new Gson().toJson(position);

                        HttpUtil.sendOkHttpPutRequest( HttpUtil.HOME_PATH + HttpUtil.UPDATE_POSITION, json, new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d("location11", "upadte location failed:");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String responseText=response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try{
                                            final JSONObject jsonObject = new JSONObject(responseText);
                                            PositionBean positionBean = new Gson().fromJson(jsonObject.toString(),PositionBean.class);

                                           // Log.d("location11", "纬度: "+positionBean.getLatitude());


                                        }catch (JSONException e){


                                        }
                                    }
                                });
                            }
                        });
                    }


                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    };
}
