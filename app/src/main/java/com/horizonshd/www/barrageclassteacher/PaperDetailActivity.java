/**
 * 试卷详情页
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaperDetailActivity extends BaseActivity {
    public static final String PAPER_NAME = "papername";
    private static String paperName;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private static List<QuestionInPaper> questionInPaperList = new ArrayList<>();
    private static QuestionInPaperAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paperdetail);

        Intent intent = getIntent();
        paperName = intent.getStringExtra(PAPER_NAME);

        initView();
        new getQuestionsInPaperTask().execute();


    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("试卷 ["+paperName+"]");
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        QuestionInPaper questionInPaper = new QuestionInPaper("qweqw","asasas","dsadsad","dasdasdas","dsadasd","A");
//        questionInPaperList.add(questionInPaper);
        adapter = new QuestionInPaperAdapter(questionInPaperList);
        recyclerView.setAdapter(adapter);

    }

    //获取一份试卷中的所有试题
    static class getQuestionsInPaperTask extends AsyncTask<Void,QuestionInPaper,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            questionInPaperList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder().add("teacherid",MyApplication.getAuthenticatedId()).add("papername",paperName).build();
                Request request= new Request.Builder().url(MyApplication.getServerURL()+"/android/teacher/get-questionlist-in-paper").post(requestBody).build();

                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                LogUtil.d("RES",responseData);

                JSONArray jsonArray = new JSONArray(responseData);
                //LogUtil.d("Length",jsonArray.getString(0));
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    QuestionInPaper questionInPaper = new QuestionInPaper(jsonObject.getString("description"),jsonObject.getString("optiona"),jsonObject.getString("optionb"),jsonObject.getString("optionc"),jsonObject.getString("optiond"),jsonObject.getString("answer"));
                    //LogUtil.d("QuestionName",jsonObject.getString("description"));
                    publishProgress(questionInPaper);
                }

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(QuestionInPaper... values) {
            super.onProgressUpdate(values);
            questionInPaperList.add(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }
    }
}
