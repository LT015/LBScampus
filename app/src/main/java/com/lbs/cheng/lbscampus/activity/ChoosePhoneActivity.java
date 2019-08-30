package com.lbs.cheng.lbscampus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbs.cheng.lbscampus.R;

import butterknife.BindView;

public class ChoosePhoneActivity extends BaseActivity {
    ImageView back;
    TextView titleName;
    @BindView(R.id.register)
    TextView register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_phone);
    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("绑定手机");
    }
    public void initListener(){
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.register:
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
