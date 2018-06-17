package com.horizonshd.www.barrageclassteacher;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PaperAdapter extends RecyclerView.Adapter<PaperAdapter.ViewHolder> {

    private Context mContext;
    private static List<Paper> mPaperList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_papername;
        public ViewHolder(View view){
            super(view);
            txt_papername = (TextView) view.findViewById(R.id.txt_papername);
        }
    }

    public PaperAdapter(List<Paper> paperList){mPaperList = paperList;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paper_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Paper paper = mPaperList.get(position);
        holder.txt_papername.setText(paper.getPapername().trim());

        holder.txt_papername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入试卷详情页
                Intent intent = new Intent(mContext,PaperDetailActivity.class);
                intent.putExtra(PaperDetailActivity.PAPER_NAME,paper.getPapername());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPaperList.size();
    }
}
