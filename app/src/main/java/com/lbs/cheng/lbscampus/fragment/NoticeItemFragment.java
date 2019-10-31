package com.lbs.cheng.lbscampus.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.activity.NoticeActivity;
import com.lbs.cheng.lbscampus.activity.SearchActivity;
import com.lbs.cheng.lbscampus.adapter.NoticeAdapter;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lt.common.util.HttpUtil;
import com.lbs.cheng.lbscampus.view.PullToRefreshView;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
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
 * Created by cheng on 2019/2/10.
 */

public class NoticeItemFragment extends Fragment{
    View view;
    @BindView(R.id.notice_item_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.notice_refreshView)
    PullToRefreshView refreshView;
    @BindView(R.id.order_search)  //搜索框
    AutoLinearLayout search;
    Unbinder unbinder;
    public List<NoticeDetailBean> noticeList=new ArrayList<>();
    private boolean isFirstLoad = false;//初始化为false

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_notice_item, container, false);
            isFirstLoad = true;//视图创建完成，将变量置为true
            initData();
            initView();
            if (getUserVisibleHint()) {//判断Fragment是否可见
                getNoticeData();
            }
        }
        return view;
    }
    private void initRecyclerView() {
        NoticeAdapter adapter = new NoticeAdapter(R.layout.item_notice,noticeList,getActivity());
        adapter.openLoadAnimation(ALPHAIN);
        adapter.isFirstOnly(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent toContent = new Intent(getActivity(), NoticeActivity.class);
                String json = new Gson().toJson(noticeList.get(position));
                toContent.putExtra("noticeDetail",json);
                startActivity(toContent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //    public void getNoticeData() {
//
//        for(int i=0;i<20;i++){
//            noticeList.add(new NoticeDetailBean());
//        }
//
//    }
    public void getNoticeData() {
        List<String> list = new ArrayList<>();
        list.add(""+ CommonUtils.noticeTypeId+"/status/2");
        HttpUtil.sendOkHttpGetRequest(HttpUtil.HOME_PATH + HttpUtil.GET_NOTICE_BY_TYPE, list, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                if(getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            final JSONArray jsonArray = new JSONArray(responseText);
                            noticeList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<NoticeDetailBean>>(){}.getType());

                            initRecyclerView();

                        }catch (JSONException e){

                            Toast.makeText(getActivity(), "获取信息失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });





            }
        });

    }
    private void initData() {
        unbinder = ButterKnife.bind(this, view);
    }

    private void initView() {
        initRecyclerView();
        initRefreshView();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initRefreshView() {
        refreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getNoticeData();
                        refreshView.setRefreshing(false);
                    }
                },1600);
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
           if(CommonUtils.isResume == 1){
               firstLoadData();
           }
        }
    }

    public void firstLoadData(){
        if (isFirstLoad || CommonUtils.isResume == 1) {
            getNoticeData();
            isFirstLoad = false;
            CommonUtils.isResume = 0;

        }
    }
}
