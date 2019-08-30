package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.lbs.cheng.lbscampus.bean.PositionBean;
import com.lbs.cheng.lbscampus.bean.UserDetailBean;
import com.lbs.cheng.lbscampus.util.GlideUtil;
import com.lbs.cheng.lbscampus.util.HttpUtil;
import com.lbs.cheng.lbscampus.util.LocationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PeopleDetailActivity extends BaseActivity {
    private static final String TAG = "PeopleDetailActivity";
    @BindView(R.id.to_navigation)
    ImageView toNavigation;
    ImageView back;
    TextView titleName;
    @BindView(R.id.click_to_navigation)
    TextView clickToNavigation;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_info)
    TextView userInfo;
    @BindView(R.id.user_image)
    ImageView userImage;

    private PositionBean position;
    private UserDetailBean userDetail;
    private UserDetailBean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_detail);
    }

    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("人员详情");
    }


    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        String json = intent.getStringExtra("people");
        userDetail = new Gson().fromJson(json,UserDetailBean.class);
        initInfo();
    }

    @Override
    protected void initView() {
        super.initView();
        initListener();
        initTitle();
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
                getPosition();
                break;
            case R.id.click_to_navigation:
                getPosition();
                break;

        }
    }



    public void getPosition(){
        List<String> list=new ArrayList<>();
        list.add(userDetail.getUserId());
        HttpUtil.sendOkHttpGetRequest(HttpUtil.GET_POSITION, list, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PeopleDetailActivity.this, "获取用户位置失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            final JSONObject jsonObject = new JSONObject(responseText);
                            position = new Gson().fromJson(jsonObject.toString(),PositionBean.class);
                            if(position.getLatitude()==0){
                                Toast.makeText(PeopleDetailActivity.this, "对方未共享位置", Toast.LENGTH_SHORT).show();
                            }else{
                                startNavigation();
                            }


                        }catch (JSONException e){

                            Toast.makeText(PeopleDetailActivity.this, "获取"+user.getUserId()+"信息失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
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
        BuildingBean buildingBean = new BuildingBean();

        endPt = new LatLng(position.getLatitude(), position.getLongitude());
        Toast.makeText(this, ""+position.getLatitude()+" "+position.getLongitude(), Toast.LENGTH_SHORT).show();
        //endPt = new LatLng(39.066082,117.289044);
        mParam = new WalkNaviLaunchParam().stPt(LocationUtil.getMyLocation()).endPt(endPt);
        WalkNavigateHelper.getInstance().routePlanWithParams(mParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d("daohang", "开始算路: ");
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
                Log.d("daohang",":算路失败");
            }
        });
    }
    public void initInfo(){
        userName.setText(userDetail.getUserName());

        if(userDetail.getUserImage() != null){
            String path = HttpUtil.Image + userDetail.getUserImage();
            //GlideUtil.REQUEST_OPTIONS.signature(new ObjectKey(System.currentTimeMillis()));

            GlideUtil.load(PeopleDetailActivity.this, path, userImage, GlideUtil.REQUEST_OPTIONS);
        }

    }
}
