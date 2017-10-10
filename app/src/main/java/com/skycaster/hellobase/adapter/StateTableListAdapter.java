package com.skycaster.hellobase.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.bean.StateTable;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/9/25.
 */

public class StateTableListAdapter extends BaseAdapter {
    private ArrayList<StateTable> mList;
    private Context mContext;

    public StateTableListAdapter(ArrayList<StateTable> list, Context context) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.item_state_table_list,null);
            vh=new ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }
        StateTable stateTable = mList.get(position);
        vh.tv_version.setText(String.valueOf(stateTable.getCurVer()));
        vh.tv_id.setText(stateTable.getHostId());
        vh.tv_state.setText(stateTable.getRunningState());
        vh.tv_owner.setText(stateTable.getTheOwner());
        return convertView;
    }

    private class ViewHolder {
        private View convertView;
        private TextView tv_id;
        private TextView tv_state;
        private TextView tv_version;
        private TextView tv_owner;

        public ViewHolder(View convertView) {
            this.convertView = convertView;
            tv_id=convertView.findViewById(R.id.item_state_table_list_tv_id);
            tv_state= convertView.findViewById(R.id.item_state_table_list_tv_status);
            tv_version=convertView.findViewById(R.id.item_state_table_list_tv_version);
            tv_owner=convertView.findViewById(R.id.item_state_table_list_tv_the_owner);
        }
    }
}
