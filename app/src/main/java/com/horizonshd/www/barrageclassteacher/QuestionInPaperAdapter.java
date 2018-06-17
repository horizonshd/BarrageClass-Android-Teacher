package com.horizonshd.www.barrageclassteacher;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class QuestionInPaperAdapter extends RecyclerView.Adapter<QuestionInPaperAdapter.ViewHolder> {

    private Context mContext;
    private List<QuestionInPaper> mQuestionInPaperList;

    QuestionInPaperAdapter(List<QuestionInPaper> questionInPaperList){
        mQuestionInPaperList = questionInPaperList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_description;
        TextView txt_optiona;
        TextView txt_optionb;
        TextView txt_optionc;
        TextView txt_optiond;
        TextView txt_answer;
       public ViewHolder(View view){
           super(view);
           txt_description = (TextView) view.findViewById(R.id.txt_description);
           txt_optiona = (TextView) view.findViewById(R.id.txt_optiona);
           txt_optionb = (TextView) view.findViewById(R.id.txt_optionb);
           txt_optionc = (TextView) view.findViewById(R.id.txt_optionc);
           txt_optiond = (TextView) view.findViewById(R.id.txt_optiond);
           txt_answer = (TextView) view.findViewById(R.id.txt_answer);
       }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_in_paper_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final QuestionInPaper questionInPaper = mQuestionInPaperList.get(position);
        holder.txt_description.setText(questionInPaper.getDescription());
        holder.txt_optiona.setText(questionInPaper.getOptiona());
        holder.txt_optionb.setText(questionInPaper.getOptionb());
        holder.txt_optionc.setText(questionInPaper.getOptionc());
        holder.txt_optiond.setText(questionInPaper.getOptiond());
        holder.txt_answer.setText(questionInPaper.getAnswer());
    }

    @Override
    public int getItemCount() {
        return mQuestionInPaperList.size();
    }
}
