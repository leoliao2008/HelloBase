package com.skycaster.hellobase.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.bean.StateTable;
import com.skycaster.hellobase.bean.StateTableLabel;
import com.skycaster.hellobase.customize.MaxHeightListView;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/9/25.
 */

public class OuterStListAdapter extends BaseAdapter {
    private ArrayList<StateTableLabel> mList;
    private Context mContext;
    public OuterStListAdapter(ArrayList<StateTableLabel> list, Context context) {
        mList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.item_new_state_table_list,null);
            vh=new ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }
        final StateTableLabel label = mList.get(position);
        vh.tv_owner.setText(label.getTheOwner());
        MaxHeightListView list = vh.multi_table_view;
        list.setAdapter(new InnerStListAdapter(mContext,label.getStateTables()));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mItemClickListener!=null){
                    mItemClickListener.onItemClick(label.getStateTables().get(position));
                }
            }
        });
        return convertView;
    }

    public interface OnItemClickListener{
        void onItemClick(StateTable table);
    }

    private OnItemClickListener mItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener){
        mItemClickListener=listener;
    }

    private class ViewHolder {
        private View convertView;
        private TextView tv_owner;
        private MaxHeightListView multi_table_view;

        public ViewHolder(View convertView) {
            this.convertView = convertView;
            tv_owner=convertView.findViewById(R.id.item_state_table_list_tv_the_owner);
            multi_table_view =convertView.findViewById(R.id.item_state_table_list_lst_view);
        }
    }
}
