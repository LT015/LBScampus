package com.lbs.cheng.lbscampus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.adapter.RoomStateAdapter;
import com.lbs.cheng.lbscampus.adapter.TagAdapter;
import com.lbs.cheng.lbscampus.bean.RoomStateBean;
import com.lt.common.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class RoomStateActivity extends BaseActivity {

    ImageView back;
    TextView titleName;
    RoomStateAdapter adapter;
    @BindView(R.id.select_time)
    Spinner spinner;
    @BindView(R.id.grid_recycleview)
    RecyclerView recyclerView;
    private ArrayAdapter<String> arr_adapter;
    private List<String> data_list;
    private int time = 0;
    ArrayList<RoomStateBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_state);
    }

    @Override
    protected void initData() {
        super.initData();
        getRoomsState();
    }

    @Override
    protected void initView() {
        super.initView();
        initSpring();
    }

    private void initRecycleView(){
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        adapter = new RoomStateAdapter(R.layout.item_room_state,list,RoomStateActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ARouter.getInstance().build("/course/main").navigation();


            }
        });

    }

    void initSpring(){
        //数据
        data_list = new ArrayList<String>();
        data_list.add("第一单元");
        data_list.add("第二单元");
        data_list.add("第三单元");
        data_list.add("第四单元");
        data_list.add("第五单元");
        data_list.add("第六单元");


        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                time = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("教室状态");
    }

    private void getRoomsState(){

        list = new ArrayList<>();
        RoomStateBean building = new RoomStateBean();
        building.setRoomName("C101");
        building.setState(0);
        for(int i = 0;i < 5;i++){
            list.add(building);
        }
        list.get(3).setState(1);

        initRecycleView();


    }

}
