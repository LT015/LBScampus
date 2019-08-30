package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.adapter.NoticeAdapter;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lbs.cheng.lbscampus.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;

public class LostPropertyActivity extends BaseActivity {

    ImageView back;
    TextView titleName;
    @BindView(R.id.my_notice_recycler_view)
    RecyclerView recyclerView;
    public List<NoticeDetailBean> noticeList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_property);
    }

    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("失物招领");
    }
    @Override
    protected void initData() {
        super.initData();
        getNoticeData();
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        initRecyclerView();
    }
    private void initRecyclerView() {
        NoticeAdapter adapter = new NoticeAdapter(R.layout.item_notice,noticeList,this);
        adapter.openLoadAnimation(ALPHAIN);
        adapter.isFirstOnly(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent toContent = new Intent(LostPropertyActivity.this, NoticeActivity.class);
                String json = new Gson().toJson(noticeList.get(position));
                toContent.putExtra("noticeDetail",json);
                startActivity(toContent);
            }
        });
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
        list.add("2");
        list.add("/status/2");
        HttpUtil.sendOkHttpGetRequest(HttpUtil.GET_NOTICE_BY_TYPE, list, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LostPropertyActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
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
                            noticeList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<NoticeDetailBean>>(){}.getType());

                            initRecyclerView();

                        }catch (JSONException e){

                            Toast.makeText(LostPropertyActivity.this, "获取信息失败!", Toast.LENGTH_SHORT).show();
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
