package com.gensee.vodsdk;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.gensee.entity.ChatMsg;
import com.gensee.entity.InitParam;
import com.gensee.entity.QAMsg;
import com.gensee.entity.VodObject;
import com.gensee.utils.GenseeLog;
import com.gensee.vod.VodSite;
import com.gensee.vod.VodSite.OnVodListener;

import java.util.List;

/**
 * 点播信息初始化,主要对VodSite、OnVodListener进行了封装，在线播放和下载之前的基本信息准备
 * 1 onVodObject响应之后方可进行播放或下载，请不要在onVodObject响应之前，用id进行播放或下载。
 * 2 若还需要点播的其他信息，如果历史聊天记录、历史问答记录和点播更多详细信息，也请在onVodObject响应之后开始调用
 * 例如 调用  vodSite.getChatHistory(vodId, 1);//获取聊天历史记录，起始页码1
 * vodSite.getQaHistory(vodId, 1);//获取问答历史记录，起始页码1
 * <p>
 * 3 处理了公共的错误码，onErr
 * 3 所有响应都是非UI线程，不要在回调里面直接操作界面
 *
 * @author Administrator
 */
public abstract class AbsVodInfoIniter implements OnVodListener {

    private static final String TAG = "AbsVodlistener";
    protected VodSite vodSite;
    private Handler handler;
    private Context context;

    protected AbsVodInfoIniter(Context context, Handler handler) {
        this.handler = handler;
        this.context = context;
    }

    public void getVodObject(InitParam initParam) {
        vodSite = new VodSite(context);
        vodSite.setVodListener(this);
        vodSite.getVodObject(initParam);
    }

    /**
     * AbsVodInfoIniter实例不再使用的时候可以调用，
     * 调用后若还要使用则重新创建AbsVodInfoIniter实例
     */
    public void shutdown() {
        if (vodSite != null) {
            vodSite.setVodListener(null);
            vodSite = null;
        }
        context = null;
        handler = null;

    }
    /********************* OnVodListener **************************/
    /**
     * 聊天记录 getChatHistory 响应 vodId 点播id list 聊天记录
     */
    @Override
    public void onChatHistory(String vodId, List<ChatMsg> list, int pageIndex, boolean more) {
        GenseeLog.d(TAG, "onChatHistory vodId = " + vodId + " " + list);
        // ChatMsg msg;
        // msg.getContent();//消息内容
        // msg.getSenderId();//发送者用户id
        // msg.getSender();//发送者昵称
        // msg.getTimeStamp();//发送时间，单位毫秒
    }

    /**
     * 问答记录 getQaHistory 响应 list 问答记录 vodId 点播id
     */
    @Override
    public void onQaHistory(String vodId, List<QAMsg> list, int pageIndex, boolean more) {
        GenseeLog.d(TAG, "onQaHistory vodId = " + vodId + " " + list);
        if (more) {
            // 如果还有更多的历史，还可以继续获取记录（pageIndex+1）页的记录
        }
        // QAMsg msg;
        // msg.getQuestion();//问题
        // msg.getQuestId();//问题id
        // msg.getQuestOwnerId();//提问人id
        // msg.getQuestOwnerName();//提问人昵称
        // msg.getQuestTimgstamp();//提问时间 单位毫秒
        //
        // msg.getAnswer();//回复的内容
        // msg.getAnswerId();//“本条回复”的id 不是回答者的用户id
        // msg.getAnswerOwner();//回复人的昵称
        // msg.getAnswerTimestamp();//回复时间 单位毫秒
    }

    /**
     * 获取点播详情
     */
    @Override
    public void onVodDetail(VodObject vodObj) {
        GenseeLog.d(TAG, "onVodDetail " + vodObj);
        if (vodObj != null) {
            vodObj.getDuration();// 时长
            vodObj.getEndTime();// 录制结束时间 始于1970的utc时间毫秒数
            vodObj.getStartTime();// 录制开始时间 始于1970的utc时间毫秒数
            vodObj.getStorage();// 大小 单位为Byte
        }
    }

    // int ERR_DOMAIN = -100; // ip(domain)不正确
    // int ERR_TIME_OUT = -101; // 超时
    // int ERR_UNKNOWN = -102; // 未知错误
    // int ERR_SITE_UNUSED = -103; // 站点不可用
    // int ERR_UN_NET = -104; // 无网络
    // int ERR_DATA_TIMEOUT = -105; // 数据过期
    // int ERR_SERVICE = -106; // 服务不正确
    // int ERR_PARAM = -107; // 参数不正确
    // int ERR_THIRD_CERTIFICATION_AUTHORITY = -108 //第三方认证失败
    // int ERR_UN_INVOKE_GETOBJECT = -201; //没有调用getVodObject
    // int ERR_VOD_INTI_FAIL = 14; //点播getVodObject失败
    // int ERR_VOD_NUM_UNEXIST = 15; //点播编号不存在或点播不存在
    // int ERR_VOD_PWD_ERR = 16; //点播密码错误
    // int ERR_VOD_ACC_PWD_ERR = 17; //帐号或帐号密码错误
    // int ERR_UNSURPORT_MOBILE = 18; //不支持移动设备
    @Override
    public void onVodErr(final int errCode) {
        if (handler != null) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    String msg = getErrMsg(errCode);
                    if (context != null) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


//	/**
//	 * getVodObject的响应，vodId 接下来可以下载后播放
//	 */
//	@Override
//	public void onVodObject(String vodId) {
//		GenseeLog.d(TAG, "onVodObject vodId = " + vodId);
//		
//		mHandler.sendMessage(mHandler
//				.obtainMessage(RESULT.ON_GET_VODOBJ, vodId));

//		vodSite.getChatHistory(vodId, 1);//获取聊天历史记录，起始页码1
//		vodSite.getQaHistory(vodId, 1);//获取问答历史记录，起始页码1
//	}

    /**
     * 错误码处理
     *
     * @param errCode 错误码
     * @return 错误码文字表示内容
     */
    private String getErrMsg(int errCode) {
        String msg = "";
        switch (errCode) {
            case ERR_DOMAIN:
                msg = "domain 不正确";
                break;
            case ERR_TIME_OUT:
                msg = "超时";
                break;
            case ERR_SITE_UNUSED:
                msg = "站点不可用";
                break;
            case ERR_UN_NET:
                msg = "无网络请检查网络连接";
                break;
            case ERR_DATA_TIMEOUT:
                msg = "数据过期";
                break;
            case ERR_SERVICE:
                msg = "请检查填写的serviceType";
                break;
            case ERR_PARAM:
                msg = "请检查参数";
                break;
            case ERR_UN_INVOKE_GETOBJECT:
                msg = "请先调用getVodObject";
                break;
            case ERR_VOD_INTI_FAIL:
                msg = "调用getVodObject失败";
                break;
            case ERR_VOD_NUM_UNEXIST:
                msg = "点播编号不存在或点播不存在";
                break;
            case ERR_VOD_PWD_ERR:
                msg = "点播密码错误";
                break;
            case ERR_VOD_ACC_PWD_ERR:
                msg = "登录帐号或登录密码错误";
                break;
            case ERR_UNSURPORT_MOBILE:
                msg = "不支持移动设备";
                break;
            case ERR_THIRD_CERTIFICATION_AUTHORITY:
                msg = "第三方认证失败";
                break;
            default:
                msg = "初始化错误，错误码：" + errCode + " 请对照文档说明";
                break;
        }
        return msg;
    }


}
