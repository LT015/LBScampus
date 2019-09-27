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
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.activity.BuildingDetailActivity;
import com.lbs.cheng.lbscampus.activity.RoomStateActivity;
import com.lbs.cheng.lbscampus.activity.SearchBuildingActivity;
import com.lbs.cheng.lbscampus.adapter.BuildingAdapter;
import com.lbs.cheng.lbscampus.bean.BuildingBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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

        initBuildingList();

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
                        startActivity(intent);
                }
            }
        });
    }
    private void initBuildingList(){
        BuildingBean building = new BuildingBean();
        building.setName("教学楼");
        for(int i = 0;i < 5;i++){
            buildingList.add(building);
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
