package com.lbs.cheng.lbscampus.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.adapter.FragmentAdapter;
import com.lbs.cheng.lbscampus.fragment.BuildingItemFragment;
import com.lbs.cheng.lbscampus.fragment.NoticeItemFragment;
import com.lbs.cheng.lbscampus.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.lbs.cheng.lbscampus.util.CommonUtils.buildingTypeId;

public class BuildingActivity extends BaseActivity {
    ImageView back;
    TextView titleName;
    private FragmentAdapter adapter;
    private List<Fragment> fragments;
    private List<String> strings;
    BuildingItemFragment fragment0 = new BuildingItemFragment();
    BuildingItemFragment fragment1 = new BuildingItemFragment();
    BuildingItemFragment fragment2 = new BuildingItemFragment();
    BuildingItemFragment fragment3 = new BuildingItemFragment();
    BuildingItemFragment fragment4 = new BuildingItemFragment();
    BuildingItemFragment fragment5 = new BuildingItemFragment();
    BuildingItemFragment fragment6 = new BuildingItemFragment();
    BuildingItemFragment fragment7 = new BuildingItemFragment();
    BuildingItemFragment fragment8 = new BuildingItemFragment();

    @BindView(R.id.activity_building_title)
    TabLayout title;
    @BindView(R.id.activity_building_viewpager)
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);
        CommonUtils.buildingTypeId = 1;
    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("建筑");
    }
    @Override
    protected void initData() {
        super.initData();
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
        fragments.add(fragment0);
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
        fragments.add(fragment4);
        fragments.add(fragment5);
        fragments.add(fragment6);
        fragments.add(fragment7);
        fragments.add(fragment8);

        strings=new ArrayList<>();
        strings.add("教学楼");
        strings.add("食堂");
        strings.add("图书馆");
        strings.add("超市");
        strings.add("公寓楼");
        strings.add("体育场馆");
        strings.add("行政管理");
        strings.add("景区");
        strings.add("出入口");
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
                buildingTypeId = position + 1;
                switch (position){
                    case 0:
                        fragment0.firstLoadData();
                        break;
                    case 1:
                        fragment1.firstLoadData();
                        break;
                    case 2:
                        fragment2.firstLoadData();
                        break;
                    case 3:
                        fragment3.firstLoadData();
                        break;
                    case 4:
                        fragment4.firstLoadData();
                        break;
                    case 5:
                        fragment5.firstLoadData();
                        break;
                    case 6:
                        fragment6.firstLoadData();
                        break;
                    case 7:
                        fragment7.firstLoadData();
                        break;
                    case 8:
                        fragment8.firstLoadData();
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
