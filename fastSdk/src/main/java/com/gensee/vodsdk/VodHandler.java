package com.gensee.vodsdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

public class VodHandler extends Handler {
    public static final int ON_GET_VODOBJ_FOR_PLAY = 100;
    public static final int ON_GET_VODOBJ_FOR_LOCAL = 101;
    private Context mContext;

    public VodHandler(Context context) {
        this.mContext = context;
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle bundle = (Bundle) msg.obj;
        Intent intent = new Intent(mContext, VodActivity.class);
        switch (msg.what) {
            case ON_GET_VODOBJ_FOR_PLAY:
                // 在线播放
//                intent.putExtra("vodId", bundle.getString("vodId"));
                intent.putExtra("play_param", bundle.getSerializable("play_param"));
                intent.putExtra("title", bundle.getString("title"));
                ((AppCompatActivity) mContext).startActivityForResult(intent, 1);
                break;
            case ON_GET_VODOBJ_FOR_LOCAL:
                // 离线播放
                intent.putExtra("vodId", bundle.getString("vodId"));
                intent.putExtra("play_path", bundle.getString("localPath"));
                intent.putExtra("title", bundle.getString("title"));
                ((AppCompatActivity) mContext).startActivityForResult(intent, 1);
                break;
        }
    }
}
