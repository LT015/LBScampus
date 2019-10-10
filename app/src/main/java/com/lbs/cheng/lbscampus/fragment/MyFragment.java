package com.lbs.cheng.lbscampus.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.activity.CollectNoticeActivity;
import com.lbs.cheng.lbscampus.activity.LoginActivity;
import com.lbs.cheng.lbscampus.activity.MyNoticeActivity;
import com.lbs.cheng.lbscampus.activity.SelectInterstActivity;
import com.lbs.cheng.lbscampus.activity.TimeIntervalActivity;
import com.lbs.cheng.lbscampus.activity.UserActivity;
import com.lbs.cheng.lbscampus.activity.VerifyActivity;
import com.lbs.cheng.lbscampus.bean.ShareTimeBean;
import com.lbs.cheng.lbscampus.bean.Staff;
import com.lbs.cheng.lbscampus.bean.Student;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lbs.cheng.lbscampus.util.GlideUtil;
import com.lbs.cheng.lbscampus.util.HttpUtil;
import com.lbs.cheng.lbscampus.view.CircleImageView;
import com.zhy.autolayout.AutoLinearLayout;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by cheng on 2019/1/15.
 */

public class MyFragment extends Fragment implements View.OnClickListener{

    private View view;
    TextView title;
    Unbinder unbinder;
    @BindView(R.id.fragment_my_login)
    AutoLinearLayout login;
    @BindView(R.id.my_image)
    CircleImageView myImage;
    @BindView(R.id.my_name)
    TextView myName;//
    @BindView(R.id.fragment_my_point)
    TextView point;//
    @BindView(R.id.my_notice)
    AutoLinearLayout myNotice;//我的公告
    @BindView(R.id.collect_notice)
    AutoLinearLayout collectNotice;//我的收藏
    @BindView(R.id.select_interst)
    AutoLinearLayout selectInterset;//编辑爱好
    @BindView(R.id.share_mode)
    AutoLinearLayout shareMode;//共享模式
    @BindView(R.id.share_time)
    AutoLinearLayout shareTime;//共享时段
    @BindView(R.id.verify)
    AutoLinearLayout verify;//审核公告
    @BindView(R.id.drop_out)
    TextView dropOut;//

    private UserBean userBean;
    private int statusId;//选定的状态id


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_my, container, false);
            initData();
            initView();
            initListener();
        }
        return view;
    }
    public void initUser(){

        userBean= DataSupport.findLast(UserBean.class);
        if (userBean!=null ){
            Log.d("lt", "initUser: ");
            if (userBean.getUserImage()==null){
                myImage.setImageResource(R.mipmap.head_img);

            }else{
                String path = HttpUtil.HOME_PATH + HttpUtil.Image + "user/"+userBean.getUserImage();
                //GlideUtil.REQUEST_OPTIONS.signature(new ObjectKey(System.currentTimeMillis()));//签名
                GlideUtil.load(getContext(), path, myImage, GlideUtil.REQUEST_OPTIONS);
            }

            myName.setText(userBean.getNickName());
            point.setVisibility(View.VISIBLE);
            //管理员权限
            if (userBean.getType() == 2){
                verify.setVisibility(View.VISIBLE);
            }else{
                verify.setVisibility(View.GONE);
            }
        }else{
            myImage.setImageResource(R.mipmap.no_image);
            myName.setText("点击登录");
            point.setVisibility(View.INVISIBLE);
        }
    }
    private void initData() {
        unbinder = ButterKnife.bind(this, view);
    }
    private void initView() {
        title=view.findViewById(R.id.title_name);
        title.setText("我的");
        initUser();
    }

    private void initListener() {
        login.setOnClickListener(this);
        myNotice.setOnClickListener(this);
        collectNotice.setOnClickListener(this);
        selectInterset.setOnClickListener(this);
        shareMode.setOnClickListener(this);
        shareTime.setOnClickListener(this);
        verify.setOnClickListener(this);
        dropOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        UserBean user=DataSupport.findLast(UserBean.class);
        if (user == null){
            switch (v.getId()){
                //跳转到登录页
                case R.id.fragment_my_login:
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    break;
                case R.id.my_notice:
                case R.id.collect_notice:
                case R.id.select_interst:
                case R.id.share_mode:
                case R.id.share_time:
                case R.id.drop_out:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),"尚未登录",Toast.LENGTH_SHORT).show();;
                        }
                    });
                    break;

            }
        }else{
            switch(v.getId()){
                case R.id.drop_out:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myImage.setImageResource(R.mipmap.no_image);
                            myName.setText("点击登录");
                            point.setVisibility(View.INVISIBLE);
                            DataSupport.deleteAll(UserBean.class);
                            DataSupport.deleteAll(ShareTimeBean.class);
                            DataSupport.deleteAll(Student.class);
                            DataSupport.deleteAll(Staff.class);
                            Toast.makeText(getContext(),"成功退出",Toast.LENGTH_SHORT).show();;
                        }
                    });
                    break;
                case R.id.fragment_my_login:
                    Intent intent=new Intent(getActivity(), UserActivity.class);
                    startActivity(intent);
                    break;
                case R.id.my_notice:
                    Intent my_notice = new Intent(getActivity(), MyNoticeActivity.class);
                    startActivity(my_notice);
                    break;
                case R.id.collect_notice:
                    Intent collect_notice = new Intent(getActivity(), CollectNoticeActivity.class);
                    startActivity(collect_notice);
                    break;
                case R.id.select_interst:
                    Intent select_interst = new Intent(getActivity(), SelectInterstActivity.class);
                    startActivity(select_interst);
                    break;
                case R.id.share_mode:
                    alterUserShareMode();
                    break;
                case R.id.share_time:
                    Intent shareTime = new Intent(getActivity(), TimeIntervalActivity.class);
//                    shareTime.putExtra("startTime",userBean.get)
                    startActivity(shareTime);
                    break;
                case R.id.verify:
                    Intent verify = new Intent(getActivity(), VerifyActivity.class);
                    startActivity(verify);
                    break;
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        initUser();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
    private void alterUserShareMode(){
        final View viewStatus = LayoutInflater.from(getContext()).inflate(R.layout.alter_mode,null);
        final RadioGroup selected = (RadioGroup) viewStatus.findViewById(R.id.radio_group_mode);
        RadioButton hide = (RadioButton) viewStatus.findViewById(R.id.hide);
        RadioButton open = (RadioButton) viewStatus.findViewById(R.id.open);
        UserBean user= DataSupport.findLast(UserBean.class);
        final int userStatus=user.getStatus();
        if (userStatus == 0){
            hide.setChecked(true);
        }else{
            open.setChecked(true);
        }
        new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.user_mode))
                .setView(viewStatus)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RadioButton rb = (RadioButton) viewStatus.findViewById(selected.getCheckedRadioButtonId());
                        final String status = rb.getText().toString();

//
                        //将修改保存到本地数据库
                        UserBean user1 = DataSupport.findLast(UserBean.class);
                        if (status.equals("隐身")){
                            statusId=0;
                        }else{
                            statusId=1;
                        }
                       if(userStatus != statusId){
                           HashMap<String,String> hash = new HashMap<>();
                           hash.put("user_id",user1.getUserId());
                           hash.put("user_status",status);
                           String url= HttpUtil.HOME_PATH + HttpUtil.UPDATE_USER_STATUS+"/"+user1.getUserId()+"/"+statusId;
                           HttpUtil.sendOkHttpPostRequest(url, hash, new Callback() {
                               @Override
                               public void onFailure(Call call, IOException e) {
                                   getActivity().runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           Toast.makeText(getActivity(), "修改失败", Toast.LENGTH_SHORT).show();
                                       }
                                   });
                               }

                               @Override
                               public void onResponse(Call call, Response response) throws IOException {
                                   final String responseText = response.body().string();
                                   getActivity().runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {

                                           UserBean user = DataSupport.findLast(UserBean.class);
                                           user.setStatus(statusId);
                                           user.saveThrows();
                                           Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                                       }
                                   });
                               }
                           });
                       }



                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .show();
    }
}
