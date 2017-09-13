package com.skycaster.hellobase.customize;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 廖华凯 on 2017/9/11.
 */

public class TwinklingTextView extends AppCompatTextView {

    private TextPaint mTextPaint;
    private LinearGradient mLinearGradient;
    private int[] mColors=new int[]{Color.RED,Color.YELLOW,Color.BLUE};
    private Matrix mMatrix;
    private int mOffSet;
    private AtomicBoolean isContinueTwinkling =new AtomicBoolean(false);


    public TwinklingTextView(Context context) {
        this(context,null);
    }

    public TwinklingTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TwinklingTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mTextPaint!=null&&isContinueTwinkling.get()){
            mOffSet+=getMeasuredWidth()/20;
            mMatrix.setTranslate(mOffSet,0);
            mLinearGradient.setLocalMatrix(mMatrix);
            postInvalidateDelayed(50);
        }
    }

    public void startTwinkling(){
        isContinueTwinkling.set(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mTextPaint=getPaint();
                    mLinearGradient=new LinearGradient(
                            0,0,getMeasuredWidth(),0,mColors,null, Shader.TileMode.MIRROR
                    );
                    mTextPaint.setShader(mLinearGradient);
                    mMatrix=new Matrix();
                    invalidate();
                }
            });
        }
    }


    public void stopTwinkling(){
        isContinueTwinkling.set(false);
    }
}
