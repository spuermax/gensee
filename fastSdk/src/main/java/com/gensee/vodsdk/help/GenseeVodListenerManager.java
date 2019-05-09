package com.gensee.vodsdk.help;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/**
 * Created by wangtao on 2018/12/29.
 */
public class GenseeVodListenerManager {
    private Context mContext;
    private OnGenseeVodListener mListener;
    private OnRefreshStatusListener listener;
    WeakReference<OnGenseeVodListener> mListenerWeakReference;
    private static GenseeVodListenerManager manager;

    private GenseeVodListenerManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static GenseeVodListenerManager getInstance(Context context) {
        if (manager == null) {
            manager = new GenseeVodListenerManager(context);
        }
        return manager;
    }

    public void setOnGenseeVodCallBack(OnGenseeVodListener mListener) {
        mListenerWeakReference = new WeakReference<>(mListener);
        this.mListener = mListenerWeakReference.get();
    }

    public void onStartDownLoad(AppCompatActivity activity, String vodId, long length) {
        if (mListener != null) {
            mListener.onStartDownload(activity, vodId, length);
        }
    }


    //刷新状态
    public void setOnRefreshStatus(OnRefreshStatusListener mListener) {
        this.listener = mListener;
    }

    public void OnRefreshStatusListener() {
        if (listener != null) {
            listener.onStatus();
        }
    }


    public interface OnGenseeVodListener {
        /**
         * @param vodId  回放id
         * @param length 回放总长度 毫秒
         */
        void onStartDownload(AppCompatActivity activity, String vodId, long length);
    }

    public interface OnRefreshStatusListener {
        void onStatus();
    }
}
