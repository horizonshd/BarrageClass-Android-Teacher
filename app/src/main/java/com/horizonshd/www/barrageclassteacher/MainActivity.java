package com.horizonshd.www.barrageclassteacher;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.emitter.Emitter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private NavigationView navView;
    private View HeaderViewInNavView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static RecyclerView CourseListRecyclerView;
    //private FloatingActionButton fab;

    private static EditText edt_coursename;
    private static CourseAdapter adapter;
    private static List<Course> courseList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //1.连接服务器的socket
        MyApplication.createSocket(MyApplication.getServerURL());
        MyApplication.getSocket().on("forceoffline",onForceOffline);

        //发送"teacher_login"事件
        MyApplication.getSocket().emit("teacher_login",MyApplication.getAuthenticatedAccount());
        initView();
        initToolbar();
        initNavigationView();
        //initFAB();


//        Intent intent = getIntent();
//        String account = intent.getStringExtra("account");
//        TextView txtViewInHeader = (TextView) HeaderViewInNavView.findViewById(R.id.account);
//        txtViewInHeader.setText(account);

        new getCourseListTask().execute();

    }

    private void initView(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navView = (NavigationView) findViewById(R.id.nav_view);
        HeaderViewInNavView = navView.getHeaderView(0);
        CourseListRecyclerView = (RecyclerView) findViewById(R.id.course_list_recycler_view);
        //fab = (FloatingActionButton) findViewById(R.id.fab);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getCourseListTask().execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        CourseListRecyclerView.setLayoutManager(layoutManager);
        adapter = new CourseAdapter(courseList);//空的
        CourseListRecyclerView.setAdapter(adapter);
    }

    private void initToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        }
    }

    private void initNavigationView(){
        navView.setCheckedItem(R.id.nav_change);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
//                    case R.id.nav_user:
//                        //注册账号
//                        item.setChecked(true);
//                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
//                        startActivity(intent);
//                        mDrawerLayout.closeDrawer(GravityCompat.START);
//                        break;
                    case R.id.nav_change:
                        //切换账号
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("提醒");
                        builder.setMessage("你将会退出当前账号，并重新登录！");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCollector.finishAll();//销毁所有活动
                                Intent intent1 = new Intent(MainActivity.this,LoginActivity.class);
                                startActivity(intent1);//转到登录界面
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        break;
                    case R.id.nav_logout:
                        //退出应用
                        item.setChecked(true);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        ActivityCollector.finishAll();
                        break;
                    default:
                }

                return true;
            }
        });
    }


//    private void initFAB(){
//        //fab按钮的点击事件
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(v,"Data deleted", Snackbar.LENGTH_SHORT).setAction("Undo", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(MainActivity.this,"Data restored",Toast.LENGTH_SHORT).show();
//                    }
//                }).show();
//            }
//        });
//    }



    //toolbar中添加Action按钮
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    //toolbar中菜单选项的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //创建课堂
            case R.id.create_course:
                View view = getLayoutInflater().inflate(R.layout.dialoag_view_createcourse,null);
                edt_coursename = (EditText)view.findViewById(R.id.edt_coursename);

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        //.setIcon(R.mipmap.icon)//设置标题的图片
                dialog.setTitle("创建课堂");//设置对话框的标题
                dialog.setView(view);
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new createCourseTask().execute();
                            }
                        });
                dialog.show();
                break;
            //进入题库
            case R.id.question_library:
                Intent intent1 = new Intent(MainActivity.this,QuestionLibraryActivity.class);
                startActivity(intent1);
                break;
            //进入试卷库
            case R.id.paper:
                Intent intent2 = new Intent(MainActivity.this,PaperLibraryActivity.class);
                startActivity(intent2);
                break;
//            case R.id.settings:
//                Toast.makeText(this,"You clicked 设置",Toast.LENGTH_SHORT).show();
//                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }


    // 创建课程
    static class createCourseTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder().add("teacherid",MyApplication.getAuthenticatedId()).add("coursename",edt_coursename.getText().toString().trim()).build();
                Request request= new Request.Builder().url(MyApplication.getServerURL()+"/android/teacher/create-course").post(requestBody).build();

                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                String status = jsonObject.getString("status");
                if(status.equals("success")){
                    return true;
                }else {
                    return false;
                }

            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean issuccess) {
            super.onPostExecute(issuccess);
            if(issuccess) new getCourseListTask().execute();//更新Activity的课程列表
            else Toast.makeText(MyApplication.getContext(),"创建失败",Toast.LENGTH_SHORT).show();
        }
    }

    // 获取课程列表并在Activity显示
    static class getCourseListTask extends AsyncTask<Void,Course,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            courseList.clear();//清空数据
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder().add("teacherid",MyApplication.getAuthenticatedId()).build();
                Request request= new Request.Builder().url(MyApplication.getServerURL()+"/android/teacher/get-courselist").post(requestBody).addHeader("Connection", "close").build();

                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                //LogUtil.d("RES",responseData);

                JSONArray jsonArray = new JSONArray(responseData);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Course course = new Course(jsonObject.getString("_id"),jsonObject.getString("coursename"),jsonObject.getBoolean("isactive"));
                    publishProgress(course);
                }

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Course... values) {
            super.onProgressUpdate(values);
            courseList.add(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }
    }

    private Emitter.Listener onForceOffline = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Intent intent1 = new Intent("com.horizonshd.www.barrageclassteacher.FORCE_OFFLINE");
            sendBroadcast(intent1);
        }
    };

}
