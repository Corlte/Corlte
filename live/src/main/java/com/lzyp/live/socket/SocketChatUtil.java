package com.lzyp.live.socket;

import android.text.TextUtils;

import com.lzyp.common.CommonAppConfig;
import com.lzyp.common.Constants;
import com.lzyp.common.bean.GoodsBean;
import com.lzyp.common.bean.UserBean;
import com.lzyp.common.utils.StringUtil;
import com.lzyp.common.utils.WordFilterUtil;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.live.R;

/**
 * Created by cxf on 2018/10/9.
 * 直播间发言
 */

public class SocketChatUtil {


    public static void sendChatMessage(SocketClient client, String content, boolean isAnchor, int userType, int guardType) {
        sendChatMessage(client, content, null, isAnchor, userType, guardType);
    }

    /**
     * 发言
     */
    public static void sendChatMessage(SocketClient client, String content, String contentEn, boolean isAnchor, int userType, int guardType) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        content = WordFilterUtil.getInstance().filter(content);
        SocketSendBean socketSendBean = new SocketSendBean()
                .param("_method_", Constants.SOCKET_SEND_MSG)
                .param("action", 0)
                .param("msgtype", 2)
                .param("usertype", userType)
                .param("isAnchor", isAnchor ? 1 : 0)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("liangname", u.getGoodName())
                .param("vip_type", u.getVip().getType())
                .param("guard_type", guardType)
                .param("ct", content);
        if (!TextUtils.isEmpty(contentEn)) {
            socketSendBean.param("ct_en", contentEn);
        }
        client.send(socketSendBean);
    }

    /**
     * 点亮
     */
    public static void sendLightMessage(SocketClient client, int heart, int guardType) {
        if (!CommonAppConfig.getInstance().isLogin()) {
            return;
        }
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SEND_MSG)
                .param("action", 0)
                .param("msgtype", 2)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("liangname", u.getGoodName())
                .param("vip_type", u.getVip().getType())
                .param("heart", heart)
                .param("guard_type", guardType)
                .param(Constants.SOCKET_CT_ZH, "我点亮了")
                .param(Constants.SOCKET_CT_EN, "I light up")
        );

    }

    /**
     * 发送飘心消息
     */
    public static void sendFloatHeart(SocketClient client) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_LIGHT)
                .param("action", 2)
                .param("msgtype", 0)
                .param("ct", ""));
    }

    /**
     * 发送弹幕消息
     */
    public static void sendDanmuMessage(SocketClient client, String danmuToken) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SEND_BARRAGE)
                .param("action", 7)
                .param("msgtype", 1)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("uhead", u.getAvatar())
                .param("ct", danmuToken));
    }

    /**
     * 发送礼物消息
     */
    public static void sendGiftMessage(SocketClient client, int giftType, String giftToken, String liveUid, String liveName) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SEND_GIFT)
                .param("action", 0)
                .param("msgtype", 1)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("uhead", u.getAvatar())
                .param("evensend", giftType)
                .param("liangname", u.getGoodName())
                .param("vip_type", u.getVip().getType())
                .param("ct", giftToken)
                .param("roomnum", liveUid)
                .param("livename", liveName)
                .paramJsonArray("paintedPath", "[]")
                .param("paintedWidth", "0")
                .param("paintedHeight", "0")
        );
    }


    /**
     * 发送礼物消息
     */
    public static void sendGiftMessage(SocketClient client, int giftType, String giftToken, String liveUid, String liveName, String paintedPath, int paintedWidth, int paintedHeight) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SEND_GIFT)
                .param("action", 0)
                .param("msgtype", 1)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("uhead", u.getAvatar())
                .param("evensend", giftType)
                .param("liangname", u.getGoodName())
                .param("vip_type", u.getVip().getType())
                .param("ct", giftToken)
                .param("roomnum", liveUid)
                .param("livename", liveName)
                .paramJsonArray("paintedPath", paintedPath)
                .param("paintedWidth", paintedWidth)
                .param("paintedHeight", paintedHeight)
        );
    }


    /**
     * 主播或管理员 踢人
     */
    public static void sendKickMessage(SocketClient client, String toUid, String toName) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_KICK)
                .param("action", 2)
                .param("msgtype", 4)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("touid", toUid)
                .param("toname", toName)
                .param(Constants.SOCKET_CT_ZH, StringUtil.contact(toName, "被踢出房间"))
                .param(Constants.SOCKET_CT_EN, StringUtil.contact(toName, " was kicked out of the room"))
        );
    }


    /**
     * 主播或管理员 禁言
     */
    public static void sendShutUpMessage(SocketClient client, String toUid, String toName, int type) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SHUT_UP)
                .param("action", 1)
                .param("msgtype", 4)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("touid", toUid)
                .param("toname", toName)
                .param(Constants.SOCKET_CT_ZH, StringUtil.contact(toName, type == 0 ? "被永久禁言" : "被本场禁言"))
                .param(Constants.SOCKET_CT_EN, StringUtil.contact(toName, type == 0 ? " is permanently banned" : " has been banned from this site"))
        );
    }

    /**
     * 设置或取消管理员消息
     */
    public static void sendSetAdminMessage(SocketClient client, int action, String toUid, String toName) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        String s = action == 1 ? WordUtil.getString(R.string.live_set_admin) : WordUtil.getString(R.string.live_set_admin_cancel);
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SET_ADMIN)
                .param("action", action)
                .param("msgtype", 1)
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param("touid", toUid)
                .param("toname", toName)
                .param(Constants.SOCKET_CT_ZH, StringUtil.contact(toName, action == 1 ? "被设为管理员" : "被取消管理员"))
                .param(Constants.SOCKET_CT_EN, StringUtil.contact(toName, action == 1 ? " is set as administrator" : " was removed as administrator"))
        );
    }

    /**
     * 超管关闭直播间
     */
    public static void superCloseRoom(SocketClient client) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_STOP_LIVE)
                .param("action", 19)
                .param("msgtype", 1)
                .param("ct", ""));
    }

    /**
     * 发系统消息
     */
    public static void sendSystemMessage(SocketClient client, String content, String contentEn) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_SYSTEM)
                .param("action", 13)
                .param("msgtype", 4)
                .param("level", u.getLevel())
                .param("uname", u.getUserNiceName())
                .param("uid", u.getId())
                .param(Constants.SOCKET_CT_ZH, content)
                .param(Constants.SOCKET_CT_EN, contentEn)
        );
    }


    /**
     * 获取僵尸粉
     */
    public static void getFakeFans(SocketClient client) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_FAKE_FANS)
                .param("action", "")
                .param("msgtype", ""));
    }


    /**
     * 更新主播映票数
     */
    public static void sendUpdateVotesMessage(SocketClient client, int votes, int first) {
        if (client == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_UPDATE_VOTES)
                .param("action", 1)
                .param("msgtype", 26)
                .param("votes", votes)
                .param("uid", CommonAppConfig.getInstance().getUid())
                .param("isfirst", first)
                .param("ct", ""));
    }

    /**
     * 更新主播映票数
     */
    public static void sendUpdateVotesMessage(SocketClient client, int votes) {
        sendUpdateVotesMessage(client, votes, 0);
    }

    /**
     * 发送购买守护成功消息
     */
    public static void sendBuyGuardMessage(SocketClient client, String votes, int guardNum, int guardType) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_BUY_GUARD)
                .param("action", 0)
                .param("msgtype", 0)
                .param("uid", u.getId())
                .param("uname", u.getUserNiceName())
                .param("uhead", u.getAvatar())
                .param("votestotal", votes)
                .param("guard_nums", guardNum)
                .param("guard_type", guardType));
    }

    /**
     * 发送发红包成功消息
     */
    public static void sendRedPackMessage(SocketClient client) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_RED_PACK)
                .param("action", 0)
                .param("msgtype", 0)
                .param("uid", u.getId())
                .param("uname", u.getUserNiceName())
                .param(Constants.SOCKET_CT_ZH, "在直播间发红包啦！快去抢哦～")
                .param(Constants.SOCKET_CT_EN, "give out red envelopes in the live broadcast room! Go and grab it~")
        );

    }


    /**
     * 直播间购物飘屏
     */
    public static void liveGoodsFloat(SocketClient client) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_LIVE_GOODS_FLOAT)
                .param("action", 0)
                .param("msgtype", 0)
                .param("uid", u.getId())
                .param("uname", u.getUserNiceName())
                .param("ct", "")
        );

    }


    /**
     * 发送展示直播间商品的消息
     */
    public static void sendLiveGoodsShow(SocketClient client, GoodsBean bean) {
        if (client == null) {
            return;
        }
        if (bean == null) {
            client.send(new SocketSendBean()
                    .param("_method_", Constants.SOCKET_LIVE_GOODS_SHOW)
                    .param("action", 0)
                    .param("msgtype", 0)
            );
        } else {
            client.send(new SocketSendBean()
                    .param("_method_", Constants.SOCKET_LIVE_GOODS_SHOW)
                    .param("action", 1)
                    .param("msgtype", 0)
                    .param("goodsid", bean.getId())
                    .param("goods_thumb", bean.getThumb())
                    .param("goods_name", bean.getName())
                    .param("goods_price", bean.getPriceNow())
                    .param("goods_type", bean.getType())
            );
        }

    }

    /**
     * 星球探宝中奖
     */
    public static void gameXqtbWin(SocketClient client, String list) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_XQTB_WIN)
                .paramJsonArray("list", list)
        );

    }

    /**
     * 幸运大转盘中奖
     */
    public static void gameLuckpanWin(SocketClient client, String list) {
        if (client == null) {
            return;
        }
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        client.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_LUCKPAN_WIN)
                .paramJsonArray("list", list)
        );

    }

}
