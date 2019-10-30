package com.lbs.cheng.lbscampus.activity;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lt.common.util.HttpUtil;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AlterPhoneActivity extends BaseActivity {
    //标题栏
    ImageView back;
    TextView titleName;
    //旧手机号
    @BindView(R.id.old_phone_ll)
    AutoLinearLayout oldPhoneLayout;
    @BindView(R.id.old_phone_tv)
    TextView oldPhone;
    @BindView(R.id.get_old_identify_code)
    TextView getOldIdentify;
    @BindView(R.id.old_identify_code)
    EditText oldIdentifyCode;
    //新手机号
    @BindView(R.id.new_phone_ll)
    AutoLinearLayout newPhoneLayout;
    @BindView(R.id.get_new_identify_code)
    TextView getNewIdentify;
    @BindView(R.id.new_phone_et)
    EditText newPhoneEt;
    @BindView(R.id.new_identify_code)
    EditText newIdentifyCode;

    @BindView(R.id.identify_btn)
    Button identifyBtn;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;

    @BindView(R.id.identify_code)
    TextView identifyCode;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private String newTelNumber=null;
    private int a;
    private int b;
    CountDownTimer oldCountDownTimer;
    CountDownTimer newCountDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AlterPhone", "onCreate: ");
        setContentView(R.layout.activity_alter_phone);
    }

    @Override
    protected void initView() {
        Log.d("AlterPhone", "initView: ");
        initTitle();
        oldPhone.setText(getIntent().getStringExtra("phone"));
        getOldIdentify.setOnClickListener(this);
        identifyBtn.setOnClickListener(this);
        getNewIdentify.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

        oldIdentifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!oldIdentifyCode.getText().toString().equals("")) {
                    identifyBtn.setEnabled(true);
                    identifyBtn.setBackground(getResources().getDrawable(R.drawable.border_blue));
                }else{
                    identifyBtn.setEnabled(false);
                    identifyBtn.setBackground(getResources().getDrawable(R.drawable.round_gray_rectangle));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newPhoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!newPhoneEt.getText().toString().equals("") && !newIdentifyCode.getText().toString().equals("")){
                    confirmBtn.setEnabled(true);
                    confirmBtn.setBackground(getResources().getDrawable(R.drawable.border_blue));
                }else{
                    confirmBtn.setEnabled(false);
                    confirmBtn.setBackground(getResources().getDrawable(R.drawable.round_gray_rectangle));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (newPhoneEt.getText().toString().length() == 11 && getNewIdentify.getText().toString().equals("获取验证码")) {
                    getNewIdentify.setTextColor(getResources().getColor(R.color.bg_blue));
                    getNewIdentify.setEnabled(true);
                } else {
                    getNewIdentify.setEnabled(false);
                    getNewIdentify.setTextColor(getResources().getColor(R.color.text_color_grey));
                }
            }
        });

        newIdentifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!newPhoneEt.getText().toString().equals("") && !newIdentifyCode.getText().toString().equals("")){
                    confirmBtn.setEnabled(true);
                    confirmBtn.setBackground(getResources().getDrawable(R.drawable.border_blue));
                }else{
                    confirmBtn.setEnabled(false);
                    confirmBtn.setBackground(getResources().getDrawable(R.drawable.round_gray_rectangle));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("换绑手机");
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_back:
                if(oldPhoneLayout.getVisibility() == View.VISIBLE){
                    if(oldCountDownTimer != null){
                        oldCountDownTimer.cancel();
                    }
                    finish();
                }else{
                    if(newCountDownTimer != null){
                        newCountDownTimer.cancel();
                    }
                    oldPhoneLayout.setVisibility(View.VISIBLE);
                    newPhoneLayout.setVisibility(View.GONE);
                    identifyCode.setVisibility(View.GONE);
                    identifyBtn.setVisibility(View.VISIBLE);
                    confirmBtn.setVisibility(View.GONE);
                }
                break;
            case R.id.get_old_identify_code://获取旧手机的验证码
                setOldCountDownTimer();
                break;
            case R.id.get_new_identify_code://获取新手机的验证码
                if (newPhoneEt.getText().toString().equals(DataSupport.findAll(UserBean.class).get(0).getTelNumber())){
                    Toast.makeText(this, "新手机号不能与老手机号相同!", Toast.LENGTH_SHORT).show();
                }else{
                    setNewCountDownTimer();
                }
                break;
            case R.id.identify_btn://验证后绑定新手机号
                if (Integer.parseInt(oldIdentifyCode.getText().toString()) == a){
                   oldPhoneLayout.setVisibility(View.GONE);
                   identifyCode.setVisibility(View.GONE);
                   newPhoneLayout.setVisibility(View.VISIBLE);
                   identifyBtn.setVisibility(View.GONE);
                   confirmBtn.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(this, "验证码错误!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.confirm_btn:
                if (Integer.parseInt(newIdentifyCode.getText().toString()) == b){
                    changePhone();
                }else{
                    Toast.makeText(this, "验证码错误!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void changePhone(){
        progressBar.setVisibility(View.VISIBLE);
        UserBean user=DataSupport.findLast(UserBean.class);
        String userId = user.getUserId();
        newTelNumber = newPhoneEt.getText().toString();
        HashMap<String,String> hash = new HashMap<>();
        hash.put("user_id",userId);
        hash.put("phone",newTelNumber);
        String url= HttpUtil.HOME_PATH + HttpUtil.UPDATE_TEL_NUM+"/"+userId+"/"+newTelNumber;
        HttpUtil.sendOkHttpPostRequest(url ,hash, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AlterPhoneActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText  = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (responseText.equals(newTelNumber)){
                            UserBean userBean = DataSupport.findLast(UserBean.class);
                            userBean.setTelNumber(newTelNumber);
                            userBean.save();
                            Toast.makeText(AlterPhoneActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            finish();
                        }else{       //返回错误信息
                            Toast.makeText(AlterPhoneActivity.this, "修改失败!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
            }
        });

    }

    private void setOldCountDownTimer() {
        identifyCode.setVisibility(View.VISIBLE);
        a = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;//产生1000-9999的随机数
        identifyCode.setText("验证码为:" + a);
        getOldIdentify.setEnabled(false);
        getOldIdentify.setTextColor(getResources().getColor(R.color.text_color_grey));
        oldCountDownTimer = new CountDownTimer(59000 + 50, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!AlterPhoneActivity.this.isFinishing()){
                    if(oldPhoneLayout.getVisibility() == View.VISIBLE){
                        getOldIdentify.setText("已发送(" + millisUntilFinished / 1000 + "秒)");
                    }
                }
            }
            @Override
            public void onFinish() {
                if(!AlterPhoneActivity.this.isFinishing()){
                    if(oldPhoneLayout.getVisibility() == View.VISIBLE){
                        getOldIdentify.setTextColor(getResources().getColor(R.color.text_select_color));
                        getOldIdentify.setText("获取验证码");
                        getOldIdentify.setEnabled(true);
                    }
                }

            }
        }.start();
    }

    private void setNewCountDownTimer() {
        identifyCode.setVisibility(View.VISIBLE);
        b = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;//产生1000-9999的随机数
        identifyCode.setText("验证码为:" + b);
        getNewIdentify.setEnabled(false);
        getNewIdentify.setTextColor(getResources().getColor(R.color.text_color_grey));
        newCountDownTimer = new CountDownTimer(59000 + 50, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!AlterPhoneActivity.this.isFinishing()){
                    if(newPhoneLayout.getVisibility() == View.VISIBLE){
                        getNewIdentify.setText("已发送(" + millisUntilFinished / 1000 + "秒)");
                    }

                }
            }

            @Override
            public void onFinish() {
                if(!AlterPhoneActivity.this.isFinishing()){
                    if(newPhoneLayout.getVisibility() == View.VISIBLE){
                        getNewIdentify.setTextColor(getResources().getColor(R.color.text_select_color));
                        getNewIdentify.setText("获取验证码");
                        getNewIdentify.setEnabled(true);
                    }
                }

            }
        }.start();
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(oldPhoneLayout.getVisibility() == View.VISIBLE){
            if(oldCountDownTimer != null){
                oldCountDownTimer.cancel();
            }
            finish();
        }else{
            if(newCountDownTimer != null){
                newCountDownTimer.cancel();
            }
            oldPhoneLayout.setVisibility(View.VISIBLE);
            newPhoneLayout.setVisibility(View.GONE);
            identifyCode.setVisibility(View.GONE);
            identifyBtn.setVisibility(View.VISIBLE);
            confirmBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(oldCountDownTimer != null){
            oldCountDownTimer.cancel();
        }
        if(newCountDownTimer != null){
            newCountDownTimer.cancel();
        }
    }
}

