package com.lbs.cheng.lbscampus.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    MapView mMapView;
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
    LatLng startPt;
    private static final int BAIDU_READ_PHONE_STATE =10;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            SDKInitializer.initialize(getActivity().getApplicationContext());
            locService = new LocationService(getActivity().getApplicationContext());
            LocationClientOption mOption = locService.getDefaultLocationClientOption();
            mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
            mOption.setCoorType("bd09ll");
            locService.setLocationOption(mOption);
            locService.registerListener(listener);
            view = inflater.inflate(R.layout.fragment_map, container, false);
            initData();
            initView();
            initMap();
            showContacts();
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
        locService.start();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
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

    /***
     * 定位结果回调，在此方法中处理定位结果
     */
    BDAbstractLocationListener listener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub

            if (location != null && (location.getLocType() == 161 || location.getLocType() == 66)) {
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
            MapFragment.LocationEntity temp = new MapFragment.LocationEntity();
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
            MapFragment.LocationEntity newLocation = new MapFragment.LocationEntity();
            newLocation.location = location;
            newLocation.time = System.currentTimeMillis();
            locationList.add(newLocation);
        }
        return locData;
    }

    /***
     * 接收定位结果消息，并显示在地图上
     */
    private Handler locHander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            try {
                BDLocation location = msg.getData().getParcelable("loc");
                int iscal = msg.getData().getInt("iscalculate");
                if (location != null) {
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                    startPt=point = new LatLng(location.getLatitude(), location.getLongitude());
                    LocationUtil.setMyLocation(startPt);
                    // 构建Marker图标
                    BitmapDescriptor bitmap = null;
//                    if (iscal == 0) {
//                    bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_openmap_mark); // 非推算结果
//                    } else {
                    bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_openmap_focuse_mark); // 推算结果
//                    }
                    // 构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
                    // 在地图上添加新Marker，并显示
                    mBaiduMap.clear();
                    mBaiduMap.addOverlay(option);
                    if (isFirst){
                        isFirst=false;
                        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    };
}
