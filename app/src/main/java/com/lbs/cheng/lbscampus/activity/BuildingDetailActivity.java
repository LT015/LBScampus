package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.google.gson.Gson;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.BuildingBean;
import com.lbs.cheng.lbscampus.util.GlideUtil;
import com.lt.common.util.HttpUtil;
import com.lbs.cheng.lbscampus.util.LocationUtil;

import org.w3c.dom.Text;

import butterknife.BindView;

import static com.baidu.mapapi.walknavi.model.WalkRoutePlanError.DISTANCE_MORE_THAN_50KM;

public class BuildingDetailActivity extends BaseActivity {


    @BindView(R.id.to_navigation)
    ImageView toNavigation;
    ImageView back;
    TextView titleName;
    @BindView(R.id.click_to_navigation)
    TextView clickToNavigation;
    @BindView(R.id.building_image)
    ImageView buildingImage;
    @BindView(R.id.building_name)
    TextView buildingName;
    @BindView(R.id.description)
    TextView description;
    private BuildingBean building;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail);
    }

    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("建筑详情");
    }


    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        String json = intent.getStringExtra("building");
        building = new Gson().fromJson(json,BuildingBean.class);

    }

    @Override
    protected void initView() {
        super.initView();
        initListener();
        initTitle();
        //buildingImage.setImageResource();
        String imagePath= HttpUtil.HOME_PATH + HttpUtil.Image + "building/" + building.getPicturePath();
        if(imagePath != null){
            GlideUtil.load(BuildingDetailActivity.this, imagePath, buildingImage, GlideUtil.REQUEST_OPTIONS);
        }

        buildingName.setText(building.getName());
        description.setText(building.getDescription());
    }

    private void initListener() {
        toNavigation.setOnClickListener(this);
        clickToNavigation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.to_navigation:
                startNavigation();
                break;
            case R.id.click_to_navigation:
                startNavigation();
                break;
        }
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
        endPt = new LatLng(building.getLatitude(), building.getLongitude());
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
