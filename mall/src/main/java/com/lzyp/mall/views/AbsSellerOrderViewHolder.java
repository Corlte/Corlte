package com.lzyp.mall.views;

import android.app.Dialog;
import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzyp.common.Constants;
import com.lzyp.common.HtmlConfig;
import com.lzyp.common.activity.WebViewActivity;
import com.lzyp.common.adapter.RefreshAdapter;
import com.lzyp.common.custom.CommonRefreshView;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.utils.DialogUitl;
import com.lzyp.common.utils.StringUtil;
import com.lzyp.common.utils.ToastUtil;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.common.views.AbsCommonViewHolder;
import com.lzyp.im.activity.ChatRoomActivity;
import com.lzyp.im.bean.ImConUserBean;
import com.lzyp.im.http.ImHttpUtil;
import com.lzyp.mall.R;
import com.lzyp.mall.activity.SellerOrderActivity;
import com.lzyp.mall.activity.SellerOrderDetailActivity;
import com.lzyp.mall.activity.SellerRefundDetailActivity;
import com.lzyp.mall.activity.SellerSendActivity;
import com.lzyp.mall.adapter.SellerOrderBaseAdapter;
import com.lzyp.mall.bean.SellerOrderBean;
import com.lzyp.mall.http.MallHttpUtil;

import java.util.List;

public abstract class AbsSellerOrderViewHolder extends AbsCommonViewHolder implements SellerOrderBaseAdapter.ActionListener {

    private CommonRefreshView mRefreshView;
    private SellerOrderBaseAdapter mAdapter;
    private String mNumJsonString;

    public AbsSellerOrderViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_seller_order_list;
    }

    @Override
    public void init() {
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_buyer_order);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<SellerOrderBean>() {
            @Override
            public RefreshAdapter<SellerOrderBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = getSellerOrderAdapter();
                    mAdapter.setActionListener(AbsSellerOrderViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MallHttpUtil.getSellerOrderList(getOrderType(), p, callback);
            }

            @Override
            public List<SellerOrderBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                mNumJsonString = obj.getString("type_list_nums");
                return JSON.parseArray(obj.getString("list"), SellerOrderBean.class);
            }

            @Override
            public void onRefreshSuccess(List<SellerOrderBean> list, int listCount) {
                if (!TextUtils.isEmpty(mNumJsonString) && mContext != null) {
                    ((SellerOrderActivity) mContext).setOrderNum(mNumJsonString);
                }
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<SellerOrderBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    public abstract String getOrderType();

    public abstract SellerOrderBaseAdapter getSellerOrderAdapter();

    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    public void refreshData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    /**
     * 点击item
     */
    @Override
    public void onItemClick(SellerOrderBean bean) {
        if (bean.getStatus() == Constants.MALL_ORDER_STATUS_REFUND) {
            SellerRefundDetailActivity.forward(mContext, bean.getId());
        } else {
            SellerOrderDetailActivity.forward(mContext, bean.getId());
        }
    }


    /**
     * 去发货
     */
    @Override
    public void onSendClick(SellerOrderBean bean) {
        SellerSendActivity.forward(mContext, bean.getId());
    }

    /**
     * 删除订单
     */
    @Override
    public void onDeleteClick(final SellerOrderBean bean) {
        new DialogUitl.Builder(mContext)
                .setContent(WordUtil.getString(R.string.mall_370))
                .setCancelable(true)
                .setBackgroundDimEnabled(true)
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        MallHttpUtil.sellerDeleteOrder(bean.getId(), new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0) {
                                    refreshData();
                                }
                                ToastUtil.show(msg);
                            }
                        });
                    }
                })
                .build()
                .show();
    }

    /**
     * 查看物流
     */
    @Override
    public void onWuLiuClick(SellerOrderBean bean) {
        String url = StringUtil.contact(HtmlConfig.MALL_BUYER_WULIU, "orderid=", bean.getId(), "&user_type=seller");
        WebViewActivity.forward(mContext, url);
    }

    /**
     * 退款详情
     */
    @Override
    public void onRefundClick(SellerOrderBean bean) {
        SellerRefundDetailActivity.forward(mContext, bean.getId());
    }

    /**
     * 联系买家
     */
    @Override
    public void onContactBuyerClick(SellerOrderBean bean) {
        ImHttpUtil.getImUserInfo(bean.getUid(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    ImConUserBean bean = JSON.parseObject(info[0], ImConUserBean.class);
                    if (bean != null) {
                        ChatRoomActivity.forward(mContext, bean, bean.getAttent() == 1, false);
                    }
                }
            }
        });
    }

}
