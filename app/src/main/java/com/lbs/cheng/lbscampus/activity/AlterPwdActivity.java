package com.lbs.cheng.lbscampus.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.lbs.cheng.lbscampus.bean.ShareTimeBean;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lbs.cheng.lbscampus.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AlterPwdActivity extends BaseActivity {
    ImageView back;
    TextView titleName;
    @BindView(R.id.alter_password_old)
    EditText passwordOld;
    @BindView(R.id.alter_password_new)
    EditText passwordNew;
    @BindView(R.id.alter_password_new_identity)
    EditText passwordIdentify;
    @BindView(R.id.identify_btn)
    TextView btn;
    private String oldPwd = null;
    private String newPwd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_pwd);
    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("重置密码");
    }
    public void initListener(){
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.identify_btn:
                if(!passwordNew.getText().toString().isEmpty() && !passwordIdentify.getText().toString().isEmpty()){
                    if (passwordNew.getText().toString().equals(passwordIdentify.getText().toString())){
                        oldPwd= passwordOld.getText().toString();
                        newPwd=passwordNew.getText().toString();
                        updatePwd();
                        finish();
                    }else {
                        Toast.makeText(getApplication(),"两次密码输入不一致!",Toast.LENGTH_SHORT).show();
                    }
                    break;
                }else{
                    Toast.makeText(getApplication(),"输入密码为空!",Toast.LENGTH_SHORT).show();
                }


        }
    }

    @Override
    protected void initData() {
        super.initData();
        UserBean user = DataSupport.findLast(UserBean.class);
        initListener();
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        passwordOld.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordNew.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordIdentify.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }
    private void updatePwd(){

        UserBean user= DataSupport.findLast(UserBean.class);
        String userId = user.getUserId();
        HashMap<String,String> hash = new HashMap<>();
        hash.put("user_id",userId);
        hash.put("phone",newPwd);
        String url= HttpUtil.UPDATE_PASSWORD+"/"+userId+"/"+newPwd+"/"+oldPwd;
        HttpUtil.sendOkHttpPostRequest(url ,hash, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AlterPwdActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseText.isEmpty()){
                            Toast.makeText(AlterPwdActivity.this, "原密码错误", Toast.LENGTH_SHORT).show();
                        }else{
                            try{
                                final JSONObject jsonObject = new JSONObject(responseText);
                                UserBean user = new Gson().fromJson(jsonObject.toString(),UserBean.class);
                                DataSupport.deleteAll(UserBean.class);
                                user.saveThrows();
                                Toast.makeText(AlterPwdActivity.this, "修改成功", Toast.LENGTH_SHORT).show();

                            }catch (JSONException e){
                                Log.d("LoginActivity",e.toString());
                                Toast.makeText(AlterPwdActivity.this, "修改失败!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        });

    }
}
