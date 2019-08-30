package com.lbs.cheng.lbscampus.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.adapter.TagAdapter;
import com.lbs.cheng.lbscampus.bean.TagBean;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lbs.cheng.lbscampus.util.HttpUtil;

import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.http.params.HttpParams;
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

public class SelectInterstActivity extends BaseActivity {
    ImageView back;
    TextView titleName;
    TagAdapter adapter;
    @BindView(R.id.grid_recycleview)
    RecyclerView recyclerView;
    @BindView(R.id.select_interst_finish)
    TextView finish;
    UserBean userBean;
    private List<TagBean> tagBeanList ;
    List<Integer> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_interst);
    }

    @Override
    protected void initData() {
        super.initData();

        userBean = DataSupport.findLast(UserBean.class);
        if(userBean.getHobbyList()!= null){
            list = userBean.getHobbyList();
        }else{
            list =new ArrayList<>();
        }

        getTagList();
    }

    @Override
    protected void initView() {
        super.initView();

        initTitle();
        initListener();
    }
    void initListener(){
        finish.setOnClickListener(this);
    }


    private void initRecycleView(){
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        adapter = new TagAdapter(R.layout.item_interest,tagBeanList,list,SelectInterstActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int i=  list.indexOf(tagBeanList.get(position).getTagId());

                if (i != -1){
                    list.remove(i);
                    TextView textView=view.findViewById(R.id.item_interst_tv);
                    textView.setBackgroundResource(R.drawable.checkbox_unselect);
                }
                else if(i == -1){
                    TextView textView=view.findViewById(R.id.item_interst_tv);
                    textView.setBackgroundResource(R.drawable.checkbox_select);
                    list.add(tagBeanList.get(position).getTagId());
                }


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
            case R.id.select_interst_finish:
                saveInterest();
                finish();
                break;

        }
    }

    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("选择爱好");
    }


    public void getTagList(){  //通过网络请求得到所有标签
        HttpUtil.sendOkHttpGetRequest(HttpUtil.TAG_LIST, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SelectInterstActivity.this, "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
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
                            tagBeanList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<TagBean>>(){}.getType());
                            initRecycleView();

                        }catch (JSONException e){
                            Log.d("LoginActivity",e.toString());
                            Toast.makeText(SelectInterstActivity.this, "获取信息失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void saveInterest(){

        String json = new Gson().toJson(list);
        HttpUtil.upLoadImgsRequest(HttpUtil.UPDATE_HOBBY + "/"+userBean.getUserId(), json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SelectInterstActivity.this, "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
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
                        List<Integer> list1;
                        try{
                            final JSONArray jsonArray = new JSONArray(responseText);
                            list1 = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<Integer>>(){}.getType());
                            userBean.setHobbyList(list);
                            userBean.saveThrows();
                            Toast.makeText(SelectInterstActivity.this, "保存成功", Toast.LENGTH_SHORT).show();

                        }catch (JSONException e){
                            Log.d("LoginActivity",e.toString());
                            Toast.makeText(SelectInterstActivity.this, "获取信息失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
