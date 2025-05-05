package com.lzyp.live.views;

import android.content.Context;
import android.view.TextureView;
import android.view.ViewGroup;

import com.lzyp.live.bean.LiveConfigBean;
import com.lzyp.live.bean.LiveVoiceMixUserBean;

import java.util.List;

/**
 * Created by http://www.yunbaokj.com on 2024/2/20.
 */
public abstract class AbsLiveChatRoomPushViewHolder extends AbsLivePushViewHolder {

    public AbsLiveChatRoomPushViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public AbsLiveChatRoomPushViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    public void startPreview(LiveConfigBean liveConfigBean, TextureView textureView) {

    }

    public void startMicrophone() {
    }

    public void setPushMute(boolean pushMute) {
    }

    public void voiceRoomAnchorMixVideo(List<LiveVoiceMixUserBean> userStreamList) {
    }

    public void voiceRoomAnchorMix(List<LiveVoiceMixUserBean> userStreamList) {
    }

    public abstract ViewGroup getContainer();
}
