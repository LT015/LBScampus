package com.lbs.cheng.lbscampus.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.adapter.FragmentAdapter;
import com.lbs.cheng.lbscampus.adapter.NoticeAdapter;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.fragment.FinishFragment;
import com.lbs.cheng.lbscampus.fragment.NoticeItemFragment;
import com.lbs.cheng.lbscampus.fragment.VerifyFragment;
import com.lbs.cheng.lbscampus.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;
import static com.lbs.cheng.lbscampus.util.CommonUtils.buildingTypeId;

public class VerifyActivity extends BaseActivity {

    ImageView back;
    TextView titleName;
    private FragmentAdapter adapter;
    private List<Fragment> fragments;
    private List<String> strings;
    private VerifyFragment verifyFragment = new VerifyFragment();
    private FinishFragment finishFragment = new FinishFragment();
    @BindView(R.id.fragment_verify_title)
    TabLayout title;
    @BindView(R.id.fragment_verify_viewpager)
    ViewPager viewPager;
    private int role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("审核公告");
    }
    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        role = intent.getIntExtra("role",0);
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        initFragment();

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
    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(verifyFragment);
        fragments.add(finishFragment);
        strings=new ArrayList<>();
        strings.add("审核");
        strings.add("完结");
        //添加导航栏文字
        for (String str : strings) {
            title.addTab(title.newTab().setText(str));
        }

        //设置导航栏的fragment
        adapter = new FragmentAdapter(getSupportFragmentManager(), fragments, strings);
        viewPager.setAdapter(adapter);
        title.setupWithViewPager(viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                CommonUtils.verifyNoticeTypeId = position;
//                switch (position){
////                    case 0:
////                        verifyFragment.getNoticeData();
////                        break;
////                    case 1:
////                        finishFragment.getNoticeData();
////                        break;
//
//
//
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
