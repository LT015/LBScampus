package com.lbs.cheng.lbscampus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mapapi.SDKInitializer;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.overlay.RoutePlanDemo;
import com.lbs.cheng.lbscampus.util.CommonUtils;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_welcome);
        CommonUtils.buildingTypeId = 1;
        CommonUtils.noticeTypeId = 1;
        initDelay();
//        if (true) {
//            ARouter.openLog();
//            ARouter.openDebug();
//        }

    }

    private void initDelay() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    startActivity(new Intent(WelcomeActivity.this,HomeActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
