package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.util.HttpUtil;

import butterknife.BindView;

public class IPActivity extends BaseActivity {

    ImageView back;
    TextView titleName;
    TextView Register;
    @BindView(R.id.ip_address)
    EditText ipAddress;
    @BindView(R.id.next)
    Button nextStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        nextStep.setOnClickListener(this);
    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        Register=findViewById(R.id.title_register);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("IP地址");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.next:
                if(!TextUtils.isEmpty(ipAddress.getText().toString())){
                    HttpUtil.setHomePath("http://"+ipAddress.getText().toString());
                    startActivity(new Intent(IPActivity.this,HomeActivity.class));
                    finish();
                }

                break;
        }
    }
}
