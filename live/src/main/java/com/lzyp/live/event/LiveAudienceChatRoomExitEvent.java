package com.lzyp.live.event;

import com.lzyp.live.bean.LiveAudienceFloatWindowData;
import com.lzyp.live.bean.LiveBean;

public class LiveAudienceChatRoomExitEvent {

    private final LiveBean mLiveBean;
    private final LiveAudienceFloatWindowData mLiveAudienceFloatWindowData;

    public LiveAudienceChatRoomExitEvent(LiveBean liveBean, LiveAudienceFloatWindowData liveAudienceFloatWindowData) {
        mLiveBean = liveBean;
        mLiveAudienceFloatWindowData = liveAudienceFloatWindowData;
    }

    public LiveBean getLiveBean() {
        return mLiveBean;
    }

    public LiveAudienceFloatWindowData getLiveAudienceAgoraData() {
        return mLiveAudienceFloatWindowData;
    }
}
