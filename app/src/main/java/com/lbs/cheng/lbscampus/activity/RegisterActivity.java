package com.lbs.cheng.lbscampus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lbs.cheng.lbscampus.util.HttpUtil;

import java.io.IOException;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";
    ImageView back;
    TextView titleName;
    @BindView(R.id.register)
    TextView register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("注册");
    }
    public void initListener(){
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.register:
                UserBean userBean=new UserBean();
                userBean.setUserId("2016111002");
                userBean.setUserName("张三");
                userBean.setPassword("123456");
                userBean.setSex("man");
                String json = new Gson().toJson(userBean);
                HttpUtil.sendOkHttpPutRequest( HttpUtil.HOME_PATH + HttpUtil.REGISTER, json, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "注册失败: ");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "注册成功: ");
                    }
                });
                finish();
                break;
            case R.id.title_back:
                finish();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        initListener();
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
    }
}
