package com.lzyp.live.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzyp.common.CommonAppConfig;
import com.lzyp.common.Constants;
import com.lzyp.common.HtmlConfig;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.bean.ConfigBean;
import com.lzyp.common.bean.GoodsBean;
import com.lzyp.common.bean.LiveGiftBean;
import com.lzyp.common.bean.UserBean;
import com.lzyp.common.dialog.AbsDialogFragment;
import com.lzyp.common.dialog.LiveChargeDialogFragment;
import com.lzyp.common.event.AppLifecycleEvent;
import com.lzyp.common.event.CoinChangeEvent;
import com.lzyp.common.event.FollowEvent;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.mob.MobCallback;
import com.lzyp.common.mob.MobShareUtil;
import com.lzyp.common.mob.ShareData;
import com.lzyp.common.pay.PayCallback;
import com.lzyp.common.pay.PayPresenter;
import com.lzyp.common.utils.KeyBoardUtil;
import com.lzyp.common.utils.L;
import com.lzyp.common.utils.LanguageUtil;
import com.lzyp.common.utils.StringUtil;
import com.lzyp.common.utils.ToastUtil;
import com.lzyp.common.utils.WordFilterUtil;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.game.util.GamePresenter;
import com.lzyp.im.utils.ImMessageUtil;
import com.lzyp.im.utils.ImUnReadCount;
import com.lzyp.live.R;
import com.lzyp.live.bean.GlobalGiftBean;
import com.lzyp.live.bean.LiveBean;
import com.lzyp.live.bean.LiveBuyGuardMsgBean;
import com.lzyp.live.bean.LiveChatBean;
import com.lzyp.live.bean.LiveDanMuBean;
import com.lzyp.live.bean.LiveEnterRoomBean;
import com.lzyp.live.bean.LiveGiftPrizePoolWinBean;
import com.lzyp.live.bean.LiveGuardInfo;
import com.lzyp.live.bean.LiveLuckGiftWinBean;
import com.lzyp.live.bean.LiveReceiveGiftBean;
import com.lzyp.live.bean.LiveUserGiftBean;
import com.lzyp.live.bean.LiveVoiceGiftBean;
import com.lzyp.live.bean.LiveVoiceLinkMicBean;
import com.lzyp.live.bean.TurntableGiftBean;
import com.lzyp.live.dialog.DailyTaskDialogFragment;
import com.lzyp.live.dialog.GiftPrizePoolFragment;
import com.lzyp.live.dialog.LiveChatListDialogFragment;
import com.lzyp.live.dialog.LiveChatRoomDialogFragment;
import com.lzyp.live.dialog.LiveGiftDialogFragment;
import com.lzyp.live.dialog.LiveGuardBuyDialogFragment;
import com.lzyp.live.dialog.LiveGuardDialogFragment;
import com.lzyp.live.dialog.LiveInputDialogFragment;
import com.lzyp.live.dialog.LiveRedPackListDialogFragment;
import com.lzyp.live.dialog.LiveRedPackSendDialogFragment;
import com.lzyp.live.dialog.LiveShareDialogFragment;
import com.lzyp.live.dialog.LiveUserListDialog;
import com.lzyp.live.dialog.LiveVoiceApplyUpFragment;
import com.lzyp.live.dialog.LuckPanDialogFragment;
import com.lzyp.live.dialog.LuckPanRecordDialogFragment;
import com.lzyp.live.dialog.LuckPanTipDialogFragment;
import com.lzyp.live.dialog.LuckPanWinDialogFragment;
import com.lzyp.live.http.LiveHttpConsts;
import com.lzyp.live.http.LiveHttpUtil;
import com.lzyp.live.livegame.luckpan.bean.LuckpanWinMsgBean;
import com.lzyp.live.livegame.luckpan.dialog.LiveGameLuckpanDialog;
import com.lzyp.live.livegame.star.bean.StarWinMsgBean;
import com.lzyp.live.livegame.star.dialog.LiveGameStarDialog;
import com.lzyp.live.presenter.LiveLinkMicAnchorPresenter;
import com.lzyp.live.presenter.LiveLinkMicPkPresenter;
import com.lzyp.live.presenter.LiveLinkMicPresenter;
import com.lzyp.live.socket.SocketChatUtil;
import com.lzyp.live.socket.SocketClient;
import com.lzyp.live.socket.SocketMessageListener;
import com.lzyp.live.socket.SocketVoiceRoomUtil;
import com.lzyp.live.views.AbsLiveChatRoomLinkMicViewHolder;
import com.lzyp.live.views.AbsLiveViewHolder;
import com.lzyp.live.views.LiveAddImpressViewHolder;
import com.lzyp.live.views.LiveAdminListViewHolder;
import com.lzyp.live.views.LiveContributeViewHolder;
import com.lzyp.live.views.LiveEndViewHolder;
import com.lzyp.live.views.LiveRoomViewHolder;
import com.lzyp.live.views.LiveWebViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/7.
 */

public abstract class LiveActivity extends AbsActivity implements SocketMessageListener, LiveShareDialogFragment.ActionListener, KeyBoardUtil.KeyBoardHeightListener {

    protected ViewGroup mContainer;
    protected ViewGroup mPageContainer;
    protected LiveRoomViewHolder mLiveRoomViewHolder;
    protected AbsLiveViewHolder mLiveBottomViewHolder;
    protected LiveAddImpressViewHolder mLiveAddImpressViewHolder;
    protected LiveContributeViewHolder mLiveContributeViewHolder;
    protected LiveWebViewHolder mLiveLuckGiftTipViewHolder;
    protected LiveWebViewHolder mLiveDaoGiftTipViewHolder;
    protected LiveAdminListViewHolder mLiveAdminListViewHolder;
    protected LiveEndViewHolder mLiveEndViewHolder;
    protected LiveLinkMicPresenter mLiveLinkMicPresenter;//观众与主播连麦逻辑
    protected LiveLinkMicAnchorPresenter mLiveLinkMicAnchorPresenter;//主播与主播连麦逻辑
    protected LiveLinkMicPkPresenter mLiveLinkMicPkPresenter;//主播与主播PK逻辑
    protected GamePresenter mGamePresenter;
    protected SocketClient mSocketClient;
    protected LiveBean mLiveBean;
    protected int mLiveSDK;//sdk类型  0金山  1腾讯
    protected String mTxAppId;//腾讯sdkAppId
    protected boolean mIsAnchor;//是否是主播
    protected int mSocketUserType;//socket用户类型  30 普通用户  40 管理员  50 主播  60超管
    protected String mStream;
    protected String mLiveUid;
    protected String mDanmuPrice;//弹幕价格
    protected String mCoinName;//钻石名称
    protected int mLiveType;//直播间的类型  普通 密码 门票 计时等
    protected int mLiveTypeVal;//收费价格,计时收费每次扣费的值
    protected KeyBoardUtil mKeyBoardUtil;
    protected int mChatLevel;//发言等级限制
    protected int mDanMuLevel;//弹幕等级限制
    private MobShareUtil mMobShareUtil;
    private boolean mFirstConnectSocket;//是否是第一次连接成功socket
    private boolean mGamePlaying;//是否在游戏中
    private boolean mChatRoomOpened;//判断私信聊天窗口是否打开
    private LiveChatRoomDialogFragment mLiveChatRoomDialogFragment;//私信聊天窗口
    protected LiveGuardInfo mLiveGuardInfo;
    private View mPkBg;
    private MobCallback mShareLiveCallback;
    protected boolean mIsChatRoom;//是否是聊天室
    protected int mChatRoomType;//聊天室类型，0语音 1视频
    protected AbsLiveChatRoomLinkMicViewHolder mLiveChatRoomLinkMicViewHolder;
    private PayPresenter mPayPresenter;
    protected boolean mTaskSwitch;
    private boolean mAppFrontGroundChanged;//App前后台切换
    protected LiveGameStarDialog mLiveGameStarDialog;
    protected LiveGameLuckpanDialog mLiveGameLuckpanDialog;

    @Override
    protected void main() {
        CommonAppConfig.getInstance().setTopActivityType(Constants.PUSH_TYPE_LIVE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mIsAnchor = this instanceof LiveAnchorActivity;
        mPageContainer = (ViewGroup) findViewById(R.id.page_container);
        EventBus.getDefault().register(this);
        mPkBg = findViewById(R.id.pk_bg);
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    public ViewGroup getPageContainer() {
        return mPageContainer;
    }

    protected void hideDialogs() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> list = fragmentManager.getFragments();
        if (list != null && list.size() > 0) {
            for (Fragment fragment : list) {
                if (fragment != null && fragment instanceof AbsDialogFragment) {
                    ((AbsDialogFragment) fragment).dismissAllowingStateLoss();
                }
            }
        }
    }

    public boolean isTxSdK() {
        return mLiveSDK == Constants.LIVE_SDK_TX;
    }


    /**
     * 连接成功socket后调用
     */
    @Override
    public void onConnect(boolean successConn) {
        if (successConn) {
            if (!mFirstConnectSocket) {
                mFirstConnectSocket = true;
                if (mLiveType == Constants.LIVE_TYPE_PAY || mLiveType == Constants.LIVE_TYPE_TIME) {
                    SocketChatUtil.sendUpdateVotesMessage(mSocketClient, mLiveTypeVal, 1);
                }
                SocketChatUtil.getFakeFans(mSocketClient);
            }
        }
    }

    /**
     * 自己的socket断开
     */
    @Override
    public void onDisConnect() {

    }

    /**
     * 收到聊天消息
     */
    @Override
    public void onChat(LiveChatBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.insertChat(bean);
        }
        if (bean.getType() == LiveChatBean.LIGHT) {
            onLight();
        }
    }

    /**
     * 收到飘心消息
     */
    @Override
    public void onLight() {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.playLightAnim();
        }
    }

    /**
     * 收到用户进房间消息
     */
    @Override
    public void onEnterRoom(LiveEnterRoomBean bean) {
        if (mLiveRoomViewHolder != null) {
            LiveUserGiftBean u = bean.getUserBean();
            mLiveRoomViewHolder.insertUser(u);
            mLiveRoomViewHolder.insertChat(bean.getLiveChatBean());
            mLiveRoomViewHolder.onEnterRoom(bean);
            mLiveRoomViewHolder.increaseUserNum(1);
        }
    }

    /**
     * 收到用户离开房间消息
     */
    @Override
    public void onLeaveRoom(UserBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.removeUser(bean.getId());
            mLiveRoomViewHolder.decreaseUserNum(1);
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceLeaveRoom(bean);
        }
    }

    /**
     * 收到礼物消息
     */
    @Override
    public void onSendGift(LiveReceiveGiftBean bean, LiveChatBean chatBean) {
        if (!isChatRoom()) {
            bean.setToName(null);
        }
        if (!"0".equals(bean.getStickerId())) {
            mLiveRoomViewHolder.setVotes(bean.getVotes());
            if (mIsAnchor && CommonAppConfig.getInstance().isMhBeautyEnable()) {
                ((LiveAnchorActivity) mContext).showStickerGift(bean);
            }
        } else {
            mLiveRoomViewHolder.showGiftMessage(bean);
        }

        //在聊天栏显示送礼物消息
        if (chatBean != null) {
            if (isChatRoom()) {
                chatBean.setContent(String.format(WordUtil.getString(R.string.live_send_gift_6), bean.getToName(), bean.getGiftCount(), bean.getGiftName()));
            } else {
                chatBean.setContent(String.format(WordUtil.getString(R.string.live_send_gift_1), bean.getGiftCount(), bean.getGiftName()));
            }
            onChat(chatBean);
        }
    }

    /**
     * pk 时候收到礼物
     *
     * @param leftGift  左边的映票数
     * @param rightGift 右边的映票数
     */
    @Override
    public void onSendGiftPk(long leftGift, long rightGift) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onPkProgressChanged(leftGift, rightGift);
        }
    }

    /**
     * 收到弹幕消息
     */
    @Override
    public void onSendDanMu(LiveDanMuBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.showDanmu(bean);
        }
    }

    /**
     * 观众收到直播结束消息
     */
    @Override
    public void onLiveEnd() {
        hideDialogs();
    }

    /**
     * 直播间  主播登录失效
     */
    @Override
    public void onAnchorInvalid() {
        hideDialogs();
    }

    /**
     * 超管关闭直播间
     */
    @Override
    public void onSuperCloseLive() {
        hideDialogs();
    }

    /**
     * 踢人
     */
    @Override
    public void onKick(String touid) {

    }

    /**
     * 禁言
     */
    @Override
    public void onShutUp(String touid, String content) {

    }

    /**
     * 设置或取消管理员
     */
    @Override
    public void onSetAdmin(String toUid, int isAdmin) {
        if (!TextUtils.isEmpty(toUid) && toUid.equals(CommonAppConfig.getInstance().getUid())) {
            mSocketUserType = isAdmin == 1 ? Constants.SOCKET_USER_TYPE_ADMIN : Constants.SOCKET_USER_TYPE_NORMAL;
        }
    }

    /**
     * 主播切换计时收费或更改计时收费价格的时候执行
     */
    @Override
    public void onChangeTimeCharge(int typeVal) {

    }

    /**
     * 门票或计时收费更新主播映票数
     */
    @Override
    public void onUpdateVotes(String uid, String deltaVal, int first) {
        if (!CommonAppConfig.getInstance().getUid().equals(uid) || first != 1) {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.updateVotes(deltaVal);
            }
        }
    }

    /**
     * 添加僵尸粉
     */
    @Override
    public void addFakeFans(List<LiveUserGiftBean> list) {
        if (mLiveRoomViewHolder != null && list != null) {
            mLiveRoomViewHolder.insertUser(list);
            mLiveRoomViewHolder.increaseUserNum(list.size());
        }
    }

    /**
     * 直播间  收到购买守护消息
     */
    @Override
    public void onBuyGuard(LiveBuyGuardMsgBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onGuardInfoChanged(bean);
            LiveChatBean chatBean = new LiveChatBean();
            chatBean.setContent(String.format(WordUtil.getString(R.string.guard_buy_msg), bean.getUserName()));
            chatBean.setType(LiveChatBean.SYSTEM);
            mLiveRoomViewHolder.insertChat(chatBean);
        }
    }

    /**
     * 直播间 收到红包消息
     */
    @Override
    public void onRedPack(LiveChatBean liveChatBean) {
        if (CommonAppConfig.getInstance().isTeenagerType()) {
            return;
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setRedPackBtnVisible(true);
            mLiveRoomViewHolder.insertChat(liveChatBean);
        }
    }

    /**
     * 观众与主播连麦  主播收到观众的连麦申请
     */
    @Override
    public void onAudienceApplyLinkMic(UserBean u) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceApplyLinkMic(u);
        }
    }

    /**
     * 观众与主播连麦  观众收到主播同意连麦的socket
     */
    @Override
    public void onAnchorAcceptLinkMic(String touid) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorAcceptLinkMic(touid);
        }
    }

    /**
     * 观众与主播连麦  观众收到主播拒绝连麦的socket
     */
    @Override
    public void onAnchorRefuseLinkMic() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorRefuseLinkMic();
        }
    }

    /**
     * 观众与主播连麦  主播收到观众发过来的流地址
     *
     * @param playUrl 连麦观众的播放地址
     */
    @Override
    public void onAudienceSendLinkMicUrl(String uid, String uname, String playUrl) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceSendLinkMicUrl(uid, uname, playUrl);
        }
    }

    /**
     * 观众与主播连麦  主播关闭观众的连麦
     */
    @Override
    public void onAnchorCloseLinkMic(String touid, String uname) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorCloseLinkMic(touid, uname);
        }
    }

    /**
     * 观众与主播连麦  观众主动断开连麦
     */
    @Override
    public void onAudienceCloseLinkMic(String uid, String uname) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceCloseLinkMic(uid, uname);
        }
    }

    /**
     * 观众与主播连麦  主播连麦无响应
     */
    @Override
    public void onAnchorNotResponse() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorNotResponse();
        }
    }

    /**
     * 观众与主播连麦  主播正在忙
     */
    @Override
    public void onAnchorBusy() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorBusy();
        }
    }

    /**
     * 主播与主播连麦  主播收到其他主播发过来的连麦申请的回调
     */
    @Override
    public void onLinkMicAnchorApply(UserBean u, String stream) {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播连麦  所有人收到对方主播的播流地址的回调
     *
     * @param playUrl 对方主播的播流地址
     * @param pkUid   对方主播的uid
     */
    @Override
    public void onLinkMicAnchorPlayUrl(String pkUid, String pkStream, String playUrl) {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorPlayUrl(pkUid, pkStream, playUrl);
        }
        if (isTxSdK()) {
            if (this instanceof LiveAudienceActivity) {
                ((LiveAudienceActivity) this).onLinkMicTxAnchor(true);
            }
        }
        setPkBgVisible(true);
    }

    /**
     * 主播与主播连麦  断开连麦的回调
     */
    @Override
    public void onLinkMicAnchorClose() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorClose();
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkClose();
        }
        if (isTxSdK()) {
            if (this instanceof LiveAudienceActivity) {
                ((LiveAudienceActivity) this).onLinkMicTxAnchor(false);
            }
        }
        setPkBgVisible(false);
    }

    /**
     * 主播与主播连麦  对方主播拒绝连麦的回调
     */
    @Override
    public void onLinkMicAnchorRefuse() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播连麦  对方主播无响应的回调
     */
    @Override
    public void onLinkMicAnchorNotResponse() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播连麦  对方主播正在游戏
     */
    @Override
    public void onlinkMicPlayGaming() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播连麦  对方主播正在忙的回调
     */
    @Override
    public void onLinkMicAnchorBusy() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK  主播收到对方主播发过来的PK申请的回调
     *
     * @param u      对方主播的信息
     * @param stream 对方主播的stream
     */
    @Override
    public void onLinkMicPkApply(UserBean u, String stream) {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK 所有人收到PK开始的回调
     */
    @Override
    public void onLinkMicPkStart(String pkUid) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkStart(pkUid);
        }
    }

    /**
     * 主播与主播PK  所有人收到断开连麦pk的回调
     */
    @Override
    public void onLinkMicPkClose() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkClose();
        }
    }

    /**
     * 主播与主播PK  对方主播拒绝pk的回调
     */
    @Override
    public void onLinkMicPkRefuse() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK   对方主播正在忙的回调
     */
    @Override
    public void onLinkMicPkBusy() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK   对方主播无响应的回调
     */
    @Override
    public void onLinkMicPkNotResponse() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK   所有人收到PK结果的回调
     */
    @Override
    public void onLinkMicPkEnd(String winUid) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkEnd(winUid);
        }
    }


    /**
     * 连麦观众退出直播间
     */
    @Override
    public void onAudienceLinkMicExitRoom(String touid) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceLinkMicExitRoom(touid);
        }
    }


    /**
     * 幸运礼物中奖
     */
    @Override
    public void onLuckGiftWin(LiveLuckGiftWinBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onLuckGiftWin(bean);
        }
    }

    /**
     * 奖池中奖
     */
    @Override
    public void onPrizePoolWin(LiveGiftPrizePoolWinBean bean) {
        if (!isChatRoom() && mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onPrizePoolWin(bean);
        }
    }


    /**
     * 奖池升级
     */
    @Override
    public void onPrizePoolUp(String level) {
        if (!isChatRoom() && mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onPrizePoolUp(level);
        }
    }

    /**
     * 全站礼物
     */
    @Override
    public void onGlobalGift(GlobalGiftBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onGlobalGift(bean);
        }
    }

    /**
     * 直播间礼物展示
     */
    @Override
    public void onLiveGoodsShow(GoodsBean bean) {
        if (!mIsAnchor && mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setShowGoodsBean(bean);
        }
    }


    /**
     * 直播间购物飘屏
     */
    @Override
    public void onLiveGoodsFloat(String userName) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onLiveGoodsFloat(userName);
        }
    }


    /**
     * 语音聊天室--主播收到观众申请上麦
     */
    @Override
    public void onVoiceRoomApplyUpMic() {
        //主播直播间实现此逻辑
    }


    /**
     * 语音聊天室--观众收到主播同意或拒绝上麦的消息
     *
     * @param toUid    上麦的人的uid
     * @param toName   上麦的人的name
     * @param toAvatar 上麦的人的头像
     * @param position 上麦的人的麦位，从0开始 -1表示拒绝上麦
     */
    @Override
    public void onVoiceRoomHandleApply(String toUid, String toName, String toAvatar, int position) {
        if (position >= 0) {
            if (mLiveChatRoomLinkMicViewHolder != null) {
                mLiveChatRoomLinkMicViewHolder.onUserUpMic(toUid, toName, toAvatar, position);
            }
        }
    }


    /**
     * 语音聊天室--所有人收到某人下麦的消息
     *
     * @param uid  下麦的人的uid
     * @param type 0自己主动下麦  1被主播下麦  2被管理员下麦
     */
    @Override
    public void onVoiceRoomDownMic(String uid, int type) {
        //各自实现
    }


    /**
     * 语音聊天室--主播控制麦位 闭麦开麦禁麦等
     *
     * @param uid      被操作人的uid
     * @param position 麦位
     * @param status   麦位的状态 -1 关麦；  0无人； 1开麦 ； 2 禁麦；
     */
    @Override
    public void onControlMicPosition(String uid, int position, int status) {
        if (mLiveChatRoomLinkMicViewHolder != null) {
            mLiveChatRoomLinkMicViewHolder.onControlMicPosition(position, status);
        }
    }


    /**
     * 语音聊天室--观众上麦后推流成功，把自己的播放地址广播给所有人
     *
     * @param uid        上麦观众的uid
     * @param pull       上麦观众的播流地址
     * @param userStream 上麦观众的流名，主播混流用
     */
    @Override
    public void onVoiceRoomPushSuccess(String uid, String pull, String userStream) {
        //各自实现
    }

    /**
     * 语音聊天室--收到上麦观众发送表情的消息
     *
     * @param uid       上麦观众的uid
     * @param faceIndex 表情标识
     */
    @Override
    public void onVoiceRoomFace(String uid, int faceIndex) {
        if (mLiveChatRoomLinkMicViewHolder != null) {
            mLiveChatRoomLinkMicViewHolder.onVoiceRoomFace(uid, faceIndex);
        }
    }


    /**
     * 直播间警告
     *
     * @param warningTip 警告提示语
     */
    @Override
    public void onLiveRoomWarning(String warningTip) {
        //LiveAnchorActivity实现
    }

    @Override
    public void onGameZjh(JSONObject obj) {
        if (CommonAppConfig.getInstance().isTeenagerType()) {
            return;
        }
        if (mGamePresenter != null) {
            mGamePresenter.onGameZjhSocket(obj);
        }
    }


    @Override
    public void onGameZp(JSONObject obj) {
        if (CommonAppConfig.getInstance().isTeenagerType()) {
            return;
        }
        if (mGamePresenter != null) {
            mGamePresenter.onGameZpSocket(obj);
        }
    }

    /**
     * 星球探宝中奖
     */
    @Override
    public void onGameXqtbWin(String json) {
        List<StarWinMsgBean> list = JSON.parseArray(json, StarWinMsgBean.class);
        if (list != null && list.size() > 0) {
            if (mLiveGameStarDialog != null) {
                mLiveGameStarDialog.onWinResultChanged(list);
            }
            if (mLiveRoomViewHolder != null) {
                List<LiveChatBean> chatList = new ArrayList<>();
                boolean isZh = LanguageUtil.isZh();
                for (int i = 0; i < list.size(); i++) {
                    LiveChatBean chatBean = new LiveChatBean();
                    if (isZh) {
                        chatBean.setContent(list.get(i).getTitle());
                    } else {
                        chatBean.setContent(list.get(i).getTitleEn());
                    }
                    chatBean.setType(LiveChatBean.SYSTEM);
                    chatList.add(chatBean);
                }
                mLiveRoomViewHolder.insertChatList(chatList);
            }
        }
    }

    /**
     * 幸运大转盘中奖
     */
    @Override
    public void onGameLuckpanWin(String json) {
        List<LuckpanWinMsgBean> list = JSON.parseArray(json, LuckpanWinMsgBean.class);
        if (list != null && list.size() > 0) {
            if (mLiveGameLuckpanDialog != null) {
                mLiveGameLuckpanDialog.onWinResultChanged(list);
            }
            if (mLiveRoomViewHolder != null) {
                List<LiveChatBean> chatList = new ArrayList<>();
                boolean isZh = LanguageUtil.isZh();
                for (int i = 0; i < list.size(); i++) {
                    LiveChatBean chatBean = new LiveChatBean();
                    if (isZh) {
                        chatBean.setContent(list.get(i).getTitle());
                    } else {
                        chatBean.setContent(list.get(i).getTitleEn());
                    }
                    chatBean.setType(LiveChatBean.SYSTEM);
                    chatList.add(chatBean);
                }
                mLiveRoomViewHolder.insertChatList(chatList);
            }
        }
    }

    /**
     * 打开聊天输入框
     */
    public void openChatWindow(String atName) {
        if (mKeyBoardUtil == null) {
            mKeyBoardUtil = new KeyBoardUtil(super.findViewById(android.R.id.content), this);
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.chatScrollToBottom();
        }
        LiveInputDialogFragment fragment = new LiveInputDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_DANMU_PRICE, mDanmuPrice);
        bundle.putString(Constants.COIN_NAME, mCoinName);
        bundle.putString(Constants.AT_NAME, atName);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveInputDialogFragment");
    }

    /**
     * 打开私信列表窗口
     */
    public void openChatListWindow() {
        LiveChatListDialogFragment fragment = new LiveChatListDialogFragment();
        if (!mIsAnchor) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.LIVE_UID, mLiveUid);
            fragment.setArguments(bundle);
        }
        fragment.show(getSupportFragmentManager(), "LiveChatListDialogFragment");
    }

    public String getLiveUid() {
        return mLiveUid;
    }

    public LiveBean getLiveBean() {
        return mLiveBean;
    }

    /**
     * 打开私信聊天窗口
     */
    public void openChatRoomWindow(UserBean userBean, boolean following) {
        if (mKeyBoardUtil == null) {
            mKeyBoardUtil = new KeyBoardUtil(super.findViewById(android.R.id.content), this);
        }
        LiveChatRoomDialogFragment fragment = new LiveChatRoomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.USER_BEAN, userBean);
        bundle.putBoolean(Constants.FOLLOW, following);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveChatRoomDialogFragment");
    }

    /**
     * 发 弹幕 消息
     */
    public void sendDanmuMessage(String content) {
        if (!mIsAnchor) {
            UserBean u = CommonAppConfig.getInstance().getUserBean();
            if (u != null && u.getLevel() < mDanMuLevel) {
                ToastUtil.show(String.format(WordUtil.getString(R.string.live_level_danmu_limit), mDanMuLevel));
                return;
            }
        }
        content = WordFilterUtil.getInstance().filter(content);
        LiveHttpUtil.sendDanmu(content, mLiveUid, mStream, mDanmuCallback);
    }

    private HttpCallback mDanmuCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                UserBean u = CommonAppConfig.getInstance().getUserBean();
                if (u != null) {
                    u.setLevel(obj.getIntValue("level"));
                    String coin = obj.getString("coin");
                    u.setCoin(coin);
                    onCoinChanged(coin);
                }
                SocketChatUtil.sendDanmuMessage(mSocketClient, obj.getString("barragetoken"));
            } else {
                ToastUtil.show(msg);
            }
        }
    };


    /**
     * 发 聊天 消息
     */
    public void sendChatMessage(String content) {
        if (!mIsAnchor) {
            UserBean u = CommonAppConfig.getInstance().getUserBean();
            if (u != null && u.getLevel() < mChatLevel) {
                ToastUtil.show(String.format(WordUtil.getString(R.string.live_level_chat_limit), mChatLevel));
                return;
            }
        }
        int guardType = mLiveGuardInfo != null ? mLiveGuardInfo.getMyGuardType() : Constants.GUARD_TYPE_NONE;
        SocketChatUtil.sendChatMessage(mSocketClient, content, mIsAnchor, mSocketUserType, guardType);
    }

    public void sendChatMessage(String content, String contentEn) {
        int guardType = mLiveGuardInfo != null ? mLiveGuardInfo.getMyGuardType() : Constants.GUARD_TYPE_NONE;
        SocketChatUtil.sendChatMessage(mSocketClient, content, contentEn, mIsAnchor, mSocketUserType, guardType);
    }

    /**
     * 发 系统 消息
     */
    public void sendSystemMessage(String content, String contentEn) {
        SocketChatUtil.sendSystemMessage(mSocketClient, content, contentEn);
    }

    /**
     * 发 送礼物 消息
     */
    public void sendGiftMessage(LiveGiftBean giftBean, String giftToken, String paintedPath, int paintedWidth, int paintedHeight) {
        String liveName = "";
        if (mLiveBean != null) {
            liveName = mLiveBean.getUserNiceName();
        }
        int type = giftBean.getType();
        if (type == LiveGiftBean.TYPE_DRAW) {
            SocketChatUtil.sendGiftMessage(mSocketClient, type, giftToken, mLiveUid, liveName, paintedPath, paintedWidth, paintedHeight);
        } else {
            SocketChatUtil.sendGiftMessage(mSocketClient, type, giftToken, mLiveUid, liveName);
        }

    }

    /**
     * 主播或管理员踢人
     */
    public void kickUser(String toUid, String toName) {
        SocketChatUtil.sendKickMessage(mSocketClient, toUid, toName);
    }

    /**
     * 禁言
     */
    public void setShutUp(String toUid, String toName, int type) {
        SocketChatUtil.sendShutUpMessage(mSocketClient, toUid, toName, type);
    }

    /**
     * 设置或取消管理员消息
     */
    public void sendSetAdminMessage(int action, String toUid, String toName) {
        SocketChatUtil.sendSetAdminMessage(mSocketClient, action, toUid, toName);
    }


    /**
     * 超管关闭直播间
     */
    public void superCloseRoom() {
        SocketChatUtil.superCloseRoom(mSocketClient);
    }

    /**
     * 更新主播映票数
     */
    public void sendUpdateVotesMessage(int deltaVal) {
        SocketChatUtil.sendUpdateVotesMessage(mSocketClient, deltaVal);
    }


    /**
     * 发送购买守护成功消息
     */
    public void sendBuyGuardMessage(String votes, int guardNum, int guardType) {
        SocketChatUtil.sendBuyGuardMessage(mSocketClient, votes, guardNum, guardType);
    }

    /**
     * 发送发红包成功消息
     */
    public void sendRedPackMessage() {
        SocketChatUtil.sendRedPackMessage(mSocketClient);
    }


    /**
     * 打开添加印象窗口
     */
    public void openAddImpressWindow(String toUid) {
        if (mLiveAddImpressViewHolder == null) {
            mLiveAddImpressViewHolder = new LiveAddImpressViewHolder(mContext, mPageContainer);
            mLiveAddImpressViewHolder.subscribeActivityLifeCycle();
        }
        mLiveAddImpressViewHolder.addToParent();
        mLiveAddImpressViewHolder.setToUid(toUid);
        mLiveAddImpressViewHolder.show();
    }

    /**
     * 直播间贡献榜窗口
     */
    public void openContributeWindow() {
        if (mLiveContributeViewHolder == null) {
            mLiveContributeViewHolder = new LiveContributeViewHolder(mContext, mPageContainer);
            mLiveContributeViewHolder.subscribeActivityLifeCycle();
            mLiveContributeViewHolder.addToParent();
        }
        mLiveContributeViewHolder.show();
        if (CommonAppConfig.LIVE_ROOM_SCROLL && !mIsAnchor) {
            ((LiveAudienceActivity) this).setScrollFrozen(true);
        }
    }


    /**
     * 直播间管理员窗口
     */
    public void openAdminListWindow() {
        if (mLiveAdminListViewHolder == null) {
            mLiveAdminListViewHolder = new LiveAdminListViewHolder(mContext, mPageContainer, mLiveUid);
            mLiveAdminListViewHolder.subscribeActivityLifeCycle();
            mLiveAdminListViewHolder.addToParent();
        }
        mLiveAdminListViewHolder.show();
    }

    /**
     * 是否能够返回
     */
    protected boolean canBackPressed() {
        if (mLiveContributeViewHolder != null && mLiveContributeViewHolder.isShowed()) {
            mLiveContributeViewHolder.hide();
            return false;
        }
        if (mLiveAddImpressViewHolder != null && mLiveAddImpressViewHolder.isShowed()) {
            mLiveAddImpressViewHolder.hide();
            return false;
        }
        if (mLiveAdminListViewHolder != null && mLiveAdminListViewHolder.isShowed()) {
            mLiveAdminListViewHolder.hide();
            return false;
        }
        if (mLiveLuckGiftTipViewHolder != null && mLiveLuckGiftTipViewHolder.isShowed()) {
            mLiveLuckGiftTipViewHolder.hide();
            return false;
        }
        if (mLiveDaoGiftTipViewHolder != null && mLiveDaoGiftTipViewHolder.isShowed()) {
            mLiveDaoGiftTipViewHolder.hide();
            return false;
        }
        return true;
    }

    /**
     * 打开分享窗口
     */
    public void openShareWindow() {
        LiveShareDialogFragment fragment = new LiveShareDialogFragment();
        fragment.setActionListener(this);
        fragment.show(getSupportFragmentManager(), "LiveShareDialogFragment");
    }

    /**
     * 分享点击事件回调
     */
    @Override
    public void onItemClick(String type) {
        if (Constants.LINK.equals(type)) {
            copyLink();
        } else {
            if (mShareLiveCallback == null) {
                mShareLiveCallback = new MobCallback() {
                    @Override
                    public void onSuccess(Object data) {
                        LiveHttpUtil.dailyTaskShareLive();
                    }

                    @Override
                    public void onError() {
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onFinish() {
                    }
                };
            }
            shareLive(type, mShareLiveCallback);
        }
    }

    /**
     * 复制直播间链接
     */
    public void copyLink() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean == null) {
            return;
        }
        String link = null;
        if (((LiveActivity) mContext).isChatRoom()) {
            link = configBean.getDownloadApkUrl();
        } else {
            link = StringUtil.contact(configBean.getLiveWxShareUrl(), mLiveUid);
        }
        if (TextUtils.isEmpty(link)) {
            return;
        }
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", link);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(R.string.copy_success);
    }


    /**
     * 分享直播间
     */
    public void shareLive(String type, MobCallback callback) {
        if (mLiveBean == null) {
            return;
        }
        shareLive(type, mLiveBean.getTitle(), callback);
    }


    /**
     * 分享直播间
     */
    public void shareLive(String type, String liveTitle, MobCallback callback) {
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean == null) {
            return;
        }
        if (mMobShareUtil == null) {
            mMobShareUtil = new MobShareUtil();
        }
        ShareData data = new ShareData();
        String liveShareTitle = configBean.getLiveShareTitle();
        if (!TextUtils.isEmpty(liveShareTitle) && liveShareTitle.contains("{username}")) {
            liveShareTitle = liveShareTitle.replace("{username}", mLiveBean.getUserNiceName());
        }
        data.setTitle(liveShareTitle);
        if (TextUtils.isEmpty(liveTitle)) {
            data.setDes(configBean.getLiveShareDes());
        } else {
            data.setDes(liveTitle);
        }
        data.setImgUrl(mLiveBean.getAvatarThumb());
        String webUrl = configBean.getDownloadApkUrl();
        if (!isChatRoom()) {
            if (Constants.MOB_WX.equals(type) || Constants.MOB_WX_PYQ.equals(type)) {
                webUrl = StringUtil.contact(configBean.getLiveWxShareUrl(), mLiveUid);
            }
        }
        data.setWebUrl(webUrl);
        mMobShareUtil.execute(type, data, callback);
    }

    /**
     * 监听关注变化事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (!TextUtils.isEmpty(mLiveUid) && mLiveUid.equals(e.getToUid())) {
            showFollow(e.getIsAttention());
        }
    }


    public void showFollow(int isAttention) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setAttention(isAttention);
        }
    }


//    /**
//     * 监听私信未读消息数变化事件
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
//        ImUnReadCount unReadCount = e.getUnReadCount();
//        if (unReadCount != null) {
//            String count = unReadCount.getLiveRoomUnReadCount();
//            if (!TextUtils.isEmpty(count) && mLiveBottomViewHolder != null) {
//                mLiveBottomViewHolder.setUnReadCount(count);
//            }
//        }
//    }

    /**
     * 获取私信未读消息数量
     */
    protected String getImUnReadCount() {
        ImUnReadCount unReadCount = ImMessageUtil.getInstance().getUnReadMsgCount();
        return unReadCount != null ? unReadCount.getLiveRoomUnReadCount() : "0";
    }

    /**
     * 监听钻石数量变化事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangeEvent(CoinChangeEvent e) {
        onCoinChanged(e.getCoin());
        if (e.isChargeSuccess() && this instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) this).onChargeSuccess();
        }
    }

    public void onCoinChanged(String coin) {
        if (mGamePresenter != null) {
            mGamePresenter.setLastCoin(coin);
        }
    }


    /**
     * 守护列表弹窗
     */
    public void openGuardListWindow() {
        LiveGuardDialogFragment fragment = new LiveGuardDialogFragment();
        fragment.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putBoolean(Constants.ANCHOR, mIsAnchor);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGuardDialogFragment");
    }

    /**
     * 打开购买守护的弹窗
     */
    public void openBuyGuardWindow() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream) || mLiveGuardInfo == null) {
            return;
        }
        LiveGuardBuyDialogFragment fragment = new LiveGuardBuyDialogFragment();
        fragment.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.COIN_NAME, mCoinName);
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putString(Constants.STREAM, mStream);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGuardBuyDialogFragment");
    }

    /**
     * 打开发红包的弹窗
     */
    public void openRedPackSendWindow() {
        LiveRedPackSendDialogFragment fragment = new LiveRedPackSendDialogFragment();
        fragment.setStream(mStream);
        //fragment.setCoinName(mCoinName);
        fragment.show(getSupportFragmentManager(), "LiveRedPackSendDialogFragment");
    }

    /**
     * 打开发红包列表弹窗
     */
    public void openRedPackListWindow() {
        LiveRedPackListDialogFragment fragment = new LiveRedPackListDialogFragment();
        fragment.setStream(mStream);
        fragment.setCoinName(mCoinName);
        fragment.show(getSupportFragmentManager(), "LiveRedPackListDialogFragment");
    }


    /**
     * 打开奖池弹窗
     */
    public void openPrizePoolWindow() {
        GiftPrizePoolFragment fragment = new GiftPrizePoolFragment();
        fragment.setLiveUid(mLiveUid);
        fragment.setStream(mStream);
        fragment.show(getSupportFragmentManager(), "GiftPrizePoolFragment");
    }

    /**
     * 打开幸运礼物说明
     */
    public void openLuckGiftTip() {
        if (mLiveLuckGiftTipViewHolder == null) {
            mLiveLuckGiftTipViewHolder = new LiveWebViewHolder(mContext, mPageContainer, HtmlConfig.LUCK_GIFT_TIP);
            mLiveLuckGiftTipViewHolder.subscribeActivityLifeCycle();
            mLiveLuckGiftTipViewHolder.addToParent();
        }
        mLiveLuckGiftTipViewHolder.show();
        if (CommonAppConfig.LIVE_ROOM_SCROLL && !mIsAnchor) {
            ((LiveAudienceActivity) this).setScrollFrozen(true);
        }
    }


    /**
     * 打开道具礼物说明
     */
    public void openDaoGiftTip() {
        if (mLiveDaoGiftTipViewHolder == null) {
            mLiveDaoGiftTipViewHolder = new LiveWebViewHolder(mContext, mPageContainer, HtmlConfig.DAO_GIFT_TIP);
            mLiveDaoGiftTipViewHolder.subscribeActivityLifeCycle();
            mLiveDaoGiftTipViewHolder.addToParent();
        }
        mLiveDaoGiftTipViewHolder.show();
        if (CommonAppConfig.LIVE_ROOM_SCROLL && !mIsAnchor) {
            ((LiveAudienceActivity) this).setScrollFrozen(true);
        }
    }

    /**
     * 打开礼物窗口
     */
    public void openGiftWindow() {
        openGiftWindow(false);
    }

    /**
     * 打开礼物窗口
     */
    public void openGiftWindow(boolean openPack) {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream)) {
            return;
        }
        LiveGiftDialogFragment fragment = new LiveGiftDialogFragment();
        fragment.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putString(Constants.LIVE_STREAM, mStream);
        bundle.putBoolean(Constants.OPEN_PACK, openPack);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGiftDialogFragment");
    }


    /**
     * 打开抽奖转盘
     */
    public void openLuckPanWindow() {
        LuckPanDialogFragment fragment = new LuckPanDialogFragment();
        fragment.show(getSupportFragmentManager(), "LuckPanDialogFragment");
    }


    /**
     * 抽奖转盘中奖
     *
     * @param winResultGiftBeanList
     */
    public void openLuckPanWinWindow(List<TurntableGiftBean> winResultGiftBeanList) {
        if (winResultGiftBeanList == null || winResultGiftBeanList.size() == 0)
            return;

        LuckPanWinDialogFragment fragment = new LuckPanWinDialogFragment();
        fragment.setTurntableResultGiftBeans(winResultGiftBeanList);
        fragment.show(getSupportFragmentManager(), "LuckPanWinDialogFragment");
    }

    /**
     * 抽奖转盘 规则说明
     */
    public void openLuckPanTipWindow() {
        LuckPanTipDialogFragment fragment = new LuckPanTipDialogFragment();
        fragment.show(getSupportFragmentManager(), "LuckPanTipDialogFragment");
    }

    /**
     * 抽奖转盘中奖记录
     */
    public void openLuckPanRecordWindow() {
        LuckPanRecordDialogFragment fragment = new LuckPanRecordDialogFragment();
        fragment.show(getSupportFragmentManager(), "LuckPanRecordDialogFragment");
    }


    /**
     * 键盘高度的变化
     */
    @Override
    public void onKeyBoardHeightChanged(int keyboardHeight) {
        if (mChatRoomOpened) {//判断私信聊天窗口是否打开
            if (mLiveChatRoomDialogFragment != null) {
                mLiveChatRoomDialogFragment.onKeyBoardHeightChanged(keyboardHeight);
            }
        } else {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.onKeyBoardChanged(keyboardHeight);
            }
        }
    }


    /**
     * 打开每日任务
     */
    public void openDailyTaskWindow() {
        DailyTaskDialogFragment fragment = new DailyTaskDialogFragment();
        fragment.setLiveUid(mLiveUid);
        fragment.show(getSupportFragmentManager(), "DailyTaskDialogFragment");
    }


    public void setChatRoomOpened(LiveChatRoomDialogFragment chatRoomDialogFragment, boolean chatRoomOpened) {
        mChatRoomOpened = chatRoomOpened;
        mLiveChatRoomDialogFragment = chatRoomDialogFragment;
    }

    /**
     * 是否在游戏中
     */
    public boolean isGamePlaying() {
        return mGamePlaying;
    }

    public void setGamePlaying(boolean gamePlaying) {
        mGamePlaying = gamePlaying;
    }

    /**
     * 是否在连麦中
     */
    public boolean isLinkMic() {
        return mLiveLinkMicPresenter != null && mLiveLinkMicPresenter.isLinkMic();
    }

    /**
     * 主播是否在连麦中
     */
    public boolean isLinkMicAnchor() {
        return mLiveLinkMicAnchorPresenter != null && mLiveLinkMicAnchorPresenter.isLinkMic();
    }

    /**
     * 显示pk背景图
     */
    public void setPkBgVisible(boolean visible) {
        if (mPkBg != null) {
            if (visible) {
                if (mPkBg.getVisibility() != View.VISIBLE) {
                    mPkBg.setVisibility(View.VISIBLE);
                }
            } else {
                if (mPkBg.getVisibility() == View.VISIBLE) {
                    mPkBg.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.pause();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.resume();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.resume();
        }
        if (mAppFrontGroundChanged) {
            mAppFrontGroundChanged = false;
            if (mSocketClient != null) {
                LiveHttpUtil.checkSocketConnect(mLiveUid, mStream, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            if (JSON.parseObject(info[0]).getIntValue("is_exist") == 0) {
                                if (mSocketClient != null) {
                                    mSocketClient.connect(mLiveUid, mStream);
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    protected void release() {
        EventBus.getDefault().unregister(this);
        LiveHttpUtil.cancel(LiveHttpConsts.SEND_DANMU);
        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_SOCKET_CONNECT);
        if (mLiveChatRoomLinkMicViewHolder != null) {
            mLiveChatRoomLinkMicViewHolder.release();
        }
        if (mKeyBoardUtil != null) {
            mKeyBoardUtil.release();
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.release();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.release();
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.release();
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.release();
        }
        if (mLiveAddImpressViewHolder != null) {
            mLiveAddImpressViewHolder.release();
        }
        if (mLiveContributeViewHolder != null) {
            mLiveContributeViewHolder.release();
        }
        if (mLiveLuckGiftTipViewHolder != null) {
            mLiveLuckGiftTipViewHolder.release();
        }
        if (mMobShareUtil != null) {
            mMobShareUtil.release();
        }
        if (mPayPresenter != null) {
            mPayPresenter.release();
        }
        mLiveChatRoomLinkMicViewHolder = null;
        mKeyBoardUtil = null;
        mLiveLinkMicPresenter = null;
        mLiveLinkMicAnchorPresenter = null;
        mLiveLinkMicPkPresenter = null;
        mLiveRoomViewHolder = null;
        mLiveAddImpressViewHolder = null;
        mLiveContributeViewHolder = null;
        mLiveLuckGiftTipViewHolder = null;
        mMobShareUtil = null;
        mPayPresenter = null;
        L.e("LiveActivity--------release------>");
    }

    @Override
    protected void onDestroy() {
        CommonAppConfig.getInstance().setTopActivityType(0);
        release();
        super.onDestroy();
    }

    public String getStream() {
        return mStream;
    }

    public String getTxAppId() {
        return mTxAppId;
    }

    /**
     * 是否是聊天室
     */
    public boolean isChatRoom() {
        return mIsChatRoom;
    }

    /**
     * 是否是视频聊天室
     */
    public boolean isChatRoomTypeVideo() {
        return mChatRoomType == Constants.CHAT_ROOM_TYPE_VIDEO;
    }

    public void setChatRoomType(int chatRoomType) {
        mChatRoomType = chatRoomType;
    }


    /**
     * 聊天室 申请上麦
     */
    public void voiceApplyUpMic() {
        LiveVoiceApplyUpFragment fragment = new LiveVoiceApplyUpFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_STREAM, mStream);
        bundle.putBoolean(Constants.ANCHOR, mIsAnchor);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveVoiceApplyUpFragment");
    }

    /**
     * 聊天室--送礼物时获取主播和麦上用户列表
     */
    public List<LiveVoiceGiftBean> getVoiceGiftUserList() {
        List<LiveVoiceGiftBean> list = new ArrayList<>();
        LiveVoiceGiftBean allBean = new LiveVoiceGiftBean();
        allBean.setType(-2);
        list.add(allBean);

        LiveVoiceGiftBean anchorBean = new LiveVoiceGiftBean();
        anchorBean.setType(-1);
        anchorBean.setUid(mLiveUid);
        if (mLiveBean != null) {
            anchorBean.setAvatar(mLiveBean.getAvatar());
        }
        list.add(anchorBean);
        if (mLiveChatRoomLinkMicViewHolder != null) {
            List<LiveVoiceGiftBean> userList = mLiveChatRoomLinkMicViewHolder.getVoiceGiftUserList();
            if (userList != null && userList.size() > 0) {
                list.addAll(userList);
            }
        }
        return list;
    }


    /**
     * 显示个人资料弹窗
     */
    public void showUserDialog(String toUid) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.showUserDialog(toUid);
        }
    }

    /**
     * 聊天室--判断用户是否上麦了
     *
     * @param toUid 要判断的用户uid
     */
    public Integer checkVoiceRoomUserUpMic(String toUid) {
        if (mLiveChatRoomLinkMicViewHolder != null) {
            LiveVoiceLinkMicBean bean = mLiveChatRoomLinkMicViewHolder.getUserBean(toUid);
            if (bean != null) {
                return bean.getStatus();
            }
        }
        return null;
    }

    /**
     * 语音聊天室--主播控制麦位 闭麦开麦禁麦等
     *
     * @param toUid    被操作人的uid
     * @param position 麦位
     * @param status   麦位的状态 -1 关麦；  0无人； 1开麦 ； 2 禁麦；
     */
    public void controlMicPosition(String toUid, int position, int status) {
        SocketVoiceRoomUtil.controlMicPosition(mSocketClient, toUid, position, status);
    }


    /**
     * 语音聊天室--切换开麦和闭麦
     *
     * @param toUid 被操作人的uid
     */
    public void changeVoiceMicOpen(final String toUid) {
        if (mLiveChatRoomLinkMicViewHolder == null) {
            return;
        }
        final int position = mLiveChatRoomLinkMicViewHolder.getUserPosition(toUid);
        if (position == -1) {
            return;
        }
        LiveVoiceLinkMicBean bean = mLiveChatRoomLinkMicViewHolder.getUserBean(position);
        if (bean == null) {
            return;
        }
        Boolean isCurOpen = null;
        if (bean.getStatus() == Constants.VOICE_CTRL_OPEN) {
            isCurOpen = true;
        } else if (bean.getStatus() == Constants.VOICE_CTRL_CLOSE) {
            isCurOpen = false;
        }
        if (isCurOpen == null) {
            return;
        }
        //当前是开，切为关
        final int targetStatus = isCurOpen ? Constants.VOICE_CTRL_CLOSE : Constants.VOICE_CTRL_OPEN;
        LiveHttpUtil.changeVoiceMicOpen(mStream, position, isCurOpen ? 0 : 1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    controlMicPosition(toUid, position, targetStatus);
                    if (mIsAnchor) {
                        ToastUtil.show(msg);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    /**
     * 语音聊天室--主播或管理员将麦上的用户下麦
     */
    public void closeUserVoiceMic(final String toUid, final int type) {
        LiveHttpUtil.closeUserVoiceMic(mStream, toUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    SocketVoiceRoomUtil.userDownMic(mSocketClient, toUid, type);
                }
                ToastUtil.show(msg);
            }
        });
    }


    /**
     * 打开充值窗口
     */
    public void openChargeWindow() {
        if (mPayPresenter == null) {
            mPayPresenter = new PayPresenter(this);
            mPayPresenter.setServiceNameAli(Constants.PAY_BUY_COIN_ALI);
            mPayPresenter.setServiceNameWx(Constants.PAY_BUY_COIN_WX);
            mPayPresenter.setServiceNamePaypal(Constants.PAY_BUY_COIN_PAYPAL);
            mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_COIN_URL);
            mPayPresenter.setServiceNamePaypal(Constants.PAY_BUY_COIN_PAYPAL);
            mPayPresenter.setPayCallback(new PayCallback() {
                @Override
                public void onSuccess() {
                    if (mPayPresenter != null) {
                        mPayPresenter.checkPayResult();
                    }
                }

                @Override
                public void onFailed() {

                }
            });
        }
        LiveChargeDialogFragment fragment = new LiveChargeDialogFragment();
        fragment.setPayPresenter(mPayPresenter);
        fragment.show(getSupportFragmentManager(), "ChatChargeDialogFragment");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppLifecycleEvent(AppLifecycleEvent e) {
        mAppFrontGroundChanged = true;
    }


    /**
     * 直播间观众列表弹窗
     */
    public void showUserListDialog() {
        LiveUserListDialog dialog = new LiveUserListDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putString(Constants.STREAM, mStream);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "LiveUserListDialog");
    }

    public int getLinkMicUid() {
        if (mLiveLinkMicPresenter != null) {
            String linkMicUid = mLiveLinkMicPresenter.getLinkMicUid();
            if (!TextUtils.isEmpty(linkMicUid)) {
                return Integer.parseInt(linkMicUid);
            }
        }
        return -1;
    }

    public int getLinkMicAnchorUid() {
        if (mLiveLinkMicAnchorPresenter != null) {
            String pkUid = mLiveLinkMicAnchorPresenter.getPkUid();
            if (!TextUtils.isEmpty(pkUid)) {
                return Integer.parseInt(pkUid);
            }
        }
        return -1;
    }

    public String getLinkMicAnchorStream() {
        if (mLiveLinkMicAnchorPresenter != null) {
            String pkStream = mLiveLinkMicAnchorPresenter.getPkStream();
            if (!TextUtils.isEmpty(pkStream)) {
                return pkStream;
            }
        }
        return "";
    }
}
