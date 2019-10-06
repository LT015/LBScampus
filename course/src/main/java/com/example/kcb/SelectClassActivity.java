package com.example.kcb;

import android.content.Intent;
import android.support.annotation.BinderThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lt.common.activity.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;

public class SelectClassActivity extends BaseActivity {

    ImageView back;
    TextView titleName;
    ImageView titleInfo;
    RecyclerView recyclerView;
    private ArrayList<String> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_class);
        recyclerView = findViewById(R.id.calss_name_list);
    }

    @Override
    protected void initData() {
        super.initData();
        getClassName();
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.title_back) {
            finish();
        }
    }
    private void getClassName(){
        for(int i = 0;i<10;i++){
            list.add("计科1601");
            list.add("信工1601");
            list.add("网络1601");
            list.add("软件1601");
        }

        initRecyclerView();

    }
    private void initRecyclerView() {
        MyAdapter adapter = new MyAdapter(R.layout.item_class_name,list,SelectClassActivity.this);
        adapter.openLoadAnimation(ALPHAIN);
        adapter.isFirstOnly(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ARouter.getInstance().build("/course/main").withInt("key", 0).withString("courseName",list.get(position)).navigation();
            }
        });
    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName = findViewById(R.id.title_name);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("选择班级");
    }

}
