package com.lbs.cheng.lbscampus.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TextView;

import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.activity.AddNoticeActivity;
import com.lbs.cheng.lbscampus.activity.MyNoticeActivity;
import com.lbs.cheng.lbscampus.adapter.FragmentAdapter;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lbs.cheng.lbscampus.view.PullToRefreshView;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by cheng on 2019/1/15.
 */

public class NoticeFragment extends Fragment implements View.OnClickListener{

    private View view;
    private FragmentAdapter adapter;
    private NoticeItemFragment noticeFragment = new NoticeItemFragment();;
    private NoticeItemFragment activityFragment = new NoticeItemFragment();;
    private NoticeItemFragment lpFragment = new NoticeItemFragment();;
    private NoticeItemFragment helpFragment = new NoticeItemFragment();;
    private List<Fragment> fragments = new ArrayList<>();;
    private List<String> strings;
    @BindView(R.id.fragment_notice_title)
    TabLayout title;
    @BindView(R.id.fragment_notice_viewpager)
    ViewPager viewPager;
    @BindView(R.id.add_notice)
    ImageView addNotice;
    PopupWindow popupWindow;
    TextView popupCancle;
    TextView popupConfirm;

    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_notice, container, false);
            initData();
            initView();
        }
        return view;
    }

    private void initData() {
        unbinder = ButterKnife.bind(this, view);
        addNotice.setOnClickListener(this);

    }

    private void initFragment() {

        fragments.add(noticeFragment);
        fragments.add(activityFragment);
        fragments.add(lpFragment);
        fragments.add(helpFragment);
        strings=new ArrayList<>();
        strings.add("通知");
        strings.add("活动");
        strings.add("失物招领");
        strings.add("求助");
        //添加导航栏文字
        for (String str : strings) {
            title.addTab(title.newTab().setText(str));
        }

        //设置导航栏的fragment
        adapter = new FragmentAdapter(getActivity().getSupportFragmentManager(), fragments, strings);
        viewPager.setAdapter(adapter);
        title.setupWithViewPager(viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                CommonUtils.noticeTypeId = position + 1;
//                if(position == 0){
//                    noticeFragment.getNoticeData();
//                }else if(position == 1){
//                    activityFragment.getNoticeData();
//                }else if(position == 2){
//                    lpFragment.getNoticeData();
//                }else{
//                    helpFragment.getNoticeData();
//                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initView() {
        initFragment();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_notice:
                showShadow();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
    private void showShadow() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopupWindow = inflater.inflate(R.layout.popup_add, null, false);//引入弹窗布局
        popupWindow = new PopupWindow(vPopupWindow, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

        //设置背景透明
        addBackground();

        //引入依附的布局
        View parentView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_add, null);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        popupConfirm = (vPopupWindow.findViewById(R.id.popup_add_confirm));
        popupCancle = (vPopupWindow.findViewById(R.id.popup_add_cancel));
        popupConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.noticeEditType = 0;
                Intent toAdd = new Intent(getActivity(),AddNoticeActivity.class);
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
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.7f;//调节透明度
        getActivity().getWindow().setAttributes(lp);
        //dismiss时恢复原样
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
    }
}
