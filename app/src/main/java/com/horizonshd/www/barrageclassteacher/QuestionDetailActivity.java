package com.horizonshd.www.barrageclassteacher;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class QuestionDetailActivity extends BaseActivity {

    public static final String QUESTION_PASS = "question";

    private Toolbar toolbar;

    private EditText edt_description;
    private EditText edt_optiona;
    private EditText edt_optionb;
    private EditText edt_optionc;
    private EditText edt_optiond;
    private TextView txt_answer;
    private Button btn_update;

    Question question;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questiondetail);


        question = (Question) getIntent().getSerializableExtra(QUESTION_PASS);


        initView();

    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edt_description = (EditText) findViewById(R.id.edt_description);
        edt_optiona = (EditText) findViewById(R.id.edt_optiona);
        edt_optionb = (EditText) findViewById(R.id.edt_optionb);
        edt_optionc = (EditText) findViewById(R.id.edt_optionc);
        edt_optiond = (EditText) findViewById(R.id.edt_optiond);
        txt_answer = (TextView) findViewById(R.id.txt_answer);
        btn_update = (Button) findViewById(R.id.btn_update);

        edt_description.setText(question.getDescription().toString().trim());
        edt_optiona.setText(question.getOptiona().toString().trim());
        edt_optionb.setText(question.getOptionb().toString().trim());
        edt_optionc.setText(question.getOptionc().toString().trim());
        edt_optiond.setText(question.getOptiond().toString().trim());
        txt_answer.setText(question.getAnswer().toString().trim());


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new questionUpdataTask().execute(question.getQuestionid());
            }
        });
    }

    class questionUpdataTask extends AsyncTask<String,Void,Boolean>{
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                //LogUtil.d("ID",strings[0]);
                RequestBody requestBody = new FormBody.Builder()
                        .add("questionid",strings[0])
                        .add("description",edt_description.getText().toString().trim())
                        .add("a",edt_optiona.getText().toString().trim())
                        .add("b",edt_optionb.getText().toString().trim())
                        .add("c",edt_optionc.getText().toString().trim())
                        .add("d",edt_optiond.getText().toString().trim())
                        .build();
                Request request= new Request.Builder().url(MyApplication.getServerURL()+"/android/teacher/question-update").post(requestBody).build();

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
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Toast.makeText(QuestionDetailActivity.this,"修改成功！",Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(QuestionDetailActivity.this,"修改失败！",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
