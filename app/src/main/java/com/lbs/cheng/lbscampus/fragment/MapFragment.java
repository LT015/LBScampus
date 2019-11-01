package com.lbs.cheng.lbscampus.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.activity.BuildingActivity;
import com.lbs.cheng.lbscampus.activity.SearchAllActivity;
import com.lbs.cheng.lbscampus.util.LocationService;
import com.lbs.cheng.lbscampus.util.LocationUtil;
import com.lbs.cheng.lbscampus.util.Utils;
import com.zhy.autolayout.AutoLinearLayout;

import org.litepal.crud.DataSupport;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by cheng on 2019/1/15.
 */

public class MapFragment extends Fragment implements View.OnClickListener{
    @BindView(R.id.fragment_map_map)
    TextureMapView mMapView;
    BaiduMap mBaiduMap;
    private LocationService locService;
    private LinkedList<MapFragment.LocationEntity> locationList = new LinkedList<MapFragment.LocationEntity>();
    private Boolean isFirst=true;
    private View view;
    Unbinder unbinder;
    @BindView(R.id.map_route)
    AutoLinearLayout route;
    @BindView(R.id.fragment_search)
    AutoLinearLayout search;
    LatLng myPt;
    private static final int BAIDU_READ_PHONE_STATE =10;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
            view = inflater.inflate(R.layout.fragment_map, container, false);
            initData();
            initView();
            initMap();
        }
        return view;
    }
    private void initData() {
        unbinder = ButterKnife.bind(this, view);
    }
    private void initView() {
        initListener();
    }
    void initListener(){
        search.setOnClickListener(this);
        route.setOnClickListener(this);
    }
    private void initMap() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18));
        myPt = LocationUtil.getMyLocation();
        if (myPt != null) {

            // 构建Marker图标
            BitmapDescriptor bitmap = null;
//                    if (iscal == 0) {
//                    bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_openmap_mark); // 非推算结果
//                    } else {
            bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_openmap_focuse_mark); // 推算结果
//                    }
            // 构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions().position(myPt).icon(bitmap);
            // 在地图上添加新Marker，并显示
            mBaiduMap.clear();
            mBaiduMap.addOverlay(option);
            if (isFirst) {
                isFirst = false;
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(myPt));
            }
        }


    }
    @Override
    public void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        super.onDestroy();


    }
    @Override
    public void onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.setVisibility(View.VISIBLE);
        mMapView.onResume();
        super.onResume();
        initMap();
    }
    @Override
    public void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.setVisibility(View.INVISIBLE);
        mMapView.onPause();
        super.onPause();
    }
    @Override
    public void onClick(View v) {
        if (LocationUtil.getMyLocation()!=null){
            switch (v.getId()){
                case R.id.map_route:
                    Intent intent = new Intent(getActivity(), BuildingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.fragment_search :
                    Intent toSearch = new Intent(getActivity(), SearchAllActivity.class);
                    startActivity(toSearch);
                    break;
            }
        }else{
            if(getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(),"尚未获取当前位置",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    class LocationEntity {
        BDLocation location;
        long time;
    }
    //Android6.0以上，动态获取权限
    public void showContacts(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(),"没有权限,请手动开启定位权限",Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, BAIDU_READ_PHONE_STATE);
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
                } else {
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(getActivity(), "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

}
