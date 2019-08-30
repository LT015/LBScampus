package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.adapter.NoticeAdapter;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;

public class CollectNoticeActivity extends BaseActivity {
    ImageView back;
    TextView titleName;
    @BindView(R.id.collect_notice_recycler_view)
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_notice);
    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("我的收藏");
    }
    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        initRecyclerView();
    }
    private void initRecyclerView() {
        NoticeAdapter adapter = new NoticeAdapter(R.layout.item_notice,getNoticeData(),this);
        adapter.openLoadAnimation(ALPHAIN);
        adapter.isFirstOnly(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent toContent = new Intent(CollectNoticeActivity.this, NoticeActivity.class);
                NoticeDetailBean notice =  new NoticeDetailBean();
                notice.setTitle("收藏公告");
                String json = new Gson().toJson(notice);
                toContent.putExtra("noticeDetail",json);
                startActivity(toContent);
            }
        });
    }
    public List<NoticeDetailBean> getNoticeData() {
        List<NoticeDetailBean> list = new ArrayList<>();
        for(int i=0;i<20;i++){
            list.add(new NoticeDetailBean());
        }
        return list;
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
