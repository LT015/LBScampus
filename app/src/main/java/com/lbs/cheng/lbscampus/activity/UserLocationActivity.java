package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.fragment.MapFragment;
import com.lbs.cheng.lbscampus.util.LocationService;
import com.lbs.cheng.lbscampus.util.LocationUtil;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.Unbinder;

import static com.baidu.mapapi.walknavi.model.WalkRoutePlanError.DISTANCE_MORE_THAN_50KM;

public class UserLocationActivity extends BaseActivity {

    ImageView back;
    TextView titleName;
    @BindView(R.id.user_location_map)
    TextureMapView mMapView;
    BaiduMap mBaiduMap;
    private Boolean isFirst=true;
    Unbinder unbinder;
    @BindView(R.id.map_go)
    AutoLinearLayout route;

    LatLng startPt;
    private double longitude;
    private double latitude;
    private static final int BAIDU_READ_PHONE_STATE =10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        longitude = intent.getDoubleExtra("longitude",0);
        latitude = intent.getDoubleExtra("latitude",0);

    }

    @Override
    protected void initView() {
        super.initView();
        route.setOnClickListener(this);
        initTitle();
        initMap();

    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("用户位置");
    }
    private void initMap() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18));

        if (longitude != 0) {
            LatLng point = new LatLng(latitude, longitude);

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
            if (isFirst) {
                isFirst = false;
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
            }
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.map_go:
                startNavigation();
                break;
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
    }
    @Override
    public void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.setVisibility(View.INVISIBLE);
        mMapView.onPause();
        super.onPause();
    }
    /**
     * 使用步行导航前，需要初始化引擎
     */
    void startNavigation(){
        WalkNavigateHelper.getInstance().initNaviEngine(this, new IWEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                //引擎初始化成功的回调
                routeWalkPlanWithParam();
            }
            @Override
            public void engineInitFail() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"导航失败",Toast.LENGTH_LONG).show();
                    }
                });

                //引擎初始化失败的回调
            }
        });

    }

    /**
     * 引擎初始化成功之后，发起导航算路。算路成功后，在回调函数中设置跳转至诱导页面。
     * 开始算路
     */
    void routeWalkPlanWithParam(){
        LatLng endPt;
        WalkNaviLaunchParam mParam;
        //设置导航的起终点信息
        endPt = new LatLng(latitude, longitude);
        mParam = new WalkNaviLaunchParam().stPt(LocationUtil.getMyLocation()).endPt(endPt);
        WalkNavigateHelper.getInstance().routePlanWithParams(mParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                //开始算路的回调
            }

            @Override
            public void onRoutePlanSuccess() {
                //算路成功
                //跳转至诱导页面
                Intent intent = new Intent(getApplicationContext(), WNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                //算路失败的回调
                if(walkRoutePlanError == DISTANCE_MORE_THAN_50KM){
                    Toast.makeText(getApplicationContext(),"距离过远，步行导航失败",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
