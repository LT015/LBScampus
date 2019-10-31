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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.adapter.BuildingAdapter;
import com.lbs.cheng.lbscampus.adapter.DepartmentAdapter;
import com.lbs.cheng.lbscampus.adapter.NoticeAdapter;
import com.lbs.cheng.lbscampus.adapter.PeopleAdapter;
import com.lbs.cheng.lbscampus.adapter.SearchAllAdapter;
import com.lbs.cheng.lbscampus.bean.BuildingBean;
import com.lbs.cheng.lbscampus.bean.DepartmentDetailBean;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;
import com.lbs.cheng.lbscampus.bean.UserDetailBean;
import com.lbs.cheng.lbscampus.bean.SearchHistoricalBean;
import com.lbs.cheng.lbscampus.bean.SectionBean;
import com.lt.common.util.HttpUtil;
import com.lbs.cheng.lbscampus.util.LocationUtil;
import com.lbs.cheng.lbscampus.util.SearchItem;
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

public class SearchAllActivity extends BaseActivity {

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
    @BindView(R.id.build_recycler)
    RecyclerView buildRecycleView;
    @BindView(R.id.department_recycler)
    RecyclerView departmentRecycleView;
    @BindView(R.id.people_recycler)
    RecyclerView peopleRecycleView;
    @BindView(R.id.empty_view)
    ImageView emptyImg;
    private String name = null; //搜索框输入的内容
    private List<String> data_list;
    List<SearchItem> resultList=new ArrayList<>();
    private TagAdapter<SearchHistoricalBean> mHistoryFlowLayoutAdapter;
    private List<String> mHotData;//热门推荐
    private List<SearchHistoricalBean> mHistoryData;
    private List<BuildingBean> buildingList=new ArrayList<>();
    private List<DepartmentDetailBean> departmentList=new ArrayList<>();
    private List<UserDetailBean> userList=new ArrayList<>();
    private int listPosition = 0;//选中的item的标号
    private int status = 0;//请求数据的状态监听  达到3时说明3组数据都返回了 再刷新recycleView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all);
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

        emptyImg.setVisibility(View.GONE);
    }



    private void searchUiChange(){
        if (!TextUtils.isEmpty(ETsearch.getText().toString())){
            name=ETsearch.getText().toString();

            getDataList();

        }
    }
    private void initRecyclerView() {

        BuildingAdapter buildingAdapter = new BuildingAdapter(R.layout.item_building,buildingList,0,SearchAllActivity.this);
        buildingAdapter.openLoadAnimation(ALPHAIN);
        buildingAdapter.isFirstOnly(false);
        buildRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        buildRecycleView.setAdapter(buildingAdapter);
        buildingAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
           @Override
           public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
               switch (view.getId()){
                   case R.id.item_building_go:
                       startNavigation(position);
                       break;
                   case R.id.building_item:
                       Intent toDetail = new Intent(SearchAllActivity.this, BuildingDetailActivity.class);
                       String json=new Gson().toJson(buildingList.get(position));
                       toDetail.putExtra("building",json);
                       startActivity(toDetail);
               }
           }
       });

        DepartmentAdapter departmentAdapter = new DepartmentAdapter(R.layout.item_department, departmentList,SearchAllActivity.this);
        departmentAdapter.openLoadAnimation(ALPHAIN);
        departmentAdapter.isFirstOnly(false);
        departmentRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        departmentRecycleView.setAdapter(departmentAdapter);
        departmentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.item_department_layout:
                        Intent toDetail = new Intent(SearchAllActivity.this, DepartmentDetailActivity.class);
                        String json=new Gson().toJson(departmentList.get(position));
                        toDetail.putExtra("department",json);
                        startActivity(toDetail);
                        break;
                }
            }
        });

        PeopleAdapter peopleAdapter = new PeopleAdapter(R.layout.item_people,userList,getApplicationContext());
        peopleAdapter.openLoadAnimation(ALPHAIN);
        peopleAdapter.isFirstOnly(false);
        peopleRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        peopleRecycleView.setAdapter(peopleAdapter);
        peopleAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.item_people_layout:
                        Intent toDetail = new Intent(SearchAllActivity.this, PeopleDetailActivity.class);
                        String json=new Gson().toJson(userList.get(position));
                        toDetail.putExtra("people",json);
                        startActivity(toDetail);
                        break;
                }
            }
        });
    }
//    public void getDataList() {
//        List<SearchItem> list = new ArrayList<>();
//        for(int i=0;i<5;i++){
//            list.add(new SearchItem(new BuildingBean(),1));
//            list.add(new SearchItem(new SectionBean(),2));
//            list.add(new SearchItem(new UserDetailBean(),3));
//
//        }
//
//    }
    public void getDataList(){
        resultList.clear();
        buildingList.clear();
        departmentList.clear();
        userList.clear();
        initRecyclerView();
        status = 0;
        try {
            name = URLEncoder.encode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getBuildingList();
        getDepartmentDetailList();
        getPeopleList();

    }
    public void getBuildingList() {
        String url = HttpUtil.HOME_PATH + HttpUtil.SEARCH_BUILDING;

        url=url+"/name/"+name;
        HttpUtil.sendOkHttpGetRequest(url, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchAllActivity.this, "网络连接失败!", Toast.LENGTH_SHORT).show();
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
                            buildingList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<BuildingBean>>(){}.getType());

                            status++;
                            if(status == 3){
                                initRecyclerView();
                            }

                        }catch (JSONException e){
                            Toast.makeText(SearchAllActivity.this, "搜索失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }
    public void getDepartmentDetailList() {

        String url = HttpUtil.HOME_PATH + HttpUtil.SEARCH_DEPART;

        url=url+"/name/"+name;
        HttpUtil.sendOkHttpGetRequest(url, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchAllActivity.this, "网络连接失败!", Toast.LENGTH_SHORT).show();
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
                            departmentList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<DepartmentDetailBean>>(){}.getType());

                            status++;
                            if(status == 3){
                                initRecyclerView();
                            }
                        }catch (JSONException e){
                            Toast.makeText(SearchAllActivity.this, "搜索失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }
    public void getPeopleList() {
        String url =  HttpUtil.HOME_PATH + HttpUtil.SEARCH_USER;


        url=url+"/name/"+name;
        HttpUtil.sendOkHttpGetRequest(url, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchAllActivity.this, "网络连接失败!", Toast.LENGTH_SHORT).show();
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
                            userList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<UserDetailBean>>(){}.getType());

                            status++;
                            if(status == 3){
                                initRecyclerView();
                            }
                        }catch (JSONException e){
                            Toast.makeText(SearchAllActivity.this, "搜索失败!", Toast.LENGTH_SHORT).show();
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
    void startNavigation(final int position){
        WalkNavigateHelper.getInstance().initNaviEngine(this, new IWEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                //引擎初始化成功的回调
                routeWalkPlanWithParam(position);
            }
            @Override
            public void engineInitFail() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"导航失败",Toast.LENGTH_LONG).show();
                    }
                });

                //引擎初始化失败的回调
            }
        });

    }
    void routeWalkPlanWithParam(int position){
        LatLng endPt;
        WalkNaviLaunchParam mParam;
        BuildingBean buildingBean = new BuildingBean();
        endPt = new LatLng(buildingList.get(position).getLatitude(), buildingList.get(position).getLongitude());
        mParam = new WalkNaviLaunchParam().stPt(LocationUtil.getMyLocation()).endPt(endPt);
        WalkNavigateHelper.getInstance().routePlanWithParams(mParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                //开始算路的回调
            }

            @Override
            public void onRoutePlanSuccess() {
                //算路成功
                //跳转至诱导页面
                Intent intent = new Intent(getApplicationContext(), WNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                //算路失败的回调
            }
        });
    }
}


