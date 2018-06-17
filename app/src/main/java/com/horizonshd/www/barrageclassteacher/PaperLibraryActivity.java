/**
 * 试卷库
 */
package com.horizonshd.www.barrageclassteacher;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaperLibraryActivity extends BaseActivity {

    private Toolbar toolbar;
    private SwipeRefreshLayout swip_refresh;
    private RecyclerView recyclerView;
    private static PaperAdapter adapter;
    private static List<Paper> paperList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paperlibrary);

        initView();
        new getPaperListTask().execute();
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        swip_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swip_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getPaperListTask().execute();
                swip_refresh.setRefreshing(false);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PaperAdapter(paperList);
        recyclerView.setAdapter(adapter);
    }


    //toolbar中添加Action按钮
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_paperlibrary,menu);
        return true;
    }


    //toolbar中菜单选项的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            default:
        }
        return true;
    }


    //获取试卷列表
    static class getPaperListTask extends AsyncTask<Void,Paper,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            paperList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder().add("teacherid",MyApplication.getAuthenticatedId()).build();
                Request request= new Request.Builder().url(MyApplication.getServerURL()+"/android/teacher/get-paperlist").post(requestBody).build();

                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                //LogUtil.d("RES",responseData);

                JSONArray jsonArray = new JSONArray(responseData);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Paper paper = new Paper(jsonObject.getString("_id"),jsonObject.getString("papername"));
                    publishProgress(paper);
                }

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Paper... values) {
            super.onProgressUpdate(values);
            paperList.add(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }
    }



}
