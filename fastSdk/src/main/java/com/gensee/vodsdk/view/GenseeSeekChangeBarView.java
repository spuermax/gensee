package com.gensee.vodsdk.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gensee.fastsdk.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


/**
 * Created by wangtao on 18/12/28.
 */

public class GenseeSeekChangeBarView extends FrameLayout {

    private static final int FADE_OUT_INFO = 0;

    private TextView mSeekInfoView;
    private ImageView mSeekIconView;
    private Handler mHandler;

    public GenseeSeekChangeBarView(Context context) {
        super(context, null);
        initView();
    }

    public GenseeSeekChangeBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GenseeSeekChangeBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        initView();
    }

    protected void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_gensee_seek_change_bar_layout, this, true);
        mSeekInfoView = findViewById(R.id.tv_seek_info);
        mSeekIconView = findViewById(R.id.tv_seek_icon);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case FADE_OUT_INFO:
                        hideInfo();
                }
            }
        };
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(FADE_OUT_INFO);
    }

    private void hideInfo() {
        startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
        setVisibility(View.INVISIBLE);
    }

    public void showInfo(boolean isFast, long time, long length) {
        updateSeekIcon(isFast);
        mSeekInfoView.setText(millisToString(time) + "/" + millisToString(length));
        setVisibility(View.VISIBLE);

        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, 1000);
    }

    public static SpannableString getCoverSeekColorText(String text, String newStr, int color) {
        StringBuffer stringBuffer = new StringBuffer(text);
        int start = stringBuffer.length();
        stringBuffer.append(newStr);
        SpannableString spannableString = new SpannableString(stringBuffer);
        spannableString.setSpan(
                new ForegroundColorSpan(color), start, stringBuffer.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    private void updateSeekIcon(boolean isFast) {
        mSeekIconView.setImageResource(isFast ? R.drawable.ic_gensee_controller_forward : R.drawable.ic_gensee_controller_back);
    }

    static String millisToString(long millis) {
        boolean negative = millis < 0;
        millis = Math.abs(millis);

        millis /= 1000;
        int sec = (int) (millis % 60);
        millis /= 60;
        int min = (int) (millis % 60);
        millis /= 60;
        int hours = (int) millis;

        String time;
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        format.applyPattern("00");

        if (millis > 0)
            time = (negative ? "-" : "") + hours + ":" + format.format(min) + ":" + format.format(sec);
        else
            time = (negative ? "-" : "") + min + ":" + format.format(sec);

        return time;
    }
}
