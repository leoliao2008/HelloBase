package com.skycaster.hellobase.customize;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;

/**
 * Created by 廖华凯 on 2017/9/18.
 */

public class MutableSizeSpan extends AbsoluteSizeSpan {

    private boolean mDip;
    private int mSize;


    public MutableSizeSpan(int size) {
        this(size,false);
    }

    public MutableSizeSpan(int size, boolean dip) {
        super(size, dip);
        mDip=dip;
        mSize=size;
    }

    public MutableSizeSpan(Parcel src) {
        super(src);
        mDip=src.readInt()!=0;
        mSize=src.readInt();
    }

    @Override
    public int getSize() {
        return mSize;
    }

    public void setSize(int size){
        mSize=size;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        if (mDip) {
            ds.setTextSize(mSize * ds.density);
        } else {
            ds.setTextSize(mSize);
        }
    }

    @Override
    public void updateMeasureState(TextPaint ds) {
        if (mDip) {
            ds.setTextSize(mSize * ds.density);
        } else {
            ds.setTextSize(mSize);
        }
    }
}
