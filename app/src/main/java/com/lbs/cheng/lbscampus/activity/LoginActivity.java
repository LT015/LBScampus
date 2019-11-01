package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.LoginInfo;
import com.lbs.cheng.lbscampus.bean.SearchHistoricalBean;
import com.lbs.cheng.lbscampus.bean.ShareTimeBean;
import com.lbs.cheng.lbscampus.bean.Staff;
import com.lbs.cheng.lbscampus.bean.Student;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lt.common.util.HttpUtil;
import com.zhy.autolayout.AutoLinearLayout;

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
    @BindView(R.id.login_by_password_ll)
    AutoLinearLayout loginByPasswordLl;
    @BindView(R.id.login_by_identify_code_ll)
    AutoLinearLayout loginByIdentifyCodeLl;
    @BindView(R.id.login_get_identify_code)
    TextView loginGetIdentifyCodeTv;
    @BindView(R.id.login_user_name)
    EditText userName;
    @BindView(R.id.login_phone)
    EditText loginPhoneEt;
    @BindView(R.id.login_password)
    EditText password;
    @BindView(R.id.login_identify_code)
    EditText loginIdentifyCodeEt;
    @BindView(R.id.login_clear_iv)
    ImageView clearIv;
    @BindView(R.id.login_clear_iv2)
    ImageView clearIv2;
    @BindView(R.id.login_eye_iv)
    ImageView eyeIv;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.login_by_identify_code)
    TextView loginByIdentifyCodeTv;
    @BindView(R.id.login_by_password)
    TextView loginByPassword;
    @BindView(R.id.identify_code)
    TextView identifyCode;
    @BindView(R.id.login_not)
    TextView loginNotTv;
    private int a = 1000000000;
    private LoginTextWatcher loginTextWatcher;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initData() {
        super.initData();
        loginTextWatcher = new LoginTextWatcher();

    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        loginBtn.setOnClickListener(this);
        clearIv.setOnClickListener(this);
        clearIv2.setOnClickListener(this);
        eyeIv.setOnClickListener(this);
        loginByIdentifyCodeTv.setOnClickListener(this);
        loginNotTv.setOnClickListener(this);
        loginByPassword.setOnClickListener(this);
        loginGetIdentifyCodeTv.setOnClickListener(this);
        userName.addTextChangedListener(loginTextWatcher);
        password.addTextChangedListener(loginTextWatcher);
        loginPhoneEt.addTextChangedListener(loginTextWatcher);
        loginIdentifyCodeEt.addTextChangedListener(loginTextWatcher);

        LoginInfo loginInfo = DataSupport.findLast(LoginInfo.class);
        if(loginInfo != null){
            if(loginInfo.getType() == 1){
                userName.setText(loginInfo.getNum());
                password.setText(loginInfo.getPassword());
            }else{
                loginPhoneEt.setText(loginInfo.getNum());
            }
        }
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


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.login_btn:
                if (loginByPasswordLl.getVisibility() == View.VISIBLE){
                    login();
                }else{//手机验证码登陆
                    if (Integer.parseInt(loginIdentifyCodeEt.getText().toString()) == a) {
                        CommonUtils.isLogin = 1;
                        Intent intent =  new Intent(LoginActivity.this,HomeActivity.class);
                        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        DataSupport.deleteAll(SearchHistoricalBean.class);
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
            case R.id.login_by_identify_code:
                loginWay();
                break;
            case R.id.login_by_password:
                loginWay();
                break;
            case R.id.login_get_identify_code:
                login();
                break;

            case R.id.login_not:
                AlertDialog builder = new AlertDialog.Builder(this)
                        .setTitle("修改登录密码流程")
                        .setMessage("通过手机验证码登录->进入我的界面->修改登录密码")
                        .setPositiveButton("确定", null)
                        .create();
                builder.show();
                break;
            case R.id.login_clear_iv:
                clearEditText(userName);
                break;
            case R.id.login_clear_iv2:
                clearEditText(loginPhoneEt);
                break;
            case R.id.login_eye_iv:
                if (password.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                password.setSelection(password.getText().length());
                break;
        }
    }

    public void login(){
        List<String> list=new ArrayList<>();
        //密码登陆
        if (loginByPasswordLl.getVisibility() == View.VISIBLE){
            String studentId = userName.getText().toString().replace(" ", "");
            String passWord = password.getText().toString();
            list.add(studentId);
            list.add(passWord);
        }else{//手机验证码登陆
            String telNum = loginPhoneEt.getText().toString().replace(" ","");
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
                                LoginInfo loginInfo = new LoginInfo();
                                List<UserBean> list= DataSupport.findAll(UserBean.class);
                                if(!jsonObject.get("shareTime").toString().equals("null")){
                                    ShareTimeBean shareTime = new Gson().fromJson(jsonObject.getJSONObject("shareTime").toString(),ShareTimeBean.class);
                                    shareTime.saveThrows();

                                }
                                if(userBean.getType() == 1){
                                    if(!jsonObject.get("student").toString().equals("null")){
                                        Student student = new Gson().fromJson(jsonObject.getJSONObject("student").toString(),Student.class);
                                        student.saveThrows();
                                        loginInfo.setNum(student.getStudentId());
                                    }

                                }else{
                                    if(!jsonObject.get("staff").toString().equals("null")){
                                        Staff staff = new Gson().fromJson(jsonObject.getJSONObject("staff").toString(),Staff.class);
                                        staff.saveThrows();
                                        loginInfo.setNum(staff.getStaffId());

                                    }

                                }

                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                                setResult(RESULT_OK);
                                if (loginByPasswordLl.getVisibility() == View.VISIBLE){
                                    CommonUtils.isLogin = 1;
                                    loginInfo.setPassword(userBean.getPassword());
                                    loginInfo.setType(1);
                                    loginInfo.saveThrows();
                                    Intent intent =  new Intent(LoginActivity.this,HomeActivity.class);
                                    DataSupport.deleteAll(SearchHistoricalBean.class);
                                    intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }else{//手机验证码登陆
                                    CommonUtils.isLogin = 0;
                                    loginInfo.setNum(userBean.getTelNumber());
                                    loginInfo.setPassword("");
                                    loginInfo.setType(2);
                                    loginInfo.saveThrows();
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

    private void clearEditText(EditText et) {
        et.setText("");
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
    }

    private void loginWay() {
        if (loginByPasswordLl.getVisibility() == View.GONE) {
            identifyCode.setVisibility(View.GONE);
            loginByPasswordLl.setVisibility(View.VISIBLE);
            loginByIdentifyCodeLl.setVisibility(View.GONE);
            loginByIdentifyCodeTv.setVisibility(View.VISIBLE);
            loginNotTv.setVisibility(View.VISIBLE);
            loginByPassword.setVisibility(View.GONE);
            password.setText("");
        } else {
            if (loginPhoneEt.getText().toString().length() == 13) {
                loginGetIdentifyCodeTv.setEnabled(true);
                loginGetIdentifyCodeTv.setTextColor(getResources().getColor(R.color.bottom_tab_text_selected_color));
            }
            loginByIdentifyCodeLl.setVisibility(View.VISIBLE);
            loginByPasswordLl.setVisibility(View.GONE);
            loginByIdentifyCodeTv.setVisibility(View.GONE);
            loginNotTv.setVisibility(View.GONE);
            loginByPassword.setVisibility(View.VISIBLE);
            loginIdentifyCodeEt.setText("");
        }

    }

    private void setCountDownTimer() {
        identifyCode.setVisibility(View.VISIBLE);
        a = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;//产生1000-9999的随机数
        identifyCode.setText("验证码为:" + a);
        loginGetIdentifyCodeTv.setEnabled(false);
        loginGetIdentifyCodeTv.setTextColor(getResources().getColor(R.color.text_color_grey));
        countDownTimer = new CountDownTimer(59000 + 50, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!LoginActivity.this.isFinishing()){
                    loginGetIdentifyCodeTv.setText("已发送(" + millisUntilFinished / 1000 + "秒)");
                }

            }

            @Override
            public void onFinish() {
                if(!LoginActivity.this.isFinishing()){
                    if (loginPhoneEt.getText().toString().length() == 11) {
                        loginGetIdentifyCodeTv.setEnabled(true);
                        loginGetIdentifyCodeTv.setTextColor(getResources().getColor(R.color.bottom_tab_text_selected_color));
                    }
                    loginGetIdentifyCodeTv.setText("获取验证码");
                }

            }
        }.start();
    }

    class LoginTextWatcher implements TextWatcher {
        EditText editText;
        int lastContentLength = 0;
        boolean isDelete = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //隐藏显示登陆按钮
            if (loginByPasswordLl.getVisibility() == View.VISIBLE) {
                if (!(userName.getText().toString().equals("") || password.getText().toString().equals(""))) {
                    loginBtn.setEnabled(true);
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.login_selected_bg));
                }
            } else {
                if (!(loginPhoneEt.getText().toString().equals("") || loginIdentifyCodeEt.getText().toString().equals(""))) {
                    loginBtn.setEnabled(true);
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.login_selected_bg));
                }
            }

        }

        /**
         * 添加或删除空格EditText的设置
         *
         * @param sb
         */
        private void setContent(StringBuffer sb) {
            editText.setText(sb.toString());
            //移动光标到最后面
            editText.setSelection(sb.length());
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().equals("")) {
                loginBtn.setEnabled(false);
                loginBtn.setBackground(getResources().getDrawable(R.drawable.login_bg));
            }
            if (loginByIdentifyCodeLl.getVisibility() == View.VISIBLE) {
                if (loginPhoneEt.getText().toString().length() == 11 && loginGetIdentifyCodeTv.getText().toString().equals("获取验证码")) {
                    loginGetIdentifyCodeTv.setTextColor(getResources().getColor(R.color.bg_blue));
                    loginGetIdentifyCodeTv.setEnabled(true);
                } else {
                    loginGetIdentifyCodeTv.setEnabled(false);
                    loginGetIdentifyCodeTv.setTextColor(getResources().getColor(R.color.text_color_grey));
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }

        if(CommonUtils.isLogin == 0){
            DataSupport.deleteAll(UserBean.class);
            DataSupport.deleteAll(ShareTimeBean.class);
            DataSupport.deleteAll(Student.class);
            DataSupport.deleteAll(Staff.class);
            DataSupport.deleteAll(LoginInfo.class);
        }

    }
}
