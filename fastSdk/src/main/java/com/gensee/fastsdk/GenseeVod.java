package com.gensee.fastsdk;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.gensee.entity.ChatMsg;
import com.gensee.entity.InitParam;
import com.gensee.entity.QAMsg;
import com.gensee.entity.VodObject;
import com.gensee.vod.VodSite;
import com.gensee.vodsdk.AbsVodInfoIniter;
import com.gensee.vodsdk.VodHandler;

import java.util.List;

public class GenseeVod {

    public static int GENSEE_VOD_RESULT = 1001;

    /**
     * 在线播放
     */
    public static void startVod(final Context context, InitParam initParam, final String title) {
        VodHandler vodHandler = new VodHandler(context);
        Bundle bundle = new Bundle();
//        bundle.putString("vodId", vodId);
        bundle.putSerializable("play_param", initParam);
        bundle.putString("title", title);
        vodHandler.sendMessage(vodHandler.obtainMessage(VodHandler.ON_GET_VODOBJ_FOR_PLAY, bundle));
    }

    /**
     * 离线播放
     */
    public static void startVod(Context context, String localPath, String vodId, String title) {
        VodHandler vodHandler = new VodHandler(context);
        Bundle bundle = new Bundle();
        bundle.putString("vodId", vodId);
        bundle.putString("localPath", localPath);
        bundle.putString("title", title);
        vodHandler.sendMessage(vodHandler.obtainMessage(VodHandler.ON_GET_VODOBJ_FOR_LOCAL, bundle));
    }

}
