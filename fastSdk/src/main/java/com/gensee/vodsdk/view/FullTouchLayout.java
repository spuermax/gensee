package com.gensee.vodsdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.gensee.view.GSDocViewGx;

/**
 * Created by wangtao on 2018/12/27.
 */
public class FullTouchLayout extends GSDocViewGx {

    private OnTouchCallBack onTouchCallBack;

    public FullTouchLayout(Context context) {
        super(context);
    }

    public FullTouchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullTouchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnTouchCallBack(OnTouchCallBack onTouchCallBack) {
        this.onTouchCallBack = onTouchCallBack;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onTouchCallBack.onTouch();
        }
        return super.dispatchTouchEvent(ev);
    }


    public interface OnTouchCallBack {
        void onTouch();
    }
}
