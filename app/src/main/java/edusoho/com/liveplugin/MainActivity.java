package edusoho.com.liveplugin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gensee.download.VodDownLoadEntity;
import com.gensee.download.VodDownLoader;
import com.gensee.fastsdk.GenseeParamFactory;
import com.gensee.fastsdk.GenseeServiceType;
import com.gensee.fastsdk.GenseeVod;
import com.gensee.taskret.OnTaskRet;
import com.gensee.vod.VodSite;
import com.gensee.vodsdk.VodActivity;
import com.gensee.vodsdk.help.GenseeVodListenerManager;
import com.gensee.vodsdk.help.VodDownloadManager;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPlay;
    private Button btnShow;
    private Button btnClear;
    private TextView tvInfo;

    //下载完成 回调数据
    private String vodId;
    private String localPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = findViewById(R.id.btn_play);
        btnShow = findViewById(R.id.btn_show);
        btnClear = findViewById(R.id.btn_clear);
        tvInfo = findViewById(R.id.tv_info);

        btnPlay.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        /**
         * 初始化
         */
        VodSite.init(this, new OnTaskRet() {
            @Override
            public void onTaskRet(boolean arg0, int arg1, String arg2) {
            }
        });

        GenseeVodListenerManager.getInstance(this).setOnGenseeVodCallBack(new GenseeVodListenerManager.OnGenseeVodListener() {
            @Override
            public void onStartDownload(AppCompatActivity activity, String vodId, long length) {
                VodDownloadManager.getIns().download(vodId);
                GenseeVodListenerManager.getInstance(MainActivity.this).OnRefreshStatusListener();

                Toast.makeText(MainActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
            }
        });

        VodDownloadManager.getIns().initDownLoader(this.getApplication());
        VodDownloadManager.getIns().setUICallback(new VodDownLoader.OnDownloadListener() {
            @Override
            public void onDLFinish(String s, String s1) {
                vodId = s;
                localPath = s1;
            }

            @Override
            public void onDLPrepare(String s) {

            }

            @Override
            public void onDLPosition(String s, int i) {

            }

            @Override
            public void onDLStart(String s) {

            }

            @Override
            public void onDLStop(String s) {

            }

            @Override
            public void onDLError(String s, int i) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                String domain = "edusoho.gensee.com";
                String number = "93223269";
                String account = "training36617_mv";
                String loginPwd = "58747f038d5f0";
                String vodPwd = "5b31b28ebc5e4";
                String k = "1532599752cbbfae9905eab1f75212e6";

                GenseeParamFactory factory = new GenseeParamFactory.Builder()
                        .setDomain(domain)
                        .setNumber(number)
                        .setLoginPwd(loginPwd)
                        .setVodPwd(vodPwd)
                        .setLoginAccount(account)
                        .setNickName("visitor88730")
                        .setK(k)
                        .setServiceType(GenseeServiceType.TRAINING.getValue())
                        .setReplayState(true)
                        .build();
                factory.start(this);
                break;
            case R.id.btn_show:
                List<VodDownLoadEntity> list = VodDownloadManager.getIns().getDownloadList();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    stringBuilder.append(list.get(i).getDownLoadId());
                    stringBuilder.append("\n");
                }
                tvInfo.setText(stringBuilder.toString());
                if (!TextUtils.isEmpty(localPath)) {
                    GenseeParamFactory factory_2 = new GenseeParamFactory.Builder().build();
                    factory_2.startLocal(this, localPath, vodId);
                }
                break;
            case R.id.btn_clear:
                List<VodDownLoadEntity> list_2 = VodDownloadManager.getIns().getDownloadList();
                if (list_2 != null) {
                    VodDownloadManager.getIns().delete(list_2.get(0).getDownLoadId());
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == GenseeVod.GENSEE_VOD_RESULT) {
            Toast.makeText(MainActivity.this, "关闭回调", Toast.LENGTH_SHORT).show();
        }
    }
}
