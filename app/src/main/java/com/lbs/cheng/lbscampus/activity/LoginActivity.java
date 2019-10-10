package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.lbs.cheng.lbscampus.util.HttpUtil;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initData() {
        super.initData();
        initTitle();
        initListener();
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
                initUser();
                List<UserBean> list= DataSupport.findAll(UserBean.class);
                Log.d("login success", "onClick: ");
                if(list.size()!=0){
                    finish();
                }
                break;
            case R.id.title_register:
                Intent intent = new Intent(this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.identify_or_password:
                if(identifyOrPassword.getText()=="手机验证码登录"){
                    getIdentifyCode.setVisibility(View.VISIBLE);
                    identifyOrPassword.setText("密码登录");
                }else{
                    getIdentifyCode.setVisibility(View.GONE);
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



        }
    }
    public void initUser(){
        String userId=account.getText().toString();
        String passWord=password.getText().toString();
        HashMap hashmap=new HashMap();
        hashmap.put("userId",userId);
        hashmap.put("password",passWord);

        List<String> list=new ArrayList<>();
        list.add(userId);
        list.add(passWord);

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
                                List<UserBean> list= DataSupport.findAll(UserBean.class);
                                if(!jsonObject.get("shareTime").toString().equals("null")){
                                    ShareTimeBean shareTime = new Gson().fromJson(jsonObject.getJSONObject("shareTime").toString(),ShareTimeBean.class);
                                    shareTime.saveThrows();
                                    ShareTimeBean shareTime1= DataSupport.findLast(ShareTimeBean.class);
                                    Log.d("login", "run: ");
                                }
                                if(userBean.getType() == 1){
                                    if(!jsonObject.get("student").toString().equals("null")){
                                        Student student = new Gson().fromJson(jsonObject.getJSONObject("student").toString(),Student.class);
                                        student.saveThrows();
                                    }

                                }else if(userBean.getType() == 2){
                                    if(!jsonObject.get("staff").toString().equals("null")){
                                        Staff staff = new Gson().fromJson(jsonObject.getJSONObject("staff").toString(),Staff.class);
                                        staff.saveThrows();
                                    }

                                }

                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                                setResult(RESULT_OK);
                                if(list.size()!=0){
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void initView() {
        super.initView();
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        identifyOrPassword.setText("手机验证码登录");
    }



}
