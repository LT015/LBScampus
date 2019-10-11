package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.adapter.RoomStateAdapter;
import com.lbs.cheng.lbscampus.adapter.TagAdapter;
import com.lbs.cheng.lbscampus.bean.BuildingBean;
import com.lbs.cheng.lbscampus.bean.Room;
import com.lbs.cheng.lbscampus.bean.RoomStateBean;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lt.common.util.HttpUtil;
import com.lt.common.activity.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
    private int time = 1;
    private ArrayList<RoomStateBean> roomList;
    private int key;//key等于0 表示查看常用教室 1表示教学楼的教室
    private int buildingId;

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
        getRoomList();
    }

    private void initRecycleView(){
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        adapter = new RoomStateAdapter(R.layout.item_room_state,roomList,RoomStateActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ARouter.getInstance().build("/course/main").withInt("key",2).withInt("tableflag",3).withString("courseName",roomList.get(position).getRoomName()).navigation();
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
                time = i+1;
                getRoomList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

    }
    private void initTitle() {
        Intent intent = getIntent();
        key = intent.getIntExtra("key",0);
        if(key == 1){
            buildingId = intent.getIntExtra("buildingId",0);
        }
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        if(key == 0){
            titleName.setText("常用教室");
        }else{
            String buildingName = intent.getStringExtra("buildingName");
            titleName.setText("教室状态");
        }

    }



    private void getRoomList(){
        UserBean user = DataSupport.findLast(UserBean.class);
        List<String> list = new ArrayList<>();
        list.add("id");
        if(key == 0){
            list.add(user.getUserId());
        }else if(key == 1){
            list.add(String.valueOf(buildingId));
        }
        list.add("type");
        list.add(String.valueOf(key));
        list.add("classtime");
        list.add(String.valueOf(time));
        String url = HttpUtil.HOME_PATH + HttpUtil.GET_ROOMS;
        HttpUtil.sendOkHttpGetRequest( url, list, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RoomStateActivity.this, "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
                        // progressBar.setVisibility(View.GONE);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            final JSONArray jsonArray = new JSONArray(responseText);
                            roomList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<RoomStateBean>>(){}.getType());
                            initRecycleView();
                        }catch (JSONException e){
                            Log.d("LoginActivity",e.toString());
                            Toast.makeText(RoomStateActivity.this, "获取信息失败!", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        });
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
