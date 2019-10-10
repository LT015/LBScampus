package com.lbs.cheng.lbscampus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.ShareTimeBean;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lbs.cheng.lbscampus.util.DateUtil;
import com.lt.common.util.HttpUtil;
import com.lbs.cheng.lbscampus.view.CustomDatePicker;
import com.lbs.cheng.lbscampus.view.DateFormatUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TimeIntervalActivity extends BaseActivity{
    ImageView back;
    TextView titleName;
    @BindView(R.id.time_interval_finish)
    TextView finish;
    private String startTime,endTime;//从时间选择器获取的时间
    private String start,end;//要上传的时间  保证了年月日相同
    private TextView tvStartTime, tvEndTime;
    private CustomDatePicker startTimePicker, endTimePicker;
    private UserBean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_interval);

        findViewById(R.id.start_time).setOnClickListener(this);
        tvStartTime = findViewById(R.id.tv_start_time);
        initStartTimePicker();
        findViewById(R.id.end_time).setOnClickListener(this);
        tvEndTime = findViewById(R.id.tv_end_time);
        initEndTimerPicker();
    }
    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        back.setOnClickListener(this);
        titleName.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleName.setText("共享时段");
        finish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_time:
                // 日期格式为yyyy-MM-dd
                startTimePicker.show(tvStartTime.getText().toString());
                break;

            case R.id.end_time:
                // 日期格式为yyyy-MM-dd HH:mm
                endTimePicker.show(tvEndTime.getText().toString());
                break;
            case R.id.title_back:
                finish();
                break;
            case R.id.time_interval_finish:
                saveShareTime();

        }
    }

    @Override
    protected void initData() {
        super.initData();
        initTitle();
        user=DataSupport.findLast(UserBean.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startTimePicker.onDestroy();
    }

    private void saveShareTime(){

        String url= HttpUtil.HOME_PATH + HttpUtil.UPDATE_SHARE_TIME+"/"+user.getUserId()+"/"+start+"/"+end;
        HashMap<String,String> hash=new HashMap<>();
        hash.put("userId",user.getUserId());
        HttpUtil.sendOkHttpPostRequest(url, hash, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TimeIntervalActivity.this, "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
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

                        if (responseText.isEmpty()){

                            ShareTimeBean shareTime= DataSupport.findLast(ShareTimeBean.class);
                            if(shareTime == null){
                                ShareTimeBean shareTimeBean = new ShareTimeBean();
                                shareTimeBean.setUserId(user.getUserId());
                                shareTimeBean.setStartTime(start);
                                shareTimeBean.setEndTime(end);
                                shareTimeBean.saveThrows();
                                Toast.makeText(TimeIntervalActivity.this, "修改成功!", Toast.LENGTH_SHORT).show();
                            }else{
                                shareTime.setStartTime(start);
                                shareTime.setEndTime(end);
                                shareTime.saveThrows();
                                Toast.makeText(TimeIntervalActivity.this, "修改成功!", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }else{       //返回错误信息
                            Toast.makeText(TimeIntervalActivity.this, "修改失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }


    private void initStartTimePicker() {
        long beginTimestamp = DateFormatUtils.str2Long("2009-05-01 0:00:00", true);
        long endTimestamp = System.currentTimeMillis();
        ShareTimeBean shareTime= DataSupport.findLast(ShareTimeBean.class);
        if(shareTime != null){
            startTime = shareTime.getStartTime();
            start = startTime;
            tvStartTime.setText(DateFormatUtils.getRightText(startTime));//显示的开始时间
        }else{
            tvStartTime.setText(DateFormatUtils.long2Str(endTimestamp, false));
        }


        // 通过时间戳初始化日期，毫秒级别
        startTimePicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                startTime = DateFormatUtils.long2String(timestamp);
                start = startTime;
                tvStartTime.setText(DateFormatUtils.long2Str(timestamp,false));
            }
        }, beginTimestamp, endTimestamp);
        // 允许点击屏幕或物理返回键关闭
        startTimePicker.setCancelable(true);
        // 显示时和分
        startTimePicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        startTimePicker.setScrollLoop(true);
        // 允许滚动动画
        startTimePicker.setCanShowAnim(true);
    }

    private void initEndTimerPicker() {
        long beginTimestamp = DateFormatUtils.str2Long("2009-05-01 0:00:00", true);
        long endTimestamp = System.currentTimeMillis();
        ShareTimeBean shareTime= DataSupport.findLast(ShareTimeBean.class);
        if(shareTime != null){
            endTime = shareTime.getEndTime();
            end = endTime;
            tvEndTime.setText(DateFormatUtils.getRightText(endTime));//显示的结束时间
        }else{
            tvEndTime.setText(DateFormatUtils.long2Str(endTimestamp, false));
        }


        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        endTimePicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                endTime = DateFormatUtils.long2String(timestamp);
                end = endTime;
                tvEndTime.setText(DateFormatUtils.long2Str(timestamp,false));
            }
        }, beginTimestamp, endTimestamp);

        // 允许点击屏幕或物理返回键关闭
        endTimePicker.setCancelable(true);
        // 显示时和分
        endTimePicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        endTimePicker.setScrollLoop(true);
        // 允许滚动动画
        endTimePicker.setCanShowAnim(true);
    }
}
