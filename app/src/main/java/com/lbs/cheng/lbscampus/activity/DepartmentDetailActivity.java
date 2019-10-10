package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
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
import com.lbs.cheng.lbscampus.bean.DepartmentDetailBean;
import com.lbs.cheng.lbscampus.util.GlideUtil;
import com.lt.common.util.HttpUtil;
import com.lbs.cheng.lbscampus.util.LocationUtil;

import butterknife.BindView;

public class DepartmentDetailActivity extends BaseActivity {

    @BindView(R.id.to_navigation)
    ImageView toNavigation;
    ImageView back;
    TextView titleName;
    @BindView(R.id.click_to_navigation)
    TextView clickToNavigation;
    @BindView(R.id.department_image)
    ImageView departmentImage;
    @BindView(R.id.department_name)
    TextView departmentName;
    @BindView(R.id.demartment_description)
    TextView departmentDescription;

    private DepartmentDetailBean department;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_detail);
    }

    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("部门详情");
    }


    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        String json = intent.getStringExtra("department");
        department = new Gson().fromJson(json,DepartmentDetailBean.class);

    }

    @Override
    protected void initView() {
        super.initView();
        initListener();
        initTitle();
        String imagePath = HttpUtil.HOME_PATH + HttpUtil.Image+"department/"+department.getPicturePath();
        if(imagePath != null){
            GlideUtil.load(DepartmentDetailActivity.this, imagePath, departmentImage, GlideUtil.REQUEST_OPTIONS);
        }
        departmentName.setText(department.getName());
        departmentDescription.setText(department.getDescription());

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
    void routeWalkPlanWithParam(){
        LatLng endPt;
        WalkNaviLaunchParam mParam;

        endPt = new LatLng(department.getBuilding().getLatitude(), department.getBuilding().getLongitude());
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
            }
        });
    }
}
