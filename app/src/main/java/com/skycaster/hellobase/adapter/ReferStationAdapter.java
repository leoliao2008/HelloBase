package com.skycaster.hellobase.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.bean.ReferentialStation;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/9/13.
 */

public class ReferStationAdapter extends BaseAdapter {
    private ArrayList<ReferentialStation> list;
    private Context mContext;
    private int[] mColors =new int[]{Color.parseColor("#FFFF00"),Color.parseColor("#0E7038"),Color.parseColor("#FFAA25")};
    private ReferStationAdapter.CallBack mCallBack;
    private boolean isEditMode =false;

    public ReferStationAdapter(ArrayList<ReferentialStation> list, Context context, CallBack callBack) {
        this.list = list;
        mContext = context;
        mCallBack=callBack;
    }

    public void toggleMode(boolean isEditMode){
        this.isEditMode =isEditMode;
        notifyDataSetChanged();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.item_service_base,null);
            vh=new ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }
        if(list.size()==0){
            vh.tv_maskNoData.setVisibility(View.VISIBLE);
            vh.tv_id.setVisibility(View.INVISIBLE);
            vh.mRelativeLayout.setVisibility(View.INVISIBLE);
        }else {
            vh.tv_maskNoData.setVisibility(View.GONE);
            vh.tv_id.setVisibility(View.VISIBLE);
            vh.mRelativeLayout.setVisibility(View.VISIBLE);
            final ReferentialStation temp = list.get(position);
            vh.tv_id.setText(String.format(Locale.CHINA,"%02d",temp.getId()));
            vh.tv_id.setBackgroundColor(mColors[position%3]);
            vh.tv_num.setText(String.valueOf(temp.getLdpcNum()));
            vh.tv_type.setText(String.valueOf(temp.getQamType()));
            vh.tv_size.setText(String.valueOf(temp.getIntvSize()));
            vh.tv_rate.setText(String.valueOf(temp.getLdpcRate()));
            if(isEditMode){
                vh.iv_more.setVisibility(View.GONE);
            }else {
                vh.iv_more.setVisibility(View.VISIBLE);
                vh.iv_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isEditMode){
                            mCallBack.onPressSettingIcon(position,temp);
                        }else {
                            mCallBack.onPressMoreIcon(position,temp);
                        }

                    }
                });
            }
        }
        return convertView;
    }

    public interface CallBack{
        void onPressMoreIcon(int position,ReferentialStation referentialStation);

        void onPressSettingIcon(int position, ReferentialStation referentialStation);
    }

    private class ViewHolder{
        private TextView tv_num;
        private TextView tv_rate;
        private TextView tv_size;
        private TextView tv_type;
        private TextView tv_maskNoData;
        private TextView tv_id;
        private ImageView iv_more;
        private View contentView;
        private RelativeLayout mRelativeLayout;

        public ViewHolder(View contentView) {
            this.contentView = contentView;
            tv_num=contentView.findViewById(R.id.item_service_base_tv_ldpc_num);
            tv_rate=contentView.findViewById(R.id.item_service_base_tv_ldpc_rate);
            tv_size=contentView.findViewById(R.id.item_service_base_tv_ldpc_size);
            tv_type=contentView.findViewById(R.id.item_service_base_tv_ldpc_type);
            tv_id=contentView.findViewById(R.id.item_service_base_tv_id);
            iv_more=contentView.findViewById(R.id.item_service_base_tv_more);
            tv_maskNoData=contentView.findViewById(R.id.item_service_base_tv_mask_no_data);
            mRelativeLayout =contentView.findViewById(R.id.item_service_base_container);
        }
    }
}
