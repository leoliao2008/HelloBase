package com.skycaster.hellobase.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.bean.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/10/16.
 */

public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.ViewHolder> {
    private ArrayList<Log> list;
    private Context mContext;
    private SimpleDateFormat mSimpleDateFormat;

    public LogListAdapter(ArrayList<Log> list, Context context) {
        this.list = list;
        this.list.sort(new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                return o1.getRecordTime().compareTo(o2.getRecordTime());
            }
        });
        mContext = context;
        mSimpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒", Locale.CHINA);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(mContext,R.layout.item_log_list,null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log temp = list.get(position);
        holder.tv_record.setText(temp.getNotes());
        holder.tv_date.setText(mSimpleDateFormat.format(temp.getRecordTime()));
        holder.tv_index.setText(String.format(Locale.CHINA,"%02d",position+1));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_date;
        private TextView tv_record;
        private TextView tv_index;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_date=itemView.findViewById(R.id.item_log_tv_date);
            tv_record=itemView.findViewById(R.id.item_log_tv_record);
            tv_index=itemView.findViewById(R.id.item_log_tv_index);
        }
    }
}
