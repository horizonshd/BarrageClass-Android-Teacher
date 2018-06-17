/**
 * 题库ACtivity
 */
package com.horizonshd.www.barrageclassteacher;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuestionLibraryActivity extends BaseActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private static EditText edt_paperename;
    private SwipeRefreshLayout  swipe_refresh;
    private static RecyclerView recyclerView;
    private static QuestionAdapter adapter;
    private static List<Question> questionList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionlibrary);



        initView();
        new getQuestionListTask().execute();
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getQuestionListTask().execute();
                swipe_refresh.setRefreshing(false);
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new QuestionAdapter(questionList);//空的
        recyclerView.setAdapter(adapter);
    }


    //toolbar中添加Action按钮
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_questionlibrary,menu);
        return true;
    }


    //toolbar中菜单选项的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //手动添加试题
            case R.id.add_by_hand:
                Intent intent1 = new Intent(QuestionLibraryActivity.this,AddQuestionByHandActivity.class);
                startActivity(intent1);
                finish();
                break;
            //从文件导入试题
            case R.id.add_from_file:
                Intent intent2 = new Intent(QuestionLibraryActivity.this,AddQuestionFromFileActivity.class);
                startActivity(intent2);
                finish();
                break;
            //创建试卷
            case R.id.create_paper:
                QuestionAdapter.setShowCheckbox(true);
                adapter.notifyDataSetChanged();
                //显示提交按钮
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = getLayoutInflater().inflate(R.layout.dialog_view_createpaper,null);
                        edt_paperename = (EditText)view.findViewById(R.id.edt_papername);

                        AlertDialog.Builder dialog = new AlertDialog.Builder(QuestionLibraryActivity.this);
                        //.setIcon(R.mipmap.icon)//设置标题的图片
                        dialog.setTitle("试卷名称");//设置对话框的标题
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
                                new createPaperTask().execute();

                            }
                        });
                        dialog.show();
                    }
                });
                break;
            default:
        }
        return true;
    }

    //重写返回键事件
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        QuestionAdapter.setShowCheckbox(false);
        QuestionAdapter.questionIDList.clear();
        fab.setVisibility(View.GONE);
    }

    // 获取试题列表
    static class getQuestionListTask extends AsyncTask<Void,Question,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            questionList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder().add("teacherid",MyApplication.getAuthenticatedId()).build();
                Request request= new Request.Builder().url(MyApplication.getServerURL()+"/android/teacher/get-questionlist").post(requestBody).build();

                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                //LogUtil.d("RES",responseData);

                JSONArray jsonArray = new JSONArray(responseData);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Question question = new Question(jsonObject.getString("_id"),jsonObject.getString("description"),jsonObject.getString("optiona"),jsonObject.getString("optionb"),jsonObject.getString("optionc"),jsonObject.getString("optiond"),jsonObject.getString("answer"));
                    publishProgress(question);
                }

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Question... values) {
            super.onProgressUpdate(values);
            questionList.add(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }

    }

    //创建试卷
    class createPaperTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder().add("papername",edt_paperename.getText().toString().trim()).add("teacherid",MyApplication.getAuthenticatedId()).add("questionidlist",QuestionAdapter.questionIDList.toString()).build();
                Request request= new Request.Builder().url(MyApplication.getServerURL()+"/android/teacher/create-paper").post(requestBody).build();

                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                LogUtil.d("IDList",QuestionAdapter.questionIDList.toString());

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
            if(issuccess) {
                //Toast.makeText(MyApplication.getContext(), "创建试卷成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(QuestionLibraryActivity.this,PaperLibraryActivity.class);
                startActivity(intent);

                QuestionAdapter.setShowCheckbox(false);
                QuestionAdapter.questionIDList.clear();
                fab.setVisibility(View.GONE);

                finish();
            }
            if(!issuccess) Toast.makeText(MyApplication.getContext(), "创建试卷失败", Toast.LENGTH_SHORT).show();
        }
    }
}
