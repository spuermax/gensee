package com.gensee.vodsdk.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.TextView;

import com.gensee.fastsdk.R;

import java.util.Timer;
import java.util.TimerTask;

public class GenseeLoadDialog extends Dialog {

    private TextView loading_txt;

    public static GenseeLoadDialog create(Context context) {
        return new GenseeLoadDialog(context, R.style.gs_loadDialogTheme);
    }

    private GenseeLoadDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_gensee_load);
        initView();
    }

    public GenseeLoadDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_gensee_load);
        initView();
    }

    private void initView() {
        loading_txt = findViewById(R.id.loading_txt);
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        //屏蔽Back
//        setOnKeyListener(new OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
    }

    public void setTextVisible(int visible) {
        loading_txt.setVisibility(visible);
    }

    public void setMessage(String message) {
        loading_txt.setText(message);
    }
}
