package com.gensee.vodsdk.help;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 本地 记忆播放
 * Created by wangtao on 2018/12/21.
 */
public class VodRecordHelper {

    private String FILE_NAME = "gensee_vod_record";

    private SharedPreferences preference;
    private Context mContext;
    private String vodId;

    public VodRecordHelper(Context context, String vodId) {
        this.mContext = context;
        this.vodId = vodId;
        this.preference = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存进度
     *
     * @param position
     */
    public void save(int position) {
        if (!TextUtils.isEmpty(vodId)) {
            this.preference.edit().putInt(vodId, position).apply();
        }
    }

    /**
     * 获取进度
     *
     * @return
     */
    public int get() {
        if (!TextUtils.isEmpty(vodId)) {
            return this.preference.getInt(vodId, 0);
        } else {
            return 0;
        }
    }
}
