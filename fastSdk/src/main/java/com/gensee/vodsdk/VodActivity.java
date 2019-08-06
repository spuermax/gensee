package com.gensee.vodsdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gensee.doc.OnDocViewEventListener;
import com.gensee.download.VodDownLoadEntity;
import com.gensee.download.VodDownLoadStatus;
import com.gensee.entity.ChatMsg;
import com.gensee.entity.DocInfo;
import com.gensee.entity.InitParam;
import com.gensee.entity.PageInfo;
import com.gensee.fastsdk.GenseeVod;
import com.gensee.fastsdk.R;

import com.gensee.media.PlaySpeed;
import com.gensee.media.VODPlayer;
import com.gensee.pdu.IGSDocView;
import com.gensee.utils.StringUtil;
import com.gensee.view.GSVideoView;
import com.gensee.vodsdk.help.GenseeVodListenerManager;
import com.gensee.vodsdk.help.VodDownloadManager;
import com.gensee.vodsdk.help.VodRecordHelper;
import com.gensee.vodsdk.help.VodVideoTouchHelper;
import com.gensee.vodsdk.view.FullTouchLayout;
import com.gensee.vodsdk.view.GenseeLoadDialog;
import com.gensee.vodsdk.view.GenseeSeekChangeBarView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VodActivity extends AppCompatActivity implements OnDocViewEventListener,
        VODPlayer.OnVodPlayListener, View.OnClickListener {

    private static final int DEFAULT_FADE_OUT_TIME = 5000;

    private OnclickListener onclickListener;

    private Context mContext;
    private VODPlayer mGSOLPlayer;
    private GSVideoView mGSVideoView;
    private FullTouchLayout mDocView;
    private SeekBar mSeekBarPlayViedo;
    private ImageView ivPlay;
    private ChapterListAdapter chapterListAdapter;

    private RelativeLayout rlToolbar;
    private ImageView ivBack;
    private TextView tvSpeed;
    private TextView tvTitle;
    private TextView tvControllerTime;
    private View llControllerBar;
    private GenseeSeekChangeBarView seekChangeBarView;

    private RelativeLayout rlNoFullToolbar;
    private ImageView tvDownload;
    private ImageView ivFullVideo;
    private ImageView ivFullDoc;
    private ImageView ivControllerDownload;

    private LinearLayout llFullToolbar;
    private ImageView ivFullBack;
    private TextView tvFullTitle;
    private ImageView iv_share;

    private boolean isTouch = false;

    private static final String DURATION = "DURATION";

    private int VIEDOPAUSEPALY = 0;
    private int SHOW = 0;
    private int HIDE = 1;

    private List<ChapterInfo> chapterList;
    private int mControllerStatus = SHOW;

    private String mTitle;
    private String mVodId = "";
    private String mLocalpath;
    private InitParam mInitParam;
    private boolean isHaveDownload = false;
    private int nowPosition; //当前播放进度
    private int maxPosition; //最大进度
    private boolean mIsFullScreen = false; //全屏

    private VodRecordHelper vodRecordHelper;
    private VodVideoTouchHelper vodVideoTouchHelper;
    private String[] arrSpeed = new String[]{"1X", "1.5X", "1.75X", "2X"};

    private GenseeLoadDialog loadDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod);
        mContext = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mTitle = getIntent().getStringExtra("title");
        mVodId = getIntent().getStringExtra("vodId");
        mLocalpath = getIntent().getStringExtra("play_path");
        mInitParam = (InitParam) getIntent().getSerializableExtra("play_param");
        onclickListener = (OnclickListener) getIntent().getSerializableExtra("onClick");

        initView();

        tvTitle.setText(mTitle);
        tvFullTitle.setText(mTitle);
        tvSpeed.setText(arrSpeed[0]);

        tvDownload.setOnClickListener(this);
        ivFullVideo.setOnClickListener(this);
        ivFullDoc.setOnClickListener(this);
        ivControllerDownload.setOnClickListener(this);
        ivFullBack.setOnClickListener(this);
        iv_share.setOnClickListener(this);

        ivBack.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        tvSpeed.setOnClickListener(this);

        vodVideoTouchHelper = new VodVideoTouchHelper(this);

        chapterListAdapter = new ChapterListAdapter();
        chapterList = new ArrayList<>();

        mSeekBarPlayViedo.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mDocView.setOnDocViewClickedListener(this);


        initPlayer();
        initListener();
        myHandler.sendEmptyMessageDelayed(MSG.FADE_OUT, DEFAULT_FADE_OUT_TIME);

        GenseeVodListenerManager.getInstance(this).setOnRefreshStatus(new GenseeVodListenerManager.OnRefreshStatusListener() {
            @Override
            public void onStatus() {
                setDownloadStatus();
            }
        });
    }

    private void initView() {
        mGSVideoView = findViewById(R.id.video_view);
        mDocView = findViewById(R.id.doc_view);
        mSeekBarPlayViedo = findViewById(R.id.seekbarpalyviedo);
        tvControllerTime = findViewById(R.id.tv_controller_time);
        llControllerBar = findViewById(R.id.ll_controller_bar);
        seekChangeBarView = findViewById(R.id.seek_change_view);

        rlToolbar = findViewById(R.id.rl_toolbar);
        ivBack = findViewById(R.id.iv_back);
        tvSpeed = findViewById(R.id.tv_speed);
        tvTitle = findViewById(R.id.tv_title);
        ivPlay = findViewById(R.id.iv_play);
        rlNoFullToolbar = findViewById(R.id.rl_no_full_toolbar);
        llFullToolbar = findViewById(R.id.ll_full_toolbar);
        tvFullTitle = findViewById(R.id.tv_full_title);
        ivFullBack = findViewById(R.id.iv_full_back);
        iv_share = findViewById(R.id.iv_share);

        tvDownload = findViewById(R.id.tv_download);
        ivFullVideo = findViewById(R.id.iv_fullscreen_video);
        ivFullDoc = findViewById(R.id.iv_fullscreen_doc);
        ivControllerDownload = findViewById(R.id.iv_controller_download);

        loadDialog = GenseeLoadDialog.create(mContext);
        loadDialog.show();

        tvSpeed.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
    }

    private void initPlayer() {
        if (TextUtils.isEmpty(mVodId)) {
            AbsVodInfoIniter playInfoIniter = new AbsVodInfoIniter(mContext, new Handler()) {
                @Override
                public void getVodObject(InitParam initParam) {
                    if (vodSite != null) {
                        vodSite.setVodListener(null);
                    }
                    super.getVodObject(initParam);
                }

                @Override
                public void onVodObject(String vodId) {
                    mVodId = vodId;
                    initPlayer();
                }

                @Override
                public void onVodErr(int errCode) {
                    super.onVodErr(errCode);
                    finish();
                }
            };

            playInfoIniter.getVodObject(mInitParam);
        } else {
            vodRecordHelper = new VodRecordHelper(this, mVodId);

            String vodIdOrLocalPath = getVodIdOrLocalPath();
            if (vodIdOrLocalPath == null) {
                Toast.makeText(this, "路径不对", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mGSOLPlayer == null) {
                mGSOLPlayer = new VODPlayer();
                mGSOLPlayer.setGSVideoView(mGSVideoView);
                mGSOLPlayer.setGSDocViewGx(mDocView);
                mGSOLPlayer.play(vodIdOrLocalPath, this, "", false);
            }
        }

    }

    private void initListener() {
        mGSVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                vodVideoTouchHelper.updateTimeLength(mSeekBarPlayViedo.getProgress(), mSeekBarPlayViedo.getMax());
                if (vodVideoTouchHelper.onTouchEvent(event)) {
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setSeekBarShow();
                }
                return true;
            }
        });

        mDocView.setOnTouchCallBack(new FullTouchLayout.OnTouchCallBack() {
            @Override
            public void onTouch() {
                if (mIsFullScreen && !mGSVideoView.isShown()) {
                    setSeekBarShow();
                }
            }
        });
    }

    private String getVodIdOrLocalPath() {
        String vodIdOrLocalPath = null;
        if (!TextUtils.isEmpty(mLocalpath)) {
            vodIdOrLocalPath = mLocalpath;
            tvDownload.setVisibility(View.GONE);
            ivControllerDownload.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(mVodId)) {
            vodIdOrLocalPath = mVodId;
            setDownloadStatus();
        }
        return vodIdOrLocalPath;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back || v.getId() == R.id.iv_full_back) {
            onBackPressed();
        } else if (v.getId() == R.id.iv_play) {
            if (VIEDOPAUSEPALY == 0) {
                mGSOLPlayer.pause();
                ivPlay.setSelected(false);
            } else if (VIEDOPAUSEPALY == 1) {
                mGSOLPlayer.resume();
                ivPlay.setSelected(true);
            } else if (VIEDOPAUSEPALY == 2) {
                ivPlay.setSelected(true);
            }
        } else if (v.getId() == R.id.tv_speed) {
            String speed = tvSpeed.getText().toString();
            if (arrSpeed[0].equals(speed)) {
                tvSpeed.setText(arrSpeed[1]);
                mGSOLPlayer.setSpeed(PlaySpeed.SPEED_150, null);
            } else if (arrSpeed[1].equals(speed)) {
                tvSpeed.setText(arrSpeed[2]);
                mGSOLPlayer.setSpeed(PlaySpeed.SPEED_175, null);
            } else if (arrSpeed[2].equals(speed)) {
                tvSpeed.setText(arrSpeed[3]);
                mGSOLPlayer.setSpeed(PlaySpeed.SPEED_200, null);
            } else if (arrSpeed[3].equals(speed)) {
                tvSpeed.setText(arrSpeed[0]);
                mGSOLPlayer.setSpeed(PlaySpeed.SPEED_NORMAL, null);
            }
        } else if (v.getId() == R.id.iv_fullscreen_video) {
            setFullScreen(true);
        } else if (v.getId() == R.id.iv_fullscreen_doc) {
            setFullScreen(false);
        } else if (v.getId() == R.id.tv_download || v.getId() == R.id.iv_controller_download) {
            if (isHaveDownload) {
                shortToast(mContext, "已加入下载，请到我的下载查看");
                return;
            }
            if (mSeekBarPlayViedo.getMax() > 0) {
                GenseeVodListenerManager.getInstance(this).onStartDownLoad(VodActivity.this, mVodId, mSeekBarPlayViedo.getMax());
            }

            setDownloadStatus();
        } else if (v.getId() == R.id.iv_share) {
            onclickListener.onShareClick();
        }
    }

    //进度条
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateTime(progress, seekBar.getMax());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isTouch = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (null != mGSOLPlayer) {
                int pos = seekBar.getProgress();
                setSeek(pos);
                showSeekChange(pos);
            }
        }
    };

    /**
     * 更新时间
     */
    private void updateTime(int position, int duration) {
        tvControllerTime.setText(String.format("%s/%s", getTime(position), getTime(duration)));
    }

    /**
     * 设置进度
     */
    public void setSeek(int position) {
        mGSOLPlayer.seekTo(position);
    }

    /**
     * 显示/隐藏 进度条
     */
    private void setSeekBarShow() {
        if (mControllerStatus == HIDE) {
            myHandler.sendEmptyMessage(MSG.SHOW_CONTROLLER);
        } else if (mControllerStatus == SHOW) {
            myHandler.removeMessages(MSG.FADE_OUT);
            myHandler.sendEmptyMessage(MSG.FADE_OUT);
        }
    }

    /**
     * 进度提示
     */
    public void showSeekChange(int position) {
//        int nowProgress = mSeekBarPlayViedo.getProgress();
        if (position < nowPosition) {
            //回退
            seekChangeBarView.showInfo(false, position, mSeekBarPlayViedo.getMax());
        } else {
            //快进
            seekChangeBarView.showInfo(true, position, mSeekBarPlayViedo.getMax());
        }
    }

    /**
     * 更新下载状态
     */
    private void setDownloadStatus() {
        List<VodDownLoadEntity> downLoadEntities = VodDownloadManager.getIns().getDownloadList();
        if (downLoadEntities == null) {
            return;
        }
        for (VodDownLoadEntity downLoad : downLoadEntities) {
            if (mVodId.equals(downLoad.getDownLoadId())) {
                isHaveDownload = true;
                break;
            }
        }

        if (isHaveDownload) {
            tvDownload.setSelected(true);
            ivControllerDownload.setSelected(true);
        }
    }

    //设置全屏
    private void setFullScreen(boolean isVideo) {
        ViewGroup.LayoutParams params = mGSVideoView.getLayoutParams();
        if (!mIsFullScreen) {
            //全屏
            mIsFullScreen = true;

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (isVideo) {
                //video全屏
                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                params.height = LinearLayout.LayoutParams.MATCH_PARENT;
                mGSVideoView.setLayoutParams(params);
                mDocView.setVisibility(View.GONE);
            } else {
                //doc全屏
                mGSVideoView.setVisibility(View.GONE);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llControllerBar.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.doc_view);
                llControllerBar.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams layoutParams_2 = (RelativeLayout.LayoutParams) seekChangeBarView.getLayoutParams();
                layoutParams_2.addRule(RelativeLayout.ALIGN_TOP, R.id.doc_view);
                layoutParams_2.addRule(RelativeLayout.ALIGN_RIGHT, R.id.doc_view);
                layoutParams_2.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.doc_view);
                layoutParams_2.addRule(RelativeLayout.ALIGN_LEFT, R.id.doc_view);
                seekChangeBarView.setLayoutParams(layoutParams_2);
            }
        } else {
            mIsFullScreen = false;

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (mGSVideoView.isShown()) {
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = dp2px(this, 240);
                mGSVideoView.setLayoutParams(params);
                mDocView.setVisibility(View.VISIBLE);
            } else {
                mGSVideoView.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llControllerBar.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.video_view);
                llControllerBar.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams layoutParams_2 = (RelativeLayout.LayoutParams) seekChangeBarView.getLayoutParams();
                layoutParams_2.addRule(RelativeLayout.ALIGN_TOP, R.id.video_view);
                layoutParams_2.addRule(RelativeLayout.ALIGN_RIGHT, R.id.video_view);
                layoutParams_2.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.video_view);
                layoutParams_2.addRule(RelativeLayout.ALIGN_LEFT, R.id.video_view);
                seekChangeBarView.setLayoutParams(layoutParams_2);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rlToolbar.setVisibility(View.GONE);
            ivFullDoc.setVisibility(View.GONE);
            if (llControllerBar.isShown()) {
                llFullToolbar.setVisibility(View.VISIBLE);
            }
            rlNoFullToolbar.setVisibility(View.GONE);
            if (TextUtils.isEmpty(mLocalpath)) {
                ivControllerDownload.setVisibility(View.VISIBLE);
            }
        } else {
            rlToolbar.setVisibility(View.VISIBLE);
            ivFullDoc.setVisibility(View.VISIBLE);
            llFullToolbar.setVisibility(View.GONE);
            rlNoFullToolbar.setVisibility(View.VISIBLE);

            ivControllerDownload.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onDoubleClicked(IGSDocView igsDocView) {
        return false;
    }

    @Override
    public boolean onSingleClicked(IGSDocView igsDocView) {
        return false;
    }

    @Override
    public boolean onEndHDirection(IGSDocView igsDocView, int i, int i1) {
        return false;
    }

    @Override
    public void onInit(int result, boolean haveVideo, int duration, List<DocInfo> docInfos) {
        loadDialog.dismiss();

        Message message = new Message();
        message.what = MSG.MSG_ON_INIT;
        message.obj = docInfos;
        Bundle bundle = new Bundle();
        bundle.putInt(DURATION, duration);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    @Override
    public void onPlayStop() {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_STOP, 0));
    }

    @Override
    public void onPlayPause() {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_PAUSE, 0));
    }

    @Override
    public void onPlayResume() {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_RESUME, 0));
    }

    @Override
    public void onPosition(int position) {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_POSITION, position));
    }

    @Override
    public void onVideoSize(int position, int videoWidth, int videoHeight) {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_VIDEOSIZE, 0));
    }

    @Override
    public void onPageSize(int position, int i1, int i2) {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_PAGE, position));
    }

    @Override
    public void onSeek(int position) {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_SEEK, position));
    }

    @Override
    public void onAudioLevel(int level) {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_AUDIOLEVEL, level));
    }

    @Override
    public void onCaching(boolean b) {

    }

    @Override
    public void onVideoStart() {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_START, 0));
    }

    @Override
    public void onChat(List<ChatMsg> list) {

    }

    @Override
    public void onDocInfo(List<DocInfo> list) {

    }

    @Override
    public void onChatCensor(String s, String s1) {

    }

    @Override
    public void onError(int errCode) {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_ERROR, errCode));
    }

    private void stopPlay() {
        if (mGSOLPlayer != null) {
            mGSOLPlayer.stop();
        }
    }

    private void release() {
        stopPlay();
        if (mGSOLPlayer != null) {
            mGSOLPlayer.release();
        }
    }

    //保存进度
    private void savePosition() {
        int progress = mSeekBarPlayViedo.getProgress();
        if (vodRecordHelper != null) {
            vodRecordHelper.save(progress);
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsFullScreen) {
            setFullScreen(false);
        } else {
            release();
            savePosition();
            setResult(GenseeVod.GENSEE_VOD_RESULT);
            finish();
        }
    }

    interface MSG {
        int MSG_ON_INIT = 1;
        int MSG_ON_STOP = 2;
        int MSG_ON_POSITION = 3;
        int MSG_ON_VIDEOSIZE = 4;
        int MSG_ON_PAGE = 5;
        int MSG_ON_SEEK = 6;
        int MSG_ON_AUDIOLEVEL = 7;
        int MSG_ON_ERROR = 8;
        int MSG_ON_PAUSE = 9;
        int MSG_ON_RESUME = 10;
        int SHOW_CONTROLLER = 11;
        int FADE_OUT = 12;
        int MSG_ON_START = 13;
    }

    @SuppressLint("HandlerLeak")
    protected Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG.MSG_ON_INIT:
                    ivPlay.setSelected(true);

                    maxPosition = msg.getData().getInt(DURATION);
                    mSeekBarPlayViedo.setMax(maxPosition);
                    maxPosition = maxPosition / 1000;

                    int recordPosition = vodRecordHelper.get();
                    if (recordPosition != 0) {
                        setSeek(recordPosition);
                        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_SEEK, recordPosition));
                    }

                    if (null != chapterListAdapter) {
                        chapterList.clear();
                        if (null != msg.obj) {
                            List<DocInfo> docInfoList = (List<DocInfo>) msg.obj;
                            for (DocInfo docInfo : docInfoList) {
                                List<PageInfo> pageInfoList = docInfo.getPages();
                                if (null != pageInfoList && pageInfoList.size() > 0) {
                                    for (PageInfo pageInfo : pageInfoList) {

                                        ChapterInfo chapterInfo = new ChapterInfo();
                                        chapterInfo.setDocId(docInfo.getDocId());
                                        chapterInfo
                                                .setDocName(docInfo.getDocName());
                                        chapterInfo.setDocPageNum(docInfo
                                                .getPageNum());
                                        chapterInfo.setDocType(docInfo.getType());

                                        chapterInfo.setPageTimeStamp(pageInfo
                                                .getTimeStamp());
                                        chapterInfo.setPageTitle(pageInfo
                                                .getTitle());
                                        chapterList.add(chapterInfo);
                                    }
                                }
                            }

                        }
                        chapterListAdapter.notifyData(chapterList);
                    }
                    break;
                case MSG.MSG_ON_STOP:
                    VIEDOPAUSEPALY = 2;
                    ivPlay.setSelected(false);
                    break;
                case MSG.MSG_ON_VIDEOSIZE:

                    break;
                case MSG.MSG_ON_PAGE:
                    int position = (Integer) msg.obj;
                    int nSize = chapterList.size();
                    for (int i = 0; i < nSize; i++) {
                        ChapterInfo chapterInfo = chapterList.get(i);
                        if (chapterInfo.getPageTimeStamp() == position) {
                            if (null != chapterListAdapter) {
                                chapterListAdapter.setSelectedPosition(i);
                            }
                            break;
                        }
                    }
                    break;
                case MSG.MSG_ON_PAUSE:
                    VIEDOPAUSEPALY = 1;
                    break;
                case MSG.MSG_ON_RESUME:
                    VIEDOPAUSEPALY = 0;
                    ivPlay.setSelected(true);
                    break;
                case MSG.MSG_ON_START:
                    ivPlay.setSelected(true);
                    break;
                case MSG.MSG_ON_POSITION:
                    nowPosition = (int) msg.obj;
                    if (isTouch) {
                        return;
                    }
                case MSG.MSG_ON_SEEK:
                    isTouch = false;
                    int anyPosition = (Integer) msg.obj;
                    mSeekBarPlayViedo.setProgress(anyPosition);
                    break;
                case MSG.MSG_ON_AUDIOLEVEL:

                    break;
                case MSG.MSG_ON_ERROR:
                    int errorCode = (Integer) msg.obj;
                    switch (errorCode) {
                        case ERR_PAUSE:
                            Toast.makeText(getApplicationContext(), "暂停失败", Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        case ERR_PLAY:
                            if (!TextUtils.isEmpty(mLocalpath)) {
                                Toast.makeText(getApplicationContext(), "离线视频已失效", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(getApplicationContext(), "播放失败", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            break;
                        case ERR_RESUME:
                            Toast.makeText(getApplicationContext(), "恢复失败", Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        case ERR_SEEK:
                            Toast.makeText(getApplicationContext(), "进度变化失败", Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        case ERR_STOP:
                            Toast.makeText(getApplicationContext(), "停止失败", Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        default:
                            break;
                    }
                    break;
                case MSG.SHOW_CONTROLLER:
                    if (mIsFullScreen) {
                        llFullToolbar.setVisibility(View.VISIBLE);
                    }
                    llControllerBar.setVisibility(View.VISIBLE);
                    mControllerStatus = SHOW;
                    myHandler.sendEmptyMessageDelayed(MSG.FADE_OUT, DEFAULT_FADE_OUT_TIME);
                    break;
                case MSG.FADE_OUT:
                    llFullToolbar.setVisibility(View.GONE);
                    llControllerBar.setVisibility(View.GONE);
                    mControllerStatus = HIDE;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private String getTime(int time) {
        time = time / 1000;
        return String.format("%02d", time / 3600) + ":"
                + String.format("%02d", time % 3600 / 60) + ":"
                + String.format("%02d", time % 3600 % 60);
    }

    private class ChapterListAdapter extends BaseAdapter {
        private List<ChapterInfo> pageList;
        private int selectedPosition = 0;

        private void setSelectedPosition(int position) {
            selectedPosition = position;
            notifyDataSetChanged();
        }

        private ChapterListAdapter() {
            pageList = new ArrayList<>();
        }

        private void notifyData(List<ChapterInfo> pageList) {
            this.pageList.clear();
            this.pageList.addAll(pageList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return pageList.size();
        }

        @Override
        public Object getItem(int position) {
            return pageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.doc_list_item_ly, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.init((ChapterInfo) getItem(position), position);
            return convertView;
        }

        private class ViewHolder {
            private TextView tvChapter;
            private TextView tvTitle;
            private TextView tvTime;
            private LinearLayout lyChapter;

            private String getChapterTime(long time) {
                return String.format("%02d", time / (3600 * 1000))
                        + ":"
                        + String.format("%02d", time % (3600 * 1000)
                        / (60 * 1000))
                        + ":"
                        + String.format("%02d", time % (3600 * 1000)
                        % (60 * 1000) / 1000);
            }

            private ViewHolder(View view) {
                tvChapter = view.findViewById(R.id.chapter_title);
                tvTitle = view.findViewById(R.id.doc_title);
                tvTime = view.findViewById(R.id.chapter_time);
                lyChapter = view.findViewById(R.id.chapter_ly);
            }

            private void init(ChapterInfo chapterInfo, int position) {
                tvChapter.setText(chapterInfo.getPageTitle());
                tvTime.setText(getChapterTime(chapterInfo.getPageTimeStamp()));
                tvTitle.setText(chapterInfo.getDocName());

                if (selectedPosition == position) {
                    lyChapter.setBackgroundResource(R.color.red);
                } else {
                    lyChapter.setBackgroundResource(R.color.transparent);
                }
            }
        }

    }


    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void shortToast(Context context, String massage) {
        Toast toast = new Toast(context);
        TextView tvToast;

        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 300);
        toast.setDuration(Toast.LENGTH_SHORT);

        View view = LayoutInflater.from(context).inflate(R.layout.gensee_toast_layout, null);
        tvToast = view.findViewById(R.id.tv_toast);
        tvToast.setText(massage);//设置文本

        toast.setView(view);
        toast.show();
    }


    public interface OnclickListener extends Serializable {
        void onShareClick();
    }

}
