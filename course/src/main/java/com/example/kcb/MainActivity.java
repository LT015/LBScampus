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
    public int tableflag;
    @Autowired
    public String courseName;
    ArrayList<Course> coursesList = new ArrayList<>(); //课程列表

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
        getCourseList();
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
        if(key == 1){//点击首页的课表进入 计科1901
            titleMore.setVisibility(View.VISIBLE);
            titleMore.setText("更多");
            titleName.setText("课程表");
        }else if(key == 2){//从查看教室状态进入，此时可以添加为常用教室
            titleMore.setVisibility(View.VISIBLE);
            titleName.setText(courseName);
            titleMore.setText("设为常用");
        }else if(key == 0){//查看其他班级的课表 软件1901
            titleName.setText(courseName);
        }


    }

    //从数据库加载数据
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

    private void getCourseList(){
        //String courseName, String teacher, String classRoom, int day, int classStart, int classEnd
        if(tableflag == 1){//计科1901
            Course course1 = new Course("程序设计基础",2,3,3,"严冬梅",""); coursesList.add(course1);
            Course course2 = new Course("电路与电子技术",3,3,3,"张南南",""); coursesList.add(course2);
            Course course3 = new Course("程序设计基础",3,4,4,"严冬梅",""); coursesList.add(course3);
            Course course4 = new Course("电路与电子技术",4,4,4,"张南南",""); coursesList.add(course4);
            Course course5 = new Course("高等数学Ⅰ",2,2,2,"王志芹",""); coursesList.add(course5);
            Course course6 = new Course("应用文写作",2,5,5,"张胜珍",""); coursesList.add(course6);
            Course course7 = new Course("高等数学Ⅰ",3,2,2,"王志芹",""); coursesList.add(course7);
            Course course8 = new Course("高等数学Ⅰ",4,2,2,"王志芹",""); coursesList.add(course8);
            Course course9 = new Course("中国近现代史纲要",4,5,5,	"葛亚坤",""); coursesList.add(course9);
            Course course10 = new Course("高等数学Ⅰ",5,2,2,"王志芹",""); coursesList.add(course10);
            Course course11 = new Course("中国近现代史纲要",5,5,5,"葛亚坤",""); coursesList.add(course11);
            Course course12 = new Course("机器人创新实践",1,1,1,"吴诺",""); coursesList.add(course12);
            Course course13 = new Course("程序设计基础实验",1,2,2,"严冬梅",""); coursesList.add(course13);
            Course course14 = new Course("计算机科学导论",1,3,3,"华斌",""); coursesList.add(course14);
            Course course15 = new Course("大学英语Ⅰ",1,4,4,"申彩红",""); coursesList.add(course15);
            Course course16 = new Course("大学英语Ⅰ",1,4,4,"李军育",""); coursesList.add(course16);
            Course course17 = new Course("大学英语Ⅰ",1,4,4,"杨祎",""); coursesList.add(course17);
        }else if(tableflag == 2){//软件1901
            Course course1 = new Course("大学英语Ⅰ",		1,	4,4	,"申彩红","F209"); coursesList.add(course1);
            Course course2 = new Course("大学英语Ⅰ",		1,	4,4	,"李军育","C201"); coursesList.add(course2);
            Course course3 = new Course("大学英语Ⅰ",		1,	4,4,"杨祎","M106"); coursesList.add(course3);
            Course course4 = new Course("应用文写作",		6,	1,1	,"张胜珍","M钻"); coursesList.add(course4);
            Course course5 = new Course("中国近现代史纲要",		4,	2,2	,"	葛亚坤","N2247"); coursesList.add(course5);
            Course course6 = new Course("中国近现代史纲要",		5,	2,2	,"	葛亚坤","N2247"); coursesList.add(course6);
            Course course7 = new Course("计算机科学导论",		1,	3,3	,"	华斌","N3101"); coursesList.add(course7);
            Course course8 = new Course("程序设计基础",		1,	2,2	,"	董静","C201"); coursesList.add(course8);
            Course course9 = new Course("程序设计基础",		4,	3,3	,"	董静","N2230"); coursesList.add(course9);
            Course course10 = new Course("程序设计基础实验",		4,	1,1	,"	董静","2307"); coursesList.add(course10);
            Course course11 = new Course("三维建模实验",		2,	1,1	,"王丽娟","A.B	"); coursesList.add(course11);
            Course course12 = new Course("高等数学Ⅰ",		1,	1,1	,"王亚","M203"); coursesList.add(course12);
            Course course13 = new Course("高等数学Ⅰ",		2,	2,2	,"王亚","M204"); coursesList.add(course13);
            Course course14 = new Course("高等数学Ⅰ",		3,	2,2	,"王亚","M1梯形"); coursesList.add(course14);
            Course course15 = new Course("高等数学Ⅰ",		5,	1,1	,"王亚","D213"); coursesList.add(course15);

        }else if(tableflag == 3){ //2307
            Course course1 = new Course("汇编程序设计"	,2	,3,3,"	王荃","计科1801"); coursesList.add(course1);
            Course course2 = new Course("程序设计基础"	,	4,	4,4,"	邢恩军","计算1801"); coursesList.add(course2);
            Course course3 = new Course("专业综合创新实践	",	2	,4,4,"	严冬梅","计科1601"); coursesList.add(course3);
            Course course4 = new Course("数据结构实验"	,	1,	1	,1,"严冬梅","计科1801"); coursesList.add(course4);
            Course course5 = new Course("数据结构实验	",	3,	4	,4,"何丽","软件1801"); coursesList.add(course5);
            Course course6 = new Course("数据结构实验"		,3	,4,4,	"何丽","网络1801"); coursesList.add(course6);
            Course course7 = new Course("数据库应用实践"		,4	,2	,2,"陈立君","计科1701"); coursesList.add(course7);
            Course course8 = new Course("数据库应用实践",	4	,2,2,	"陈立君","软件1701"); coursesList.add(course8);
            Course course9 = new Course("计算机网络技术实践"	,2,	1,1,"吕景刚","计科1701"); coursesList.add(course9);
            Course course10 = new Course("程序设计基础实验	",4	,1,1,"		董静","软件1901"); coursesList.add(course10);
            Course course11 = new Course("程序设计基础实验	",	4,	1	,1,"董静","软件1902"); coursesList.add(course11);
            Course course12 = new Course("程序设计基础实验	",1	,2,2,"	严冬梅","计科1901"); coursesList.add(course12);

        }

        //使用从数据库读取出来的课程信息来加载课程表视图
        createLeftView();
        for (Course coursee : coursesList) {
            createItemCourseView(coursee);
        }
    }
}