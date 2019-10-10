package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
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
    int key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_state);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void initView() {
        super.initView();
        initSpring();
        initTitle();
        getRoomsState();
    }

    private void initRecycleView(){
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        adapter = new RoomStateAdapter(R.layout.item_room_state,list,RoomStateActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ARouter.getInstance().build("/course/main").withInt("key",2).withInt("tableflag",3).withString("courseName",list.get(position).getRoomName()).navigation();


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
        Intent intent = getIntent();
        key = intent.getIntExtra("key",0);
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        if(key == 0){
            titleName.setText("常用教室");
        }else{
            titleName.setText("教室状态");
        }

    }

    private void getRoomsState(){
        list = new ArrayList<>();
        if(key == 0){
            RoomStateBean building4 = new RoomStateBean("2004",1);
            list.add(building4);
            RoomStateBean building12 = new RoomStateBean("2210",0);
            list.add(building12);
            RoomStateBean building14 = new RoomStateBean("C301",0);
            list.add(building14);
            RoomStateBean building13 = new RoomStateBean("E101",1);
            list.add(building13);
        }else if(key == 1){
            RoomStateBean building1 = new RoomStateBean("2001",0);
            list.add(building1);
            RoomStateBean building2 = new RoomStateBean("2002",1);
            list.add(building2);
            RoomStateBean building3 = new RoomStateBean("2003",0);
            list.add(building3);
            RoomStateBean building4 = new RoomStateBean("2004",1);
            list.add(building4);
            RoomStateBean building5 = new RoomStateBean("2106",0);
            list.add(building5);
            RoomStateBean building6 = new RoomStateBean("2107",0);
            list.add(building6);
            RoomStateBean building7 = new RoomStateBean("2108",0);
            list.add(building7);
            RoomStateBean building8 = new RoomStateBean("2109",0);
            list.add(building8);
            RoomStateBean building9 = new RoomStateBean("2111",0);
            list.add(building9);
            RoomStateBean building10 = new RoomStateBean("2114",1);
            list.add(building10);
            RoomStateBean building11 = new RoomStateBean("2205",0);
            list.add(building11);
            RoomStateBean building12 = new RoomStateBean("2210",0);
            list.add(building12);
            RoomStateBean building13 = new RoomStateBean("2213",1);
            list.add(building13);
            RoomStateBean building14 = new RoomStateBean("2214",0);
            list.add(building14);
            RoomStateBean building15 = new RoomStateBean("2301",0);
            list.add(building15);
            RoomStateBean building16 = new RoomStateBean("2304",0);
            list.add(building16);
            RoomStateBean building17 = new RoomStateBean("2305",0);
            list.add(building17);
            RoomStateBean building18 = new RoomStateBean("2306",1);
            list.add(building18);
            RoomStateBean building19 = new RoomStateBean("2307",1);
            list.add(building19);
            RoomStateBean building20 = new RoomStateBean("2310",1);
            list.add(building20);
            RoomStateBean building21 = new RoomStateBean("2317",0);
            list.add(building21);
            RoomStateBean building22 = new RoomStateBean("2509",0);
            list.add(building22);
            RoomStateBean building23 = new RoomStateBean("2616",0);
            list.add(building23);

        }
        initRecycleView();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
        }
    }
}
