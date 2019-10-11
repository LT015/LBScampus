package com.lbs.cheng.lbscampus.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.activity.BuildingDetailActivity;
import com.lbs.cheng.lbscampus.activity.RoomStateActivity;
import com.lbs.cheng.lbscampus.activity.SearchBuildingActivity;
import com.lbs.cheng.lbscampus.adapter.BuildingAdapter;
import com.lbs.cheng.lbscampus.bean.BuildingBean;
import com.lt.common.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;

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

public class RoomFragment extends Fragment {
    View view;
    Unbinder unbinder;
    TextView titleName;
    @BindView(R.id.room_recycler)
    RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private BuildingAdapter adapter;
    private List<BuildingBean> buildingList=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_room, container, false);
            initData();
            initView();
        }
        return view;

    }

    public void initData(){
        unbinder = ButterKnife.bind(this, view);

    }
    public void initView(){
        initTitle();
        initRecyclerView();
    }

    private void initTitle() {
        titleName=view.findViewById(R.id.title_name);
        titleName.setText("选择教学楼");
    }
    private void initRecyclerView() {


        adapter = new BuildingAdapter(R.layout.item_building,buildingList,1,getActivity());
        adapter.openLoadAnimation(ALPHAIN);
        adapter.isFirstOnly(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.building_item:
                        Intent intent =new Intent(getActivity(), RoomStateActivity.class);
                        intent.putExtra("key",1);
                        intent.putExtra("buildingId",buildingList.get(position).getBuildingId());
                        intent.putExtra("buildingName",buildingList.get(position).getName());
                        startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getBuildingData();
    }

    public void getBuildingData() {
        String url = HttpUtil.HOME_PATH + HttpUtil.SEARCH_BUILDING+"/type/1";

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
                            if(buildingList.size()==0){
                                Toast.makeText(getActivity(), "未找到教学楼", Toast.LENGTH_SHORT).show();
                            }
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

}
