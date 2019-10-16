package com.lbs.cheng.lbscampus.fragment;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.GlideLoader;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.activity.HelpActivity;
import com.lbs.cheng.lbscampus.activity.LostPropertyActivity;
import com.lbs.cheng.lbscampus.activity.NoticeActivity;
import com.lbs.cheng.lbscampus.activity.RoomStateActivity;
import com.lbs.cheng.lbscampus.activity.SearchBuildingActivity;
import com.lbs.cheng.lbscampus.activity.SearchPeopleActivity;
import com.lbs.cheng.lbscampus.activity.SearchDepartmentActivity;
import com.lbs.cheng.lbscampus.adapter.NoticeAdapter;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;
import com.lbs.cheng.lbscampus.bean.Student;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lt.common.util.HttpUtil;
import com.lbs.cheng.lbscampus.view.PullToRefreshView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;


/**
 * Created by cheng on 2019/1/15.
 */
@Route(path = "/app/home")
public class HomeFragment extends Fragment implements View.OnClickListener{
    private View view;
    @BindView(R.id.home_banner)
    Banner banner;
    @BindView(R.id.home_recyclerlistview)
    RecyclerView recyclerView;
    @BindView(R.id.home_refreshView)
    PullToRefreshView refreshView;
    @BindView(R.id.head_icon_1)
    AutoLinearLayout headIcon1;
    @BindView(R.id.head_icon_2)
    AutoLinearLayout headIcon2;
    @BindView(R.id.head_icon_3)
    AutoLinearLayout headIcon3;
    @BindView(R.id.head_icon_4)
    AutoLinearLayout headIcon4;
    @BindView(R.id.head_icon_5)
    AutoLinearLayout headIcon5;
    @BindView(R.id.head_icon_6)
    AutoLinearLayout headIcon6;
    @BindView(R.id.head_icon_7)
    AutoLinearLayout headIcon7;
    Unbinder unbinder;
    List<NoticeDetailBean> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_home, container, false);
            initData();
            initView();
        }
        return view;
    }//5B:A0:5D:C7:D1:A7:3C:E5:CF:E4:A9:5C:68:DD:F3:55:A8:BD:2B:BA 发布版SH1
    //9F:B2:D4:45:E4:C8:06:51:D6:24:A5:24:E8:B9:40:99:B4:38:EC:0F 开发版SH1


    private void initData() {
        unbinder = ButterKnife.bind(this, view);
        initBanner();
        initRecyclerView();
        getNoticeData();
        initRefreshView();
        initListener();
    }
    void initListener(){
        headIcon1.setOnClickListener(this);
        headIcon2.setOnClickListener(this);
        headIcon3.setOnClickListener(this);
        headIcon4.setOnClickListener(this);
        headIcon5.setOnClickListener(this);
        headIcon6.setOnClickListener(this);
        headIcon7.setOnClickListener(this);
    }


    private void initView() {


    }

    private void initBanner() {
        banner.setImageLoader(new GlideLoader());
//       List<String> images = new ArrayList<>();
//        images.add(HttpUtil.Banner+"1.jpg");
//        images.add(HttpUtil.Banner+"2.jpeg");
//        images.add(HttpUtil.Banner+"3.jpeg");
//        images.add(HttpUtil.Banner+"4.jpg");
        List<Integer> images=new ArrayList<>();
        images.add(R.mipmap.b1);
        images.add(R.mipmap.b2);
        images.add(R.mipmap.b3);
        images.add(R.mipmap.b4);

        banner.setImages(images);
        banner.isAutoPlay(true);
        banner.setDelayTime(5000);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置轮播样式（没有标题默认为右边,有标题时默认左边）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        setBannerClickListener();
        banner.start();
    }

    private void setBannerClickListener() {
        OnBannerListener onBannerListener = new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {

            }
        };
    }

    private void initRecyclerView() {
        NoticeAdapter adapter = new NoticeAdapter(R.layout.item_notice,list,getContext());
        adapter.openLoadAnimation(ALPHAIN);
        adapter.isFirstOnly(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent toContent = new Intent(getActivity(), NoticeActivity.class);
                String json = new Gson().toJson(list.get(position));
                toContent.putExtra("noticeDetail",json);
                startActivity(toContent);
            }
        });
    }

    public void getNoticeData() {
        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("status/2");
        HttpUtil.sendOkHttpGetRequest(HttpUtil.HOME_PATH + HttpUtil.GET_NOTICE_BY_TYPE, list1, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();

                try {
                    final JSONArray jsonArray = new JSONArray(responseText);
                    list = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<NoticeDetailBean>>(){}.getType());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initRecyclerView();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void initRefreshView() {
        refreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getNoticeData();
                        refreshView.setRefreshing(false);
                    }
                },1600);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.head_icon_1:
                Intent intent1 = new Intent(getActivity(), SearchBuildingActivity.class);
                startActivity(intent1);
                break;
            case R.id.head_icon_2:
                Intent intent2 = new Intent(getActivity(), SearchDepartmentActivity.class);
                startActivity(intent2);
                break;
            case R.id.head_icon_3:
                Intent intent3 = new Intent(getActivity(), SearchPeopleActivity.class);
                startActivity(intent3);
                break;
            case R.id.head_icon_4:
                Intent intent4 = new Intent(getActivity(), LostPropertyActivity.class);
                startActivity(intent4);
                break;
            case R.id.head_icon_5:
                Intent intent5 = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent5);
                break;
            case R.id.head_icon_6:
                UserBean userBean = DataSupport.findLast(UserBean.class);
                if(userBean.getType() == 1){
                    Student student = DataSupport.findLast(Student.class);
                    ARouter.getInstance().build("/course/main").withInt("key",1).withInt("classId",student.getClassId()).withString("courseName","").navigation();
                }else{
                    ARouter.getInstance().build("/course/selectroom").navigation();
                }
                break;
            case R.id.head_icon_7:
                UserBean user = DataSupport.findLast(UserBean.class);
                if(user.getType() == 1){
                    Intent intent =new Intent(getActivity(), RoomStateActivity.class);
                    intent.putExtra("key",0);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"无常用教室",Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.head_icon_8:
                Toast.makeText(getContext(),"敬请期待",Toast.LENGTH_LONG).show();
                break;
        }
    }
//    public void getNoticeData() {
//        list = new ArrayList<>();
//        for(int i=0;i<20;i++){
//            list.add(new NoticeDetailBean());
//        }
//        initRecyclerView();
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
