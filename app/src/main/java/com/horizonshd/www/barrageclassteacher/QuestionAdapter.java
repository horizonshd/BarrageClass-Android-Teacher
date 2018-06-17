package com.horizonshd.www.barrageclassteacher;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private Context mContext;
    private List<Question> mQuestionList;
    private static Boolean showCheckbox = false;

    protected static List<String> questionIDList = new ArrayList<>();


    public static void setShowCheckbox(Boolean isShow){
        showCheckbox = isShow;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_description;
        CheckBox checkBox;

        public ViewHolder(View view){
            super(view);
            txt_description = (TextView) view.findViewById(R.id.question_description);
            checkBox = (CheckBox) view.findViewById(R.id.cb);
        }
    }

    // 构造函数
    QuestionAdapter(List<Question> questionList) {
        mQuestionList = questionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item,parent,false);
        //ViewHolder holder = new ViewHolder(view);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Question question = mQuestionList.get(position);
        holder.txt_description.setText(question.getDescription());
        if(showCheckbox) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        questionIDList.add(question.getQuestionid());
                    }else {
                        questionIDList.remove(question.getQuestionid());
                    }
                }
            });

        }else {
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.txt_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,QuestionDetailActivity.class);
                intent.putExtra(QuestionDetailActivity.QUESTION_PASS,question);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mQuestionList.size();
    }
}
