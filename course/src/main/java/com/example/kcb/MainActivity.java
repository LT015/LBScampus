package com.example.kcb;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.kcb.bean.Course;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lt.common.activity.BaseActivity;
import com.lt.common.bean.UserBean;
import com.lt.common.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


@Route(path = "/course/main")
public class MainActivity extends BaseActivity {

    //星期几
    private RelativeLayout day;
    ImageView back;
    TextView titleName;
    ImageView titleInfo;
    TextView titleMore;
    @Autowired
    public int key; //1是本班课表 2是教室课表 3是其他班课表
    @Autowired
    public int classId;//key为1或3时有值
    @Autowired
    public String courseName;// key 为2时，为教室名 key为3时为班级名
    ArrayList<Course> coursesList = new ArrayList<>(); //课程列表
    private int status = 2;//status为2时表示查看当前room是否被该用户收藏   收藏为1，未收藏为0

    //SQLite Helper类
    private DatabaseHelper databaseHelper = new DatabaseHelper
            (this, "database.db", null, 1);

    int currentCoursesNumber = 0;
    int maxCoursesNumber = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ARouter.getInstance().inject(this);
        //初始化数据
//        initCourse();
        //从数据库读取数据
//        loadData();
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        getCourseList();
    }

    private void initTitle() {
        back = findViewById(R.id.title_back);
        titleName = findViewById(R.id.title_name);
        titleInfo = findViewById(R.id.title_info);
        titleMore = findViewById(R.id.title_register);
        back.setOnClickListener(this);
        titleInfo.setOnClickListener(this);
        titleMore.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        titleInfo.setVisibility(View.VISIBLE);
        if(key == 1){//点击首页的课表进入 计科1901
            titleMore.setVisibility(View.VISIBLE);
            titleMore.setText("更多");
            titleName.setText("课程表");
        }else if(key == 2){//从查看教室状态进入，此时可以添加为常用教室
            titleMore.setVisibility(View.VISIBLE);
            titleName.setText(courseName);
            getUserRoomStatus();
        }else if(key == 3){//查看其他班级的课表 软件1901
            titleName.setText(courseName);
        }


    }

    //装载数据
    private void loadData() {
        //使用从数据库读取出来的课程信息来加载课程表视图
        createLeftView();
        for (Course course : coursesList) {
            createItemCourseView(course);
        }
    }



//    void initCourse(){
//            for (int i=0 ; i<5;i++){
//                Course course = new Course("数据结构", "严冬梅", "2307",
//                        Integer.valueOf(i), Integer.valueOf(i), Integer.valueOf(i));
//                saveData(course);
//            }
//            Course course = new Course("数据结构", "严冬梅", "2307",
//                    Integer.valueOf(3), Integer.valueOf(6), Integer.valueOf(6));
//            saveData(course);
//    }
    //保存数据到数据库
    private void saveData(Course course) {
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL
                ("insert into courses(course_name, teacher, class_room, day, class_start, class_end) " + "values(?, ?, ?, ?, ?, ?)",
                new String[] {course.getCourseName(),
                        course.getTeacher(),
                        course.getClassRoom(),
                        course.getDay()+"",
                        course.getStart()+"",
                        course.getEnd()+""}
                );
    }

    //创建"第几节数"视图
    private void createLeftView() {
        for (int i = 0; i < maxCoursesNumber; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.left_view, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90,320);
            view.setLayoutParams(params);

            TextView text = view.findViewById(R.id.class_number_text);
            text.setText(String.valueOf(++currentCoursesNumber));

            View view1 = LayoutInflater.from(this).inflate(R.layout.views, null);


            LinearLayout leftViewLayout = findViewById(R.id.left_view_layout);
            leftViewLayout.addView(view1);
            leftViewLayout.addView(view);
        }
    }

    //创建单个课程视图
    private void createItemCourseView(final Course course) {
        int getDay = course.getDay();
        if ((getDay < 1 || getDay > 5) || course.getStart() > course.getEnd())
            // TODO: 2019/9/3
            Log.d("course", "星期几没写对,或课程结束时间比开始时间还早~~: ");
        else {
            int dayId = 0;
            switch (getDay) {
                case 1: dayId = R.id.monday; break;
                case 2: dayId = R.id.tuesday; break;
                case 3: dayId = R.id.wednesday; break;
                case 4: dayId = R.id.thursday; break;
                case 5: dayId = R.id.friday; break;
            }
            day = findViewById(dayId);

            int height = 320;
            final View v = LayoutInflater.from(this).inflate(R.layout.course_card, null); //加载单个课程布局
            v.setY(height * (course.getStart()-1)); //设置开始高度,即第几节课开始
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT,(course.getEnd()-course.getStart()+1)*height - 8); //设置布局高度,即跨多少节课
            v.setLayoutParams(params);
            TextView text = v.findViewById(R.id.text_view);
            text.setText(course.getCourseName() + "\n" + course.getTeacher() + "\n" + course.getClassRoom()); //显示课程名
            day.addView(v);
            //长按删除课程
            /*
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.setVisibility(View.GONE);//先隐藏
                    day.removeView(v);//再移除课程视图
                    SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
                    sqLiteDatabase.execSQL("delete from courses where course_name = ?", new String[] {course.getCourseName()});
                    return true;
                }
            });
            */
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_courses) {
            Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
            startActivityForResult(intent, 0);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Course course = (Course) data.getSerializableExtra("course");
            //创建课程表左边视图(节数)
//            createLeftView(course);
            //创建课程表视图
            createItemCourseView(course);
            //存储数据到数据库
            saveData(course);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.title_back) {
            finish();
        } else if (i == R.id.title_info) {
            getClassTime();
        } else if (i == R.id.title_register) {
            if(key == 1){
                Intent intent = new Intent(MainActivity.this,SelectClassActivity.class);
                startActivity(intent);
            }else if(key == 2){
                setUseClassroom();
            }

        }

    }
    private void getClassTime(){
        final View viewStatus = LayoutInflater.from(getApplicationContext()).inflate(R.layout.get_class_time,null);

        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme))
                .setTitle(getResources().getString(R.string.class_time))
                .setView(viewStatus)
                .setPositiveButton(getResources().getString(R.string.class_time_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .setNegativeButton("", null)
                .show();
    }
    private void setUseClassroom(){
        final View viewStatus = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_classroom,null);
        String title = "设为常用教室？";
        if(status ==  1){
            title = "取消关注？";
        }



        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme))
                .setTitle(title)
                .setView(viewStatus)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getUserRoomStatus();
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void getCourseList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("name");
        if(key == 2){
            String name = null;
            try {
                name = URLEncoder.encode(courseName,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            list.add(name);
            list.add("type");
            list.add("2");
        }else{
            String name = null;
            try {
                name = URLEncoder.encode(String.valueOf(classId),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            list.add(String.valueOf(name));
            list.add("type");
            list.add("1");
        }
        HttpUtil.sendOkHttpGetRequest( HttpUtil.HOME_PATH + HttpUtil.GET_TIMETABLE,list, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
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
                            final JSONArray jsonArray = new JSONArray(responseText);
                            coursesList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<Course>>(){}.getType());
                            loadData();
                        }catch (JSONException e){
                            Log.d("LoginActivity",e.toString());
                            Toast.makeText(MainActivity.this, "获取信息失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });





    }

    private void getUserRoomStatus() {//
        UserBean user= DataSupport.findLast(UserBean.class);
        List<String> list1 = new ArrayList<>();
        list1.add("userId");
        list1.add(user.getUserId());
        list1.add("roomName");
        String roomName = null;
        try {
            roomName = URLEncoder.encode(courseName,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        list1.add(roomName);
        list1.add("status");
        list1.add(String.valueOf(status));
        String url = HttpUtil.HOME_PATH + HttpUtil.COLLECT_ROOM;
        HttpUtil.sendOkHttpGetRequest( url, list1, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
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
                        int flag = 0;//用来判断要不要弹toast
                        if(status == 2){
                            flag = 1;
                        }
                        status = Integer.valueOf(responseText);
                        if(status == 0){
                            titleMore.setText("设为常用");
                            if(flag == 0){
                                Toast.makeText(MainActivity.this, "取消关注", Toast.LENGTH_SHORT).show();
                            }
                        }else if(status == 1){
                            titleMore.setText("已关注");
                            if(flag == 0){
                                Toast.makeText(MainActivity.this, "已关注", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
            }
        });
    }
}