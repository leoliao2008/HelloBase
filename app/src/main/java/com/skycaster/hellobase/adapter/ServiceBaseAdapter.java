package com.skycaster.hellobase.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.bean.ServiceBase;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/9/13.
 */

public class ServiceBaseAdapter extends BaseAdapter {
    private ArrayList<ServiceBase> list;
    private Context mContext;
    private int[] mColors =new int[]{Color.GREEN,Color.YELLOW,Color.BLUE};

    public ServiceBaseAdapter(ArrayList<ServiceBase> list, Context context) {
        this.list = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        int count = list.size();
        return count>0?count:1;
    }

    @Override
    public Object getItem(int position) {
        int size = list.size();
        return size>0?list.get(position):null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.item_service_base_type_2,null);
            vh=new ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }
        if(list.size()==0){
            vh.tv_maskNoData.setVisibility(View.VISIBLE);
            vh.tv_id.setVisibility(View.INVISIBLE);
            vh.mLinearLayout.setVisibility(View.INVISIBLE);
        }else {
            vh.tv_maskNoData.setVisibility(View.GONE);
            vh.tv_id.setVisibility(View.VISIBLE);
            vh.mLinearLayout.setVisibility(View.VISIBLE);

            ServiceBase temp = list.get(position);
            vh.tv_id.setText(String.format(Locale.CHINA,"%02d",temp.getId()));
            vh.tv_id.setBackgroundColor(mColors[position%3]);
            vh.tv_num.setText(String.valueOf(temp.getLdpcNum()));
            vh.tv_type.setText(String.valueOf(temp.getQamType()));
            vh.tv_size.setText(String.valueOf(temp.getIntvSize()));
            vh.tv_rate.setText(String.valueOf(temp.getLdpcRate()));
        }
        return convertView;
    }

    private class ViewHolder{
        private TextView tv_num;
        private TextView tv_rate;
        private TextView tv_size;
        private TextView tv_type;
        private TextView tv_maskNoData;
        private TextView tv_id;
        private View contentView;
        private LinearLayout mLinearLayout;

        public ViewHolder(View contentView) {
            this.contentView = contentView;
            tv_num=contentView.findViewById(R.id.item_service_base_tv_ldpc_num);
            tv_rate=contentView.findViewById(R.id.item_service_base_tv_ldpc_rate);
            tv_size=contentView.findViewById(R.id.item_service_base_tv_ldpc_size);
            tv_type=contentView.findViewById(R.id.item_service_base_tv_ldpc_type);
            tv_id=contentView.findViewById(R.id.item_service_base_tv_id);
            tv_maskNoData=contentView.findViewById(R.id.item_service_base_tv_mask_no_data);
            mLinearLayout =contentView.findViewById(R.id.item_service_base_container);
        }

        public TextView getTv_num() {
            return tv_num;
        }

        public TextView getTv_rate() {
            return tv_rate;
        }

        public TextView getTv_size() {
            return tv_size;
        }

        public TextView getTv_type() {
            return tv_type;
        }

        public View getContentView() {
            return contentView;
        }

        public TextView getTv_maskNoData() {
            return tv_maskNoData;
        }

        public TextView getTv_id() {
            return tv_id;
        }

        public LinearLayout getLinearLayout() {
            return mLinearLayout;
        }
    }
}
