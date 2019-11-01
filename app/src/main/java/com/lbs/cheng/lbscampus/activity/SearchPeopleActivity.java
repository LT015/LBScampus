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
import android.view.LayoutInflater;
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
import com.lbs.cheng.lbscampus.adapter.NoticeAdapter;
import com.lbs.cheng.lbscampus.adapter.PeopleAdapter;
import com.lbs.cheng.lbscampus.bean.BuildingBean;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;
import com.lbs.cheng.lbscampus.bean.UserDetailBean;
import com.lbs.cheng.lbscampus.bean.SearchHistoricalBean;
import com.lbs.cheng.lbscampus.util.CommonUtils;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;

public class SearchPeopleActivity extends BaseActivity {
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
    @BindView(R.id.activity_search_hot_and_history_ll)
    AutoLinearLayout LLhotAndHistory;
    @BindView(R.id.search_btn)
    TextView searchBtn;
    @BindView(R.id.search_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    ImageView emptyImg;
    @BindView(R.id.search_people_spinner_type1 )
    Spinner spinnerType1;
    @BindView(R.id.search_people_spinner_type2 )
    Spinner spinnerType2;
    private List<String> data_list_type1;
    private ArrayAdapter<String> arr_adapter_type1;
    private List<String> data_list_type2;
    private ArrayAdapter<String> arr_adapter_type2;
    private TagAdapter<SearchHistoricalBean> mHistoryFlowLayoutAdapter;
    private List<String> mHotData;//热门推荐
    private List<SearchHistoricalBean> mHistoryData;

    List<UserDetailBean> peopleList=new ArrayList<>();
    private List<String> type1 = new ArrayList<>();
    private List<String> type2 = new ArrayList<>();

    private String name = null;//搜索框输的学生名字
    private int type = 0;//查找人的类型 0是所有 1是学生 2是教职工
    private int deptId = -1;//院系ID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_people);
    }

    @Override
    protected void initData() {
        super.initData();
        mHistoryData = DataSupport.where("type = 3").order("time desc").find(SearchHistoricalBean.class);
    }

    void initSpring(){
        //数据
        data_list_type1 = new ArrayList<String>();
        data_list_type1.add("全体人员");
        data_list_type1.add("学生");
        data_list_type1.add("教职工");

        //适配器
        arr_adapter_type1= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list_type1);
        //设置样式
        arr_adapter_type1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinnerType1.setAdapter(arr_adapter_type1);
        spinnerType1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = i;
                switch (i){
                    case 0:
                        getData(0);
                        break;
                    case 1:
                        getData(1);
                        break;
                    case 2:
                        getData(2);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                getData(0);
            }

        });
        //数据



        getData(0);
        spinnerType2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                deptId = position - 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getData(int type){// 0 是所有 1是学生 2是老师

        String url = HttpUtil.HOME_PATH + HttpUtil.GET_DEPARTMENT_NAME + "/" + type;

        HttpUtil.sendOkHttpGetRequest(url, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            List<String> list2 = new ArrayList<>();
                            data_list_type2 = new ArrayList<>();
                            data_list_type2.add("全体部门");
                            final JSONArray jsonArray = new JSONArray(responseText);
                            list2 = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<String>>(){}.getType());
                            data_list_type2.addAll(list2);
                            setAdapter2();
                        }catch (JSONException e){
                            Toast.makeText(SearchPeopleActivity.this, "搜索失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void setAdapter2(){
        arr_adapter_type2= new ArrayAdapter<String>(SearchPeopleActivity.this, android.R.layout.simple_spinner_item, data_list_type2);
        //设置样式
        arr_adapter_type2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType2.setAdapter(arr_adapter_type2);
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
                DataSupport.deleteAll(SearchHistoricalBean.class ,"type = 3");
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

    private void initRecyclerView() {

        PeopleAdapter adapter = new PeopleAdapter(R.layout.item_people,peopleList,this);
        adapter.openLoadAnimation(ALPHAIN);
        adapter.isFirstOnly(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.item_people_layout:
                        Intent toDetail = new Intent(SearchPeopleActivity.this, PeopleDetailActivity.class);
                        String json=new Gson().toJson(peopleList.get(position));
                        toDetail.putExtra("people",json);
                        startActivity(toDetail);
                        break;
                }
            }
        });
    }

    public void getPeopleList() {
        String url = HttpUtil.HOME_PATH + HttpUtil.SEARCH_USER;

//        if(deptId == -1){
//            if(type!=-1){
//                url=url+"/type/"+type;
//            }
//
//        }else{
//            if(type == 0){
//                url = url+"/student/dept"+deptId;
//
//            }else if(type == 1){
//                url = url+"/staff/dept"+deptId;
//
//            }
//        }
        if(type != 0){
            url=url+"/type/"+type;
        }
        try {
            name = URLEncoder.encode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url=url+"/name/"+name;
        HttpUtil.sendOkHttpGetRequest(url, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchPeopleActivity.this, "网络连接失败!", Toast.LENGTH_SHORT).show();
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
                            peopleList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<UserDetailBean>>(){}.getType());
                            if(peopleList.size()==0){
                                Toast.makeText(SearchPeopleActivity.this, "未找到该用户", Toast.LENGTH_SHORT).show();
                            }
                            initRecyclerView();
                        }catch (JSONException e){
                            Toast.makeText(SearchPeopleActivity.this, "搜索失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });




    }

    private void searchUiChange(){
        if (!TextUtils.isEmpty(ETsearch.getText().toString())){
            saveSearchHistoryBean(ETsearch.getText().toString());
            mHistoryData.clear();
            mHistoryData.addAll(DataSupport.where("type = 3").order("time desc").find(SearchHistoricalBean.class));
            mHistoryFlowLayoutAdapter.notifyDataChanged();
            name = ETsearch.getText().toString();
            LLhotAndHistory.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            getPeopleList();
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
        searchHistoricalBean.setType(3);
        searchHistoricalBean.setTime(df.format(new Date()));
        List<SearchHistoricalBean> list = DataSupport.where("type = 3 and name = ?",name).find(SearchHistoricalBean.class);
        if (list.size()>0){
            searchHistoricalBean.updateAll("type = 3 and name=?",name);
        }else{
            searchHistoricalBean.save();
        }
    }
    private void initTagFlowLayout(){

        mHistoryFlowLayoutAdapter = new TagAdapter<SearchHistoricalBean>(mHistoryData) {
            @Override
            public View getView(FlowLayout parent, int position, SearchHistoricalBean searchHistoricalBean) {
                //将tv.xml文件填充到标签内
                TextView tv = (TextView) LayoutInflater.from(SearchPeopleActivity.this).inflate(R.layout.flow_layout_item,
                        mHistoryFlowLayout, false);
                //为标签设置对应的内容
                tv.setText(searchHistoricalBean.getName());
                return tv;
            }
        };

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
                mHistoryData.addAll(DataSupport.where("type = 3").order("time desc").find(SearchHistoricalBean.class));
                mHistoryFlowLayoutAdapter.notifyDataChanged();
                searchUiChange();
                return true;
            }
        });
        mHistoryFlowLayout.setAdapter(mHistoryFlowLayoutAdapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}
