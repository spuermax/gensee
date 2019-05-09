package com.gensee.fastsdk;

import android.content.Context;
import android.text.TextUtils;

import com.gensee.common.ServiceType;
import com.gensee.entity.InitParam;
import com.gensee.fastsdk.core.GSFastConfig;

public class GenseeParamFactory {

    private String domain;
    private String number;
    private String loginAccount;
    private String loginPwd;
    private String vodPwd;
    private String joinPwd;
    private String nickName;
    private String serviceType;
    private String k;
    private boolean replayState;


    private GenseeParamFactory(Builder builder) {
        this.domain = builder.domain;
        this.number = builder.number;
        this.loginAccount = builder.loginAccount;
        this.loginPwd = builder.loginPwd;
        this.vodPwd = builder.vodPwd;
        this.joinPwd = builder.joinPwd;
        this.nickName = builder.nickName;
        this.serviceType = builder.serviceType;
        this.k = builder.k;
        this.replayState = builder.replayState;
    }

    private InitParam createInitParam() {
        InitParam initParam = new InitParam();
        initParam.setDomain(domain);
        initParam.setNumber(number);
        initParam.setLoginAccount(loginAccount);
        initParam.setLoginPwd(loginPwd);
        initParam.setNickName(nickName);
        initParam.setK(k);
        if (!TextUtils.isEmpty(number) && number.length() == 8) {
            initParam.setNumber(number);
        } else {
            initParam.setNumber(number);
            initParam.setLiveId(number);
        }
        if (!TextUtils.isEmpty(vodPwd)) {
            initParam.setVodPwd(vodPwd);
        } else {
            initParam.setJoinPwd(joinPwd);
        }

        if (!TextUtils.isEmpty(serviceType) && serviceType.equals(ServiceType.WEBCAST.getValue())) {
            initParam.setServiceType(ServiceType.WEBCAST);
        } else {
            initParam.setServiceType(ServiceType.TRAINING);
        }
        initParam.setDownload(false);
        return initParam;
    }

    private GSFastConfig createGSFastConfig() {
        return new GSFastConfig();
    }

    public void start(Context context) {
        if (replayState) {
            GenseeVod.startVod(context, createInitParam(), "这是标题");
        } else {
            GenseeLive.startLive(context, createGSFastConfig(), createInitParam());
        }
    }

    public void startLocal(Context context, String localPath, String vodId) {
        GenseeVod.startVod(context, localPath, vodId, "这是离线播放");
    }

    public static final class Builder {
        private String domain;
        private String number;
        private String loginAccount;
        private String loginPwd;
        private String vodPwd;
        private String joinPwd;
        private String nickName;
        private String serviceType;
        private String k;
        private boolean replayState;

        public Builder() {

        }

        public Builder setDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder setNumber(String number) {
            this.number = number;
            return this;
        }

        public Builder setLoginAccount(String loginAccount) {
            this.loginAccount = loginAccount;
            return this;
        }

        public Builder setLoginPwd(String loginPwd) {
            this.loginPwd = loginPwd;
            return this;
        }

        public Builder setVodPwd(String vodPwd) {
            this.vodPwd = vodPwd;
            return this;
        }

        public Builder setJoinPwd(String joinPwd) {
            this.joinPwd = joinPwd;
            return this;
        }

        public Builder setNickName(String nickName) {
            this.nickName = nickName;
            return this;
        }

        public Builder setServiceType(String serviceType) {
            this.serviceType = serviceType;
            return this;
        }

        public Builder setK(String k) {
            this.k = k;
            return this;
        }

        public Builder setReplayState(boolean replayState) {
            this.replayState = replayState;
            return this;
        }

        public GenseeParamFactory build() {
            return new GenseeParamFactory(this);
        }
    }
}
