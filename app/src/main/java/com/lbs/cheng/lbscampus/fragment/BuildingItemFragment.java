package com.lbs.cheng.lbscampus.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.activity.BuildingDetailActivity;
import com.lbs.cheng.lbscampus.activity.LoginActivity;
import com.lbs.cheng.lbscampus.activity.SearchBuildingActivity;
import com.lbs.cheng.lbscampus.activity.WNaviGuideActivity;
import com.lbs.cheng.lbscampus.adapter.BuildingAdapter;
import com.lbs.cheng.lbscampus.adapter.NoticeAdapter;
import com.lbs.cheng.lbscampus.bean.BuildingBean;
import com.lbs.cheng.lbscampus.bean.LocationBean;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.StartptBean;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lbs.cheng.lbscampus.util.HttpUtil;
import com.lbs.cheng.lbscampus.util.LocationUtil;
import com.lbs.cheng.lbscampus.view.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;

/**
 * Created by cheng on 2019/2/21.
 */

public class BuildingItemFragment extends Fragment {
    View view;
    @BindView(R.id.building_item_recyclerview)
    RecyclerView recyclerView;
    Unbinder unbinder;
    List<BuildingBean> buildingList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_building_item, container, false);
            initData();
            initView();
        }
        return view;
    }
    private void initRecyclerView() {

        BuildingAdapter adapter = new BuildingAdapter(R.layout.item_building,buildingList,0,getActivity());
        adapter.openLoadAnimation(ALPHAIN);
        adapter.isFirstOnly(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.item_building_go:
                        startNavigation(position);
                        break;
                    case R.id.building_item:
                        Intent toDetail = new Intent(getActivity(), BuildingDetailActivity.class);
                        String json=new Gson().toJson(buildingList.get(position));
                        toDetail.putExtra("building",json);
                        startActivity(toDetail);
                }
            }
        });
    }
//    public void getBuildingData() {
//
//        for(int i=0;i<20;i++){
//            buildingList.add(new BuildingBean());
//        }
//    }

    private void initData() {
        unbinder = ButterKnife.bind(this, view);
    }

    private void initView() {
        initRecyclerView();

    }

    public void getBuildingData() {
        String url = HttpUtil.HOME_PATH + HttpUtil.SEARCH_BUILDING+"/type/"+ CommonUtils.buildingTypeId;

        HttpUtil.sendOkHttpGetRequest(url, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "搜索失败!", Toast.LENGTH_SHORT).show();
                        // progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            final JSONArray jsonArray = new JSONArray(responseText);
                            buildingList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<BuildingBean>>(){}.getType());
                            initRecyclerView();
                        }catch (JSONException e){
                            Toast.makeText(getActivity(), "搜索失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
    void startNavigation(final int position){
        WalkNavigateHelper.getInstance().initNaviEngine(getActivity(), new IWEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                //引擎初始化成功的回调
                routeWalkPlanWithParam(position);
            }
            @Override
            public void engineInitFail() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"导航失败",Toast.LENGTH_LONG).show();
                    }
                });

                //引擎初始化失败的回调
            }
        });

    }
    void routeWalkPlanWithParam(int position){
        LatLng endPt;
        WalkNaviLaunchParam mParam;
        endPt = new LatLng(buildingList.get(position).getLatitude(), buildingList.get(position).getLongitude());
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
                Intent intent = new Intent(getActivity(), WNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                //算路失败的回调
            }
        });
    }

}
