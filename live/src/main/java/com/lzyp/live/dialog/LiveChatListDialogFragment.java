package com.lzyp.live.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.lzyp.common.CommonAppConfig;
import com.lzyp.common.Constants;
import com.lzyp.common.dialog.AbsDialogFragment;
import com.lzyp.common.utils.DpUtil;
import com.lzyp.common.utils.RouteUtil;
import com.lzyp.common.utils.ToastUtil;
import com.lzyp.im.bean.ImConUserBean;
import com.lzyp.im.views.ImConversationViewHolder;
import com.lzyp.live.R;
import com.lzyp.live.activity.LiveActivity;

/**
 * Created by cxf on 2018/10/24.
 * 直播间私信聊天列表
 */

public class LiveChatListDialogFragment extends AbsDialogFragment {

    private ImConversationViewHolder mImConversationViewHolder;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_empty;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(300);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mImConversationViewHolder = new ImConversationViewHolder(mContext, (ViewGroup) mRootView, ImConversationViewHolder.TYPE_DIALOG);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String liveUid = bundle.getString(Constants.LIVE_UID);
            mImConversationViewHolder.setLiveUid(liveUid);
        }
        mImConversationViewHolder.setActionListener(new ImConversationViewHolder.ActionListener() {
            @Override
            public void onCloseClick() {
                dismiss();
            }

            @Override
            public void onItemClick(ImConUserBean bean) {
                if (Constants.MALL_GOODS_ORDER.equals(bean.getId())) {
                    if (CommonAppConfig.getInstance().isTeenagerType()) {
                        ToastUtil.show(com.lzyp.common.R.string.a_137);
                    }else{
                        RouteUtil.forward(mContext, "com.lzyp.mall.activity.OrderMessageActivity");
                    }
                } else {
                    ((LiveActivity) mContext).openChatRoomWindow(bean, bean.getAttent() == 1);
                }
            }
        });
        mImConversationViewHolder.addToParent();
        mImConversationViewHolder.loadData();
    }

    @Override
    public void onDestroy() {
        if (mImConversationViewHolder != null) {
            mImConversationViewHolder.release();
        }
        super.onDestroy();
    }
}
