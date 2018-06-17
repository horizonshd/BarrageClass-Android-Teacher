package com.horizonshd.www.barrageclassteacher;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddQuestionByHandActivity extends BaseActivity {

    private Toolbar toolbar;
    private EditText edt_description;
    private EditText edt_optiona;
    private EditText edt_optionb;
    private EditText edt_optionc;
    private EditText edt_optiond;
    private Spinner spinner_answer;
    private Button btn_submmit;

    private String str_description;
    private String str_optiona;
    private String str_optionb;
    private String str_optionc;
    private String str_optiond;
    private String str_answer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addquestion_byhand);

        initView();

        btn_submmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_description = edt_description.getText().toString().trim();
                str_optiona = edt_optiona.getText().toString().trim();
                str_optionb = edt_optionb.getText().toString().trim();
                str_optionc = edt_optionc.getText().toString().trim();
                str_optiond = edt_optiond.getText().toString().trim();
                str_answer = spinner_answer.getSelectedItem().toString().trim();

                new AddQuestionTask().execute();
            }
        });


    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        edt_description = (EditText) findViewById(R.id.edt_description);
        edt_optiona = (EditText) findViewById(R.id.edt_optiona);
        edt_optionb = (EditText) findViewById(R.id.edt_optionb);
        edt_optionc = (EditText) findViewById(R.id.edt_optionc);
        edt_optiond = (EditText) findViewById(R.id.edt_optiond);
        spinner_answer = (Spinner) findViewById(R.id.spinner_answer);
        btn_submmit = (Button) findViewById(R.id.btn_submmit);
    }

    //添加试题的task
    class AddQuestionTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder().add("teacherid",MyApplication.getAuthenticatedId()).add("description",str_description).add("optiona",str_optiona).add("optionb",str_optionb).add("optionc",str_optionc).add("optiond",str_optiond).add("answer",str_answer).build();
                Request request= new Request.Builder().url(MyApplication.getServerURL()+"/android/teacher/add-question-byhand").post(requestBody).build();

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
            if(issuccess){
//                Intent intent = new Intent(AddQuestionByHandActivity.this,QuestionLibraryActivity.class);
//                startActivity(intent);
                finish();
            }else {
                Toast.makeText(AddQuestionByHandActivity.this,"添加试题失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
