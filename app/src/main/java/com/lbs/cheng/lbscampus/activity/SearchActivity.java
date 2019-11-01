package com.lbs.cheng.lbscampus.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.adapter.NoticeAdapter;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;
import com.lbs.cheng.lbscampus.bean.SearchHistoricalBean;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lbs.cheng.lbscampus.util.SoftKeyboardUtils;
import com.lt.common.util.HttpUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.view.flowlayout.FlowLayout;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;

public class SearchActivity extends BaseActivity {
    @BindView(R.id.back_btn)
    ImageButton backBtn;
    @BindView(R.id.search_activity_hot_flowlayout)
    TagFlowLayout mHotFlowLayout;
    @BindView(R.id.search_activity_history_flowlayout)
    TagFlowLayout mHistoryFlowLayout;
    @BindView(R.id.search_et)
    EditText ETsearch;
    @BindView(R.id.activity_search_no_history)
    TextView TVnoHistory;
    @BindView(R.id.activity_search_clear_history)
    TextView TVclearHistory;
    @BindView(R.id.activity_search_hot_and_history_ll)
    AutoLinearLayout LLhotAndHistory;
    @BindView(R.id.search_btn)
    TextView searchBtn;
    @BindView(R.id.first_load)
    ProgressBar progressBar;
    @BindView(R.id.search_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    ImageView emptyImg;

    private TagAdapter<String> mHotFlowLayoutAdapter;
    private TagAdapter<SearchHistoricalBean> mHistoryFlowLayoutAdapter;
    private List<String> mHotData;//热门推荐
    private List<SearchHistoricalBean> mHistoryData;
    private List<NoticeDetailBean> list;
    private LinearLayoutManager linearLayoutManager;
    private  NoticeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void initData() {
        super.initData();
        list = new ArrayList<>();

        mHotData = new ArrayList<>();
        mHotData.add("考研");
        mHotData.add("U盘");
        mHotData.add("讲座");

        mHistoryData = DataSupport.where("type = 5").order("time desc").find(SearchHistoricalBean.class);
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
        initTagFlowLayout();
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
                    LLhotAndHistory.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.activity_search_clear_history:
                DataSupport.deleteAll(SearchHistoricalBean.class ,"type = 5");
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

    private void initTagFlowLayout(){
        mHotFlowLayoutAdapter = new TagAdapter<String>(mHotData){
            @Override
            public View getView(com.zhy.view.flowlayout.FlowLayout parent, int position, String s)
            {
                //将tv.xml文件填充到标签内
                TextView tv = (TextView) LayoutInflater.from(SearchActivity.this).inflate(R.layout.flow_layout_item,
                        mHotFlowLayout, false);
                //为标签设置对应的内容
                tv.setText(s);
                return tv;
            }
        };
        mHistoryFlowLayoutAdapter = new TagAdapter<SearchHistoricalBean>(mHistoryData) {
            @Override
            public View getView(FlowLayout parent, int position, SearchHistoricalBean searchHistoricalBean) {
                //将tv.xml文件填充到标签内
                TextView tv = (TextView) LayoutInflater.from(SearchActivity.this).inflate(R.layout.flow_layout_item,
                        mHistoryFlowLayout, false);
                //为标签设置对应的内容
                tv.setText(searchHistoricalBean.getName());
                return tv;
            }
        };

        //为热门标签设置点击事件
        mHotFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener()
        {
            @Override
            public boolean onTagClick(View view, int position, com.zhy.view.flowlayout.FlowLayout parent)
            {
                ETsearch.setText(mHotData.get(position));
                ETsearch.setSelection(ETsearch.getText().length());
                saveSearchHistoryBean(mHotData.get(position));
                mHistoryData.clear();
                mHistoryData.addAll(DataSupport.where("type = ", "5").order("time desc").find(SearchHistoricalBean.class));
                mHistoryFlowLayoutAdapter.notifyDataChanged();
                search(ETsearch.getText().toString());
                if (mHistoryFlowLayout.getVisibility() == View.GONE){
                    mHistoryFlowLayout.setVisibility(View.VISIBLE);
                    TVclearHistory.setVisibility(View.VISIBLE);
                    TVnoHistory.setVisibility(View.GONE);
                }
                return true;
            }
        });

        //为历史标签设置点击事件
        mHistoryFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener()
        {
            @Override
            public boolean onTagClick(View view, int position, com.zhy.view.flowlayout.FlowLayout parent)
            {
                ETsearch.setText(mHistoryData.get(position).getName());
                ETsearch.setSelection(ETsearch.getText().length());
                saveSearchHistoryBean(mHistoryData.get(position).getName());
                mHistoryData.clear();
                mHistoryData.addAll(DataSupport.where("type = ", "5").order("time desc").find(SearchHistoricalBean.class));
                mHistoryFlowLayoutAdapter.notifyDataChanged();
                search(ETsearch.getText().toString());
                return true;
            }
        });

        mHotFlowLayout.setAdapter(mHotFlowLayoutAdapter);
        mHistoryFlowLayout.setAdapter(mHistoryFlowLayoutAdapter);
    }

    private void search(String content){


        LLhotAndHistory.setVisibility(View.GONE);


        emptyImg.setVisibility(View.GONE);
//

        try {
            content = URLEncoder.encode(content, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url= HttpUtil.HOME_PATH + HttpUtil.GET_NOTICE_BY_TITLE+"/type/"+ CommonUtils.noticeTypeId+"/status/2"+"/title/"+content;
        HttpUtil.sendOkHttpGetRequest(url, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
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
                            list = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<NoticeDetailBean>>(){}.getType());

                            initRecyclerView();

                        }catch (JSONException e){

                            Toast.makeText(SearchActivity.this, "获取信息失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });





            }
        });
    }

    private void initRecyclerView(){
        linearLayoutManager = new LinearLayoutManager(this);
        adapter = new NoticeAdapter(R.layout.item_notice,list,SearchActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.openLoadAnimation(ALPHAIN);
        adapter.isFirstOnly(false);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent toContent = new Intent(SearchActivity.this, NoticeActivity.class);
                String json = new Gson().toJson(list.get(position));
                toContent.putExtra("noticeDetail",json);
                startActivity(toContent);
            }
        });
    }

    private void searchUiChange(){
        if (!TextUtils.isEmpty(ETsearch.getText().toString())){
            saveSearchHistoryBean(ETsearch.getText().toString());
            mHistoryData.clear();
            mHistoryData.addAll(DataSupport.where("type = 5").order("time desc").find(SearchHistoricalBean.class));
            mHistoryFlowLayoutAdapter.notifyDataChanged();
            search(ETsearch.getText().toString());
            if (mHistoryFlowLayout.getVisibility() == View.GONE){
                mHistoryFlowLayout.setVisibility(View.VISIBLE);
                TVclearHistory.setVisibility(View.VISIBLE);
                TVnoHistory.setVisibility(View.GONE);
            }
        }
    }

    private void saveSearchHistoryBean(String name){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        SearchHistoricalBean searchHistoricalBean = new SearchHistoricalBean();
        searchHistoricalBean.setName(name);
        searchHistoricalBean.setType(5);
        searchHistoricalBean.setTime(df.format(new Date()));
        List<SearchHistoricalBean> list = DataSupport.where("type = 5 and name = ?",name).find(SearchHistoricalBean.class);
        if (list.size()>0){
            searchHistoricalBean.updateAll("type = 5 and name=?",name);
        }else{
            searchHistoricalBean.save();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
