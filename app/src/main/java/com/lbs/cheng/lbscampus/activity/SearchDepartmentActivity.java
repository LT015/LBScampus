package com.lbs.cheng.lbscampus.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.adapter.DepartmentAdapter;
import com.lbs.cheng.lbscampus.bean.DepartmentDetailBean;
import com.lbs.cheng.lbscampus.bean.SearchHistoricalBean;
import com.lt.common.util.HttpUtil;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;

public class SearchDepartmentActivity extends BaseActivity {

    @BindView(R.id.back_btn)
    ImageButton backBtn;
    @BindView(R.id.search_activity_history_flowlayout)
    TagFlowLayout mHistoryFlowLayout;
    @BindView(R.id.search_et)
    EditText ETsearch;
    @BindView(R.id.activity_search_no_history)
    TextView TVnoHistory;
    @BindView(R.id.activity_search_clear_history)
    TextView TVclearHistory;
    @BindView(R.id.search_btn)
    TextView searchBtn;
    @BindView(R.id.search_section_spinner)
    Spinner spinner;
    @BindView(R.id.search_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    ImageView emptyImg;
    private ArrayAdapter<String> arr_adapter;
    private List<String> data_list;
    private TagAdapter<SearchHistoricalBean> mHistoryFlowLayoutAdapter;
    private List<String> mHotData;//热门推荐
    private List<SearchHistoricalBean> mHistoryData;
    private List<DepartmentDetailBean> departmentDetailList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    private String name = null;//搜索框输的部门名字
    private int type = 0;//选择的部门类型
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_section);
    }
    void initSpring(){
        //数据
        data_list = new ArrayList<String>();
        data_list.add("所有部门");
        data_list.add("教学系部");
        data_list.add("学校部门");
        data_list.add("其他部门");


        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                type = i;
                if(type != 0){
                    getDepartmentDetailList();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

    }
    @Override
    protected void initData() {
        super.initData();


        mHotData = new ArrayList<>();
        mHotData.add("考研");
        mHotData.add("U盘");
        mHotData.add("讲座");

        mHistoryData = DataSupport.order("time desc").find(SearchHistoricalBean.class);


    }

    @Override
    protected void initView() {
        super.initView();
        //让软键盘延时弹出，以更好的加载Activity
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(ETsearch, 0);
            }
        }, 300);

        if (mHistoryData.size() > 0){
            TVclearHistory.setVisibility(View.VISIBLE);
            TVnoHistory.setVisibility(View.GONE);
            mHistoryFlowLayout.setVisibility(View.VISIBLE);
        }else{
            TVclearHistory.setVisibility(View.GONE);
            TVnoHistory.setVisibility(View.VISIBLE);
            mHistoryFlowLayout.setVisibility(View.GONE);
        }
//        initTagFlowLayout();
        //软键盘 搜索键 监听
        ETsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){

                    searchUiChange();
                    return true;
                }
                return false;
            }
        });
        ETsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())){
                    recyclerView.setVisibility(View.GONE);
                    emptyImg.setVisibility(View.GONE);

                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        TVclearHistory.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        initRecyclerView();
        initSpring();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.activity_search_clear_history:
                DataSupport.deleteAll(SearchHistoricalBean.class);
                mHistoryData.clear();
                TVnoHistory.setVisibility(View.VISIBLE);
                TVclearHistory.setVisibility(View.GONE);
                mHistoryFlowLayout.setVisibility(View.GONE);
                mHistoryFlowLayoutAdapter.notifyDataChanged();
                break;
            case R.id.search_btn:
                searchUiChange();
                break;
        }
    }

    private void search(String content){
        //关闭软键盘
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        recyclerView.setVisibility(View.GONE);
        emptyImg.setVisibility(View.GONE);
    }



    private void searchUiChange(){
        if (!TextUtils.isEmpty(ETsearch.getText().toString())){
            name = ETsearch.getText().toString();
            recyclerView.setVisibility(View.VISIBLE);
            getDepartmentDetailList();
        }
    }
    private void initRecyclerView() {

        DepartmentAdapter adapter = new DepartmentAdapter(R.layout.item_department,departmentDetailList,SearchDepartmentActivity.this);
        adapter.openLoadAnimation(ALPHAIN);
        adapter.isFirstOnly(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.item_department_layout:
                        Intent toDetail = new Intent(SearchDepartmentActivity.this, DepartmentDetailActivity.class);
                        String json=new Gson().toJson(departmentDetailList.get(position));
                        toDetail.putExtra("department",json);
                        startActivity(toDetail);
                        break;
                }
            }
        });
    }
//    public void getDepartmentDetailList() {
//
//        for(int i=0;i<20;i++){
//            departmentDetailList.add(new DepartmentDetailBean());
//        }
//
//    }
    public void getDepartmentDetailList() {

        String url = HttpUtil.HOME_PATH + HttpUtil.SEARCH_DEPART;

        if(type != 0){
            url=url+"/higher/"+type;
        }
        if(name != null){
            try {
                name = URLEncoder.encode(name, "utf-8");
                url=url+"/name/"+name;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        HttpUtil.sendOkHttpGetRequest(url, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchDepartmentActivity.this, "搜索失败!", Toast.LENGTH_SHORT).show();
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
                            departmentDetailList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<DepartmentDetailBean>>(){}.getType());
                            if(departmentDetailList.size()==0){
                                Toast.makeText(SearchDepartmentActivity.this, "未找到相关部门", Toast.LENGTH_SHORT).show();
                            }
                            initRecyclerView();
                        }catch (JSONException e){
                            Toast.makeText(SearchDepartmentActivity.this, "搜索失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }
    private void saveSearchHistoryBean(String name){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        SearchHistoricalBean searchHistoricalBean = new SearchHistoricalBean();
        searchHistoricalBean.setName(name);
        searchHistoricalBean.setTime(df.format(new Date()));
        List<SearchHistoricalBean> list = DataSupport.where("name = ?",name).find(SearchHistoricalBean.class);
        if (list.size()>0){
            searchHistoricalBean.updateAll("name=?",name);
        }else{
            searchHistoricalBean.save();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
