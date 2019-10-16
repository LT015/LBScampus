package com.example.kcb;

import android.content.Intent;
import android.support.annotation.BinderThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.kcb.adapter.SortAdapter;
import com.example.kcb.bean.ClassBean;
import com.example.kcb.bean.SortModel;
import com.example.kcb.util.PinyinComparator;
import com.example.kcb.util.PinyinUtils;
import com.example.kcb.view.ClearEditText;
import com.example.kcb.view.SideBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lt.common.activity.BaseActivity;
import com.lt.common.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;
@Route(path = "/course/selectroom")
public class SelectClassActivity extends BaseActivity {


    ImageView back;
    TextView titleName;
    ImageView titleInfo;
    TextView titleMore;
    private RecyclerView mRecyclerView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;
    private ArrayList<ClassBean> courseList = new ArrayList<>();
    LinearLayoutManager manager;

    private List<SortModel> SourceDateList;

    /**
     * 根据拼音来排列RecyclerView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_class);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        initTitle();
        getClassList();

    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName = findViewById(R.id.title_name);
        titleInfo = findViewById(R.id.title_info);
        back.setOnClickListener(this);
        titleInfo.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleInfo.setVisibility(View.VISIBLE);
        titleName.setText("选择班级");
    }

    private void initRecycleView(){
        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sideBar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧SideBar触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
                }

            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        SourceDateList = filledData(courseList);

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        //RecyclerView社置manager
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        adapter = new SortAdapter(this, SourceDateList);
        mRecyclerView.setAdapter(adapter);
        //item点击事件
        adapter.setOnItemClickListener(new SortAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.title_back) {
            finish();
        }
    }

    /**
     * 为RecyclerView填充数据
     *
     * @return
     */
    private List<SortModel> filledData(ArrayList<ClassBean> list) {

        List<SortModel> mSortList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setId(list.get(i).getClassId());
            sortModel.setName(list.get(i).getClassName());
            //汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(list.get(i).getClassName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setLetters(sortString.toUpperCase());
            } else {
                sortModel.setLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新RecyclerView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 ||
                        PinyinUtils.getFirstSpell(name).startsWith(filterStr.toString())
                        //不区分大小写
                        || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr.toString())
                        || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr.toString())
                ) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateList(filterDateList);
    }
    public void getClassList(){  //通过网络请求得到所有标签
        HttpUtil.sendOkHttpGetRequest( HttpUtil.HOME_PATH + HttpUtil.GET_CLASS_LIST, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SelectClassActivity.this, "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
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
                            courseList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<ClassBean>>(){}.getType());
                            initRecycleView();

                        }catch (JSONException e){
                            Log.d("LoginActivity",e.toString());
                            Toast.makeText(SelectClassActivity.this, "获取信息失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}
