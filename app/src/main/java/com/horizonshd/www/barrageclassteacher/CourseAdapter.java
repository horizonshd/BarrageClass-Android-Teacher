package com.horizonshd.www.barrageclassteacher;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private static  Context mContext;
    private List<Course> mCourseList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView courseName;
        Button btnIn;
//        Button btnOut;

        public ViewHolder(View view){
            super(view);
            courseName = (TextView) view.findViewById(R.id.course_name);
            btnIn = (Button) view.findViewById(R.id.btn_in);
//            btnOut = (Button) view.findViewById(R.id.btn_out);
        }
    }

    // 构造函数
    CourseAdapter(List<Course> courseList){
        mCourseList = courseList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }



    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Course course = mCourseList.get(position);

        holder.courseName.setText(course.getCoursename());


        holder.btnIn.setOnClickListener(new View.OnClickListener() {//进入课堂--点击事件
            @Override
            public void onClick(View v) {
                //1.向服务器提交 进入教室 请求
                //2.进入 ClassroomActivity(SingleTask)
                new getInTask().execute(course.getCourseid(),course.getCoursename());
            }
        });



    }


    public int getItemCount() {
        return mCourseList.size();
    }


    //进入课堂
    static class getInTask extends AsyncTask<String,Void,List<String>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected List<String> doInBackground(String... strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                //LogUtil.d("ID",strings[0]);
                RequestBody requestBody = new FormBody.Builder().add("courseid",strings[0]).build();
                Request request= new Request.Builder().url(MyApplication.getServerURL()+"/android/teacher/course-in").post(requestBody).build();

                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                JSONObject jsonObject = new JSONObject(responseData);
                String status = jsonObject.getString("status");
                if(status.equals("success")){
                    List<String> IDandCourseName = new ArrayList<>();
                    IDandCourseName.add(strings[0]);
                    IDandCourseName.add(strings[1]);
                    return IDandCourseName;
                }else {
                    return null;
                }

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            if(strings != null){
                Intent intent = new Intent(mContext,ClassroomActivity.class);
                intent.putExtra(ClassroomActivity.COURSE_ID,strings.get(0));
                intent.putExtra(ClassroomActivity.COURSE_NAME,strings.get(1));
                mContext.startActivity(intent);

            }
        }



    }

    //退出课堂
    private static class getOutTask extends AsyncTask<String,Void,Boolean>{
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                LogUtil.d("ID",strings[0]);
                RequestBody requestBody = new FormBody.Builder().add("courseid",strings[0]).build();
                Request request= new Request.Builder().url(MyApplication.getServerURL()+"/android/teacher/course-out").post(requestBody).build();

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
                Intent intent = new Intent(mContext,MainActivity.class);
                mContext.startActivity(intent);
                //finish()怎么调用
            }
        }
    }






}
