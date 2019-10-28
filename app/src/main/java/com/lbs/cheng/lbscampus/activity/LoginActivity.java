package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.ShareTimeBean;
import com.lbs.cheng.lbscampus.bean.Staff;
import com.lbs.cheng.lbscampus.bean.Student;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lt.common.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class LoginActivity extends BaseActivity {
    ImageView back;
    TextView titleName;
    TextView Register;
    @BindView(R.id.account)
    EditText account;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.get_identify_code)
    TextView getIdentifyCode;
    @BindView(R.id.login)
    TextView login;
    @BindView(R.id.identify_or_password)
    TextView identifyOrPassword;
    @BindView(R.id.password_not)
    TextView passwordNot;
    @BindView(R.id.login_identify_code)
    TextView identifyCode;
    private int loginType = 1;//1是学号登录 2是手机号登录
    private int a = 1000000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        initListener();
        account.setText(CommonUtils.userID);
        password.setText(CommonUtils.password);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        identifyOrPassword.setText("手机验证码登录");
    }

    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        Register=findViewById(R.id.title_register);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        Register.setVisibility(View.VISIBLE);
        Register.setOnClickListener(this);
        titleName.setText("登录");
    }

    private void initListener() {
        getIdentifyCode.setOnClickListener(this);
        login.setOnClickListener(this);
        identifyOrPassword.setOnClickListener(this);
        passwordNot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.login:
                if(loginType == 1){
                    initUser();
                }else if(loginType == 2){
                    if (Integer.parseInt(passwordNot.getText().toString()) == a) {
                        Intent intent =  new Intent(LoginActivity.this,HomeActivity.class);
                        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        startActivity(intent);

                    }else{
                        Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.title_register:
                Intent intent = new Intent(this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.identify_or_password:
                if(identifyOrPassword.getText()=="手机验证码登录"){
                    getIdentifyCode.setVisibility(View.VISIBLE);
                    account.setHint("请输入手机号");
                    loginType = 2;
                    identifyOrPassword.setText("密码登录");
                }else{
                    account.setHint("请输入学号或职工号");
                    getIdentifyCode.setVisibility(View.GONE);
                    loginType = 1;
                    identifyOrPassword.setText("手机验证码登录");
                }
                break;
            case R.id.password_not:
                AlertDialog builder = new AlertDialog.Builder(this)
                        .setTitle("修改登录密码流程")
                        .setMessage("通过手机验证码登录->进入我的界面->修改登录密码")
                        .setPositiveButton("确定", null)
                        .create();
                builder.show();
                break;
            case R.id.get_identify_code:
                initUser();
                break;


        }
    }
    public void initUser(){

        List<String> list=new ArrayList<>();
        if(loginType == 1){
            String userId = account.getText().toString();
            String passWord = password.getText().toString();
            list.add(userId);
            list.add(passWord);
        }else if(loginType == 2){
            String telNum = account.getText().toString();
            list.add(telNum);
        }

        HashMap hashmap = new HashMap();

        String address=HttpUtil.GetUrl( HttpUtil.HOME_PATH + HttpUtil.LOG_IN,list);

        HttpUtil.sendOkHttpPostRequest(address, hashmap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("LoginActivity",e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "登陆失败，请检查网络!", Toast.LENGTH_SHORT).show();
                       // progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            final JSONObject jsonObject = new JSONObject(responseText);
                            Integer state = (Integer) jsonObject.get("status");
                            if (state == 1){
                                UserBean userBean = new Gson().fromJson(jsonObject.getJSONObject("user").toString(),UserBean.class);
                                userBean.saveThrows();
                                CommonUtils.password = userBean.getPassword();
                                List<UserBean> list= DataSupport.findAll(UserBean.class);
                                if(!jsonObject.get("shareTime").toString().equals("null")){
                                    ShareTimeBean shareTime = new Gson().fromJson(jsonObject.getJSONObject("shareTime").toString(),ShareTimeBean.class);
                                    shareTime.saveThrows();

                                }
                                if(userBean.getType() == 1){
                                    if(!jsonObject.get("student").toString().equals("null")){
                                        Student student = new Gson().fromJson(jsonObject.getJSONObject("student").toString(),Student.class);
                                        student.saveThrows();
                                        CommonUtils.userID = student.getStudentId();
                                    }

                                }else{
                                    if(!jsonObject.get("staff").toString().equals("null")){
                                        Staff staff = new Gson().fromJson(jsonObject.getJSONObject("staff").toString(),Staff.class);
                                        staff.saveThrows();
                                        CommonUtils.userID = staff.getStaffId();

                                    }

                                }

                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                                setResult(RESULT_OK);
                                if(list.size()!=0){
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                }

                                if(loginType == 1){
                                    Intent intent =  new Intent(LoginActivity.this,HomeActivity.class);
                                    intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }else if(loginType == 2){
                                    setCountDownTimer();
                                }

                            }else if(state == 2){
                                Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            Log.d("LoginActivity",e.toString());
                            Toast.makeText(LoginActivity.this, "登陆失败，请检查网络!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });




    }
//    public void initUser() {
//        UserBean user=new UserBean();
//        user.setUserId("2016111001");
//        user.setNickName("5bCP55m95Z+O");
//        user.setSex("man");
//        user.setEmail("2016111001@163.com");
//        user.save();
//    }

    private void setCountDownTimer() {
        identifyCode.setVisibility(View.VISIBLE);
        a = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;//产生1000-9999的随机数
        identifyCode.setText("验证码为:" + a);
        getIdentifyCode.setEnabled(false);
        getIdentifyCode.setTextColor(getResources().getColor(R.color.text_color_grey));
        new CountDownTimer(59000 + 50, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getIdentifyCode.setText("已发送(" + millisUntilFinished / 1000 + "秒)");
            }

            @Override
            public void onFinish() {
                if (account.getText().toString().length() == 11) {
                    getIdentifyCode.setEnabled(true);
                    getIdentifyCode.setTextColor(getResources().getColor(R.color.bottom_tab_text_selected_color));
                }
                getIdentifyCode.setText("获取验证码");
            }
        }.start();
    }



}
