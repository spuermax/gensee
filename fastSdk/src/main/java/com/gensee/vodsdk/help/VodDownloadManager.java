package com.gensee.vodsdk.help;

import android.content.Context;

import com.gensee.download.ErrorCode;
import com.gensee.download.VodDownLoadEntity;
import com.gensee.download.VodDownLoader;
import com.gensee.utils.GenseeLog;

import java.util.List;

public class VodDownloadManager implements VodDownLoader.OnDownloadListener {

    private static final String TAG = "VodDownloadManager";
    private VodDownLoader mDownloader;
    private VodDownLoader.OnDownloadListener uiCallBack;
    private static VodDownloadManager ins = null;


    public static VodDownloadManager getIns() {
        if (ins == null) {
            synchronized (VodDownloadManager.class) {
                if (ins == null) {
                    ins = new VodDownloadManager();
                }
            }
        }
        return ins;
    }

    public void initDownLoader(Context context) {
        //只初始化一个下载器，回调
        if (mDownloader == null) {
            //如果需要区分多用户，请使用带用户id的instance进行初始化，默认情况下用户id为0
            mDownloader = VodDownLoader.instance(context.getApplicationContext(), this, "");
        }
        // 启动已存在且未完成的任务
        mDownloader.download();
    }


    public int download(String downloadId) {
        if (mDownloader != null) {
            return mDownloader.download(downloadId);
        }
        return ErrorCode.FAILED;
    }

    public void download() {
        if (mDownloader != null) {
            mDownloader.download();
        }
    }

    public void stop(String downLoadId) {
        if (mDownloader != null) {
            mDownloader.stop(downLoadId);
        }
    }

    public void delete(String downLoadId) {
        if (mDownloader != null) {
            mDownloader.delete(downLoadId);
        }
    }


    public void setUICallback(VodDownLoader.OnDownloadListener uiCallBack) {
        this.uiCallBack = uiCallBack;
    }

    @Override
    public void onDLFinish(String downLoadId, String localPath) {
        GenseeLog.i(TAG, "onDLFinish downLoadId = " + downLoadId + " uiCallback = " + uiCallBack);
        if (uiCallBack != null) {
            uiCallBack.onDLFinish(downLoadId, localPath);
        }
    }

    @Override
    public void onDLPosition(String downLoadId, int percent) {
        GenseeLog.i(TAG, "onDLPosition downLoadId = " + downLoadId
                + " percent = " + percent + " uiCallback = " + uiCallBack);
        if (uiCallBack != null) {
            uiCallBack.onDLPosition(downLoadId, percent);
        }
    }

    public void onDLPrepare(String downLoadId) {
        GenseeLog.i(TAG, "onDLPrepare downLoadId = " + downLoadId + " uiCallback = " + uiCallBack);
        if (uiCallBack != null) {
            uiCallBack.onDLPrepare(downLoadId);
        }
    }

    @Override
    public void onDLStart(String downLoadId) {
        GenseeLog.i(TAG, "onDLStart downLoadId = " + downLoadId + " uiCallback = " + uiCallBack);
        if (uiCallBack != null) {
            uiCallBack.onDLStart(downLoadId);
        }
    }

    @Override
    public void onDLStop(String downLoadId) {
        // 下载停止
        GenseeLog.i(TAG, "onDLStop downLoadId = " + downLoadId + " uiCallback = " + uiCallBack);
        if (uiCallBack != null) {
            uiCallBack.onDLStop(downLoadId);
        }

    }

    @Override
    public void onDLError(String downLoadId, int errorCode) {
        GenseeLog.i(TAG, "onDLError downLoadId = " + downLoadId
                + " errorCode = " + errorCode + " uiCallback = " + uiCallBack);
        if (uiCallBack != null) {
            uiCallBack.onDLError(downLoadId, errorCode);
        }
    }


    public List<VodDownLoadEntity> getDownloadList() {
        return mDownloader == null ? null : mDownloader.getDownloadList();
    }

    public void setAutoDownloadNext(boolean autoDownloadNext) {
        if (mDownloader != null) {
            mDownloader.setAutoDownloadNext(autoDownloadNext);
        }
    }
}
