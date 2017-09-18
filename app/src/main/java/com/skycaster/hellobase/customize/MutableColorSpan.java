package com.skycaster.hellobase.customize;

import android.graphics.Color;
import android.os.Parcel;
import android.support.annotation.ColorInt;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

/**
 * Created by 廖华凯 on 2017/9/18.
 */

public class MutableColorSpan extends ForegroundColorSpan {
    private int mColor;
    private int mAlpha;
    public MutableColorSpan(@ColorInt int color,int alpha) {
        super(color);
        mColor=color;
        mAlpha=alpha;
    }

    public MutableColorSpan(Parcel src) {
        super(src);
        src.readInt();
        src.readInt();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mColor);
        dest.writeInt(mAlpha);
    }

    public void setAlpha(int alpha) {
        mAlpha = alpha;
    }

    @Override
    public int getForegroundColor() {
        return Color.argb(mAlpha,Color.red(mColor),Color.green(mColor),Color.blue(mColor));
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(getForegroundColor());
    }
}
