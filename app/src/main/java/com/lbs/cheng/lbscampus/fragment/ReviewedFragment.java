package com.lbs.cheng.lbscampus.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.activity.AddNoticeActivity;
import com.lbs.cheng.lbscampus.activity.NoticeActivity;
import com.lbs.cheng.lbscampus.adapter.NoticeAdapter;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lbs.cheng.lbscampus.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.crud.DataSupport;

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
 * Created by cheng on 2019/4/26.
 */
//审核中
public class ReviewedFragment extends Fragment implements View.OnClickListener{
    @BindView(R.id.reviewed_recycler_view)
    RecyclerView recyclerView;
    private View view;
    Unbinder unbinder;
    public List<NoticeDetailBean> noticeList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_reviewed, container, false);
            initData();
            initView();
        }
        return view;
    }

    private void initData() {
        unbinder = ButterKnife.bind(this, view);
        getNoticeData();

    }

    private void initView() {
        initRecyclerView();
    }
    private void initRecyclerView() {
        NoticeAdapter adapter = new NoticeAdapter(R.layout.item_reviewed,noticeList,getContext());
        adapter.openLoadAnimation(ALPHAIN);
        adapter.isFirstOnly(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.item_notice_layout:
                        Intent toContent = new Intent(getActivity(), NoticeActivity.class);
                        String json = new Gson().toJson(noticeList.get(position));
                        toContent.putExtra("noticeDetail",json);
                        startActivity(toContent);
                        break;
                }

            }
        });
    }
//    public void getNoticeData() {
//        List<NoticeDetailBean> list = new ArrayList<>();
//        for(int i=0;i<20;i++){
//            list.add(new NoticeDetailBean());
//        }
//
//    }
    public void getNoticeData() {
        UserBean user = DataSupport.findLast(UserBean.class);
        String url=HttpUtil.GET_MY_NOTICE+"/publisher/"+user.getUserId()+"/status/1";
        HttpUtil.sendOkHttpGetRequest(url, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onClick(View view) {

    }
}