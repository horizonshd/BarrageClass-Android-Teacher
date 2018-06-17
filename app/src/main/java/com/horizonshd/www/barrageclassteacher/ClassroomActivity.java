/**
 * 教室Activity---聊天室
 */
package com.horizonshd.www.barrageclassteacher;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClassroomActivity extends BaseActivity {
    public static final String COURSE_ID = "course_id";
    public static final String COURSE_NAME = "course_name";

    private List<Message> messageList = new ArrayList<>();

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private EditText editInput;
    private Button btnSend;
    private MessageAdapter adapter;
    private int selectedPaperIndex = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        //0.获取参数
        //Intent intent = getIntent();


        //1.注册socket监听的事件
        MyApplication.getSocket().on("send-message",onMessage);
        MyApplication.getSocket().on("mention",onMention);
        //MyApplication.getSocket().on("list-result",onListResult);

        //2.初始化Activity控件
        initView();

        //3.发送消息
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editInput.getText().toString().trim();
                if(content == null || content.trim().length() == 0){

                }else {
                    try {
                        MyApplication.getSocket().emit("send-message",content);
                        editInput.setText("");
                        messageList.add(new Message(MyApplication.getAuthenticatedAccount(),Message.TYPE_SENT,content));
                        adapter.notifyItemInserted(messageList.size()-1);
                        recyclerView.scrollToPosition(messageList.size()-1);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        //4.发送"teacher_getin"事件
        MyApplication.getSocket().emit("teacher_getin",getIntent().getStringExtra(COURSE_NAME));
//        MyApplication.getSocket().emit("list", new Ack() {
//            @Override
//            public void call(Object... args) {
//                try {
//                    JSONArray jsonArray = new JSONArray(args[0]);
//                    for(int i=0;i<jsonArray.length();i++){
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        LogUtil.d("List",jsonObject.getString("name"));
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //1.1发送"teacher_getout"事件，撤销socket上的相关监听事件
        MyApplication.getSocket().emit("teacher_getout");
        MyApplication.getSocket().off("send-message",onMessage);

        //1.2更新该课程在数据库中的"isactive"值
        new getOutTask().execute(getIntent().getStringExtra(COURSE_ID));
    }

    //toolbar中添加Action按钮
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_classroom,menu);
        return true;
    }

    //toolbar中菜单选项的点击事件
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()){
            //在线列表
            case R.id.list:
                MyApplication.getSocket().emit("list", new Ack() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    JSONArray jsonArray = new JSONArray(args[0].toString());
                                    String items[] = new String[jsonArray.length()];
                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        items[i] = jsonObject.getString("name");
                                        LogUtil.d("List",jsonObject.getString("name"));
                                    }
                                    AlertDialog dialog = new AlertDialog.Builder(ClassroomActivity.this)
                                            .setTitle("在线列表")//设置对话框的标题
                                            .setItems(items, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).setCancelable(false)
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).create();
                                    dialog.show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                //MyApplication.getSocket().emit("list");
                break;

            //发试卷
            case R.id.paper:
                MyApplication.getSocket().emit("pre_distribute_paper", MyApplication.getAuthenticatedId(), new Ack() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    JSONArray jsonArray = new JSONArray(args[0].toString());
                                    final String items[] = new String[jsonArray.length()];
                                    final String ids[] = new String[jsonArray.length()];
                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        items[i] = jsonObject.getString("papername");
                                        ids[i]  = jsonObject.getString("_id");
                                        //LogUtil.d("List",jsonObject.getString("papername"));
                                    }
                                    AlertDialog dialog = new AlertDialog.Builder(ClassroomActivity.this)
                                            .setTitle("试卷列表")//设置对话框的标题
                                            .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    selectedPaperIndex = which;
                                                    LogUtil.d("id0",String.valueOf(which));
                                                }
                                            })
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    LogUtil.d("id1",String.valueOf(selectedPaperIndex));
                                                    MyApplication.getSocket().emit("distribute_paper",ids[selectedPaperIndex],3000);
                                                    Intent intent = new Intent(ClassroomActivity.this,PaperDetailActivity.class);
                                                    intent.putExtra(PaperDetailActivity.PAPER_NAME,items[selectedPaperIndex]);
                                                    startActivity(intent);
                                                    selectedPaperIndex = 0;
                                                    dialog.dismiss();

                                                }
                                            })
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    selectedPaperIndex = 0;
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create();
                                    dialog.show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

                break;
            //离开教室
            case R.id.out:
                //1.弹对话框提示
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提醒");
                builder.setMessage("你将退出教室，学生将被强制离开教室！");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
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

            default:
        }
        return true;
    }


    //重写返回按键
    @Override
    public void onBackPressed() {
        //1.弹对话框提示
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提醒");
        builder.setMessage("你将退出教室，学生将被强制离开教室！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void initView(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("课堂 ["+getIntent().getStringExtra(COURSE_NAME)+']');
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        editInput = (EditText) findViewById(R.id.edt_input);
        btnSend = (Button) findViewById(R.id.btn_send);

        setSupportActionBar(toolbar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);
    }


    //"message" 事件处理
    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        Message message = new Message(data.getString("from"),Message.TYPE_RECEIVED,data.getString("text"));
                        messageList.add(message);
                        adapter.notifyItemInserted(messageList.size()-1);
                        recyclerView.scrollToPosition(messageList.size()-1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }//call
    };//onMessage


    //"mention" 事件处理
    private Emitter.Listener onMention = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Message message = new Message("SERVER",Message.TYPE_MENTION,args[0].toString());
                        messageList.add(message);
                        adapter.notifyItemInserted(messageList.size()-1);
                        recyclerView.scrollToPosition(messageList.size()-1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    //"list-result" 事件处理
    private Emitter.Listener onListResult = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONArray jsonArray = new JSONArray(args[0].toString());
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            LogUtil.d("List",jsonObject.getString("name"));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    //退出课堂task
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
        }
    }

}
