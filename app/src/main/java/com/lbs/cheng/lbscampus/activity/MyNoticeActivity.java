package com.lbs.cheng.lbscampus.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
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
import com.lbs.cheng.lbscampus.fragment.DraftFragment;
import com.lbs.cheng.lbscampus.fragment.FinishFragment;
import com.lbs.cheng.lbscampus.fragment.PublishedFragment;
import com.lbs.cheng.lbscampus.fragment.ReviewedFragment;
import com.lbs.cheng.lbscampus.fragment.VerifyFragment;
import com.lbs.cheng.lbscampus.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;
import static com.lbs.cheng.lbscampus.util.CommonUtils.buildingTypeId;

public class MyNoticeActivity extends BaseActivity {
    PopupWindow popupWindow;
    TextView popupCancle;
    TextView popupConfirm;
    @BindView(R.id.title_add)
    ImageView add;
    ImageView back;
    TextView titleName;
    private FragmentAdapter adapter;
    private List<Fragment> fragments;
    private DraftFragment draftFragment =new DraftFragment();
    private ReviewedFragment reviewedFragment= new ReviewedFragment();
    private PublishedFragment publishedFragment = new PublishedFragment();


    private List<String> strings;
    @BindView(R.id.fragment_mynotice_title)
    TabLayout title;
    @BindView(R.id.fragment_mynotice_viewpager)
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notice);
    }

    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("我的公告");
        add.setOnClickListener(this);
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
            case R.id.title_add:
                showShadow();
                break;

        }
    }
    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(draftFragment);
        fragments.add(reviewedFragment);
        fragments.add(publishedFragment);
        strings=new ArrayList<>();
        strings.add("草稿");
        strings.add("审核中");
        strings.add("发布");
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
//                CommonUtils.myNoticeTypeId = position;
//                switch (position){
//                    case 0:
//                        draftFragment.getNoticeData();
//                        break;
//                    case 1:
//                        reviewedFragment.getNoticeData();
//                        break;
//                    case 2:
//                        publishedFragment.getNoticeData();
//                        break;
//
//
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void showShadow() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopupWindow = inflater.inflate(R.layout.popup_add, null, false);//引入弹窗布局
        popupWindow = new PopupWindow(vPopupWindow, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

        //设置背景透明
        addBackground();

        //引入依附的布局
        View parentView = LayoutInflater.from(MyNoticeActivity.this).inflate(R.layout.popup_add, null);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        popupConfirm = (vPopupWindow.findViewById(R.id.popup_add_confirm));
        popupCancle = (vPopupWindow.findViewById(R.id.popup_add_cancel));
        popupConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toAdd = new Intent(MyNoticeActivity.this,AddNoticeActivity.class);
                CommonUtils.noticeEditType = 0;
                startActivity(toAdd);
                popupWindow.dismiss();
            }
        });
        popupCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }
    private void addBackground() {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;//调节透明度
        getWindow().setAttributes(lp);
        //dismiss时恢复原样
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }
}
