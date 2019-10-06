package com.example.kcb;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.lt.common.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;


@Route(path = "/course/main")
public class MainActivity extends BaseActivity {

    //星期几
    private RelativeLayout day;
    ImageView back;
    TextView titleName;
    ImageView titleInfo;
    TextView titleMore;
    @Autowired
    public int key;
    @Autowired
    public String courseName;

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
        initCourse();
        //从数据库读取数据
        loadData();
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
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
        if(key == 1){//点击首页的课表进入
            titleMore.setVisibility(View.VISIBLE);
            titleMore.setText("更多");
            titleName.setText("课程表");
        }else if(key == 2){//从查看教室状态进入，此时可以添加为常用教室
            titleMore.setVisibility(View.VISIBLE);
            titleName.setText(courseName);
            titleMore.setText("设为常用");
        }else if(key == 0){//查看其他班级的课表
            titleName.setText(courseName);
        }


    }

    //从数据库加载数据
    private void loadData() {
        ArrayList<Course> coursesList = new ArrayList<>(); //课程列表
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from courses", null);
        if (cursor.moveToFirst()) {
            do {
                coursesList.add(new Course(
                        cursor.getString(cursor.getColumnIndex("course_name")),
                        cursor.getString(cursor.getColumnIndex("teacher")),
                        cursor.getString(cursor.getColumnIndex("class_room")),
                        cursor.getInt(cursor.getColumnIndex("day")),
                        cursor.getInt(cursor.getColumnIndex("class_start")),
                        cursor.getInt(cursor.getColumnIndex("class_end"))));
            } while(cursor.moveToNext());
        }
        cursor.close();

        //使用从数据库读取出来的课程信息来加载课程表视图
        createLeftView();
        for (Course course : coursesList) {
            createItemCourseView(course);
        }
    }

    void initCourse(){
            for (int i=0 ; i<5;i++){
                Course course = new Course("数据结构", "严冬梅", "2307",
                        Integer.valueOf(i), Integer.valueOf(i), Integer.valueOf(i));
                saveData(course);
            }
            Course course = new Course("数据结构", "严冬梅", "2307",
                    Integer.valueOf(3), Integer.valueOf(6), Integer.valueOf(6));
            saveData(course);
    }
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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100,320);
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

        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme))
                .setTitle(getResources().getString(R.string.add_classroom))
                .setView(viewStatus)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}