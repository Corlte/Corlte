package com.lzyp.common.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzyp.common.CommonAppConfig;
import com.lzyp.common.Constants;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.bean.CoinBean;
import com.lzyp.common.bean.CoinPayBean;
import com.lzyp.common.custom.ItemDecoration;
import com.lzyp.common.event.ChargeTypeEvent;
import com.lzyp.common.http.CommonHttpConsts;
import com.lzyp.common.http.CommonHttpUtil;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.interfaces.OnItemClickListener;
import com.lzyp.common.pay.PayPresenter;
import com.lzyp.common.utils.StringUtil;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.common.R;
import com.lzyp.common.adapter.ChatChargeCoinAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2019/4/22.
 */

public class LiveChargeDialogFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener<CoinBean>, LiveChargePayDialogFragment.ActionListener {

    private RecyclerView mRecyclerView;
    private TextView mBtnCharge;
    private List<CoinPayBean> mPayList;
    private ChatChargeCoinAdapter mAdapter;
    private CoinBean mCheckedCoinBean;
    private PayPresenter mPayPresenter;
    private boolean mIsPayPal;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_chat_charge;
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
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 10);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mBtnCharge = (TextView) findViewById(R.id.btn_charge);
        mBtnCharge.setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        EventBus.getDefault().register(this);
        loadData();
    }

    private void loadData() {
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mRecyclerView == null) {
                        return;
                    }
                    JSONObject obj = JSON.parseObject(info[0]);
                    List<CoinPayBean> paylist = JSON.parseArray(obj.getString("paylist"), CoinPayBean.class);
                    mPayList = paylist;
                    if (paylist != null && paylist.size() > 0) {
                        mIsPayPal = Constants.PAY_TYPE_PAYPAL.equals(paylist.get(0).getId());
                    }
                    List<CoinBean> list = JSON.parseArray(obj.getString("rules"), CoinBean.class);
                    if (list != null && list.size() > 0) {
                        CoinBean bean = list.get(0);
                        bean.setChecked(true);
                        mAdapter = new ChatChargeCoinAdapter(mContext, list);
                        mAdapter.setOnItemClickListener(LiveChargeDialogFragment.this);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.setIsPaypal(mIsPayPal);
                        showMoney(bean);
                    }
                    if (mPayPresenter != null) {
                        String coin = obj.getString("coin");
                        mPayPresenter.setBalanceValue(Long.parseLong(coin));
                        mPayPresenter.setAliPartner(obj.getString("aliapp_partner"));
                        mPayPresenter.setAliSellerId(obj.getString("aliapp_seller_id"));
                        mPayPresenter.setAliPrivateKey(obj.getString("aliapp_key_android"));
                        mPayPresenter.setWxAppID(obj.getString("wx_appid"));
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_close) {
            dismiss();
        } else if (i == R.id.btn_charge) {
            charge();
        }
    }

    @Override
    public void onItemClick(CoinBean bean, int position) {
        showMoney(bean);
    }

    private void showMoney(CoinBean bean) {
        mCheckedCoinBean = bean;
        if (mCheckedCoinBean != null && mBtnCharge != null) {
            String money = StringUtil.contact(mIsPayPal ? "$" : "￥", bean.getMoney());
            mBtnCharge.setText(String.format(WordUtil.getString(R.string.chat_charge_tip), money));
        }
    }

    private void charge() {
        if (mCheckedCoinBean == null || mPayList == null || mPayList.size() == 0) {
            return;
        }
        LiveChargePayDialogFragment fragment = new LiveChargePayDialogFragment();
        fragment.setCoinBean(mCheckedCoinBean);
        fragment.setPayList(mPayList);
        fragment.setActionListener(this);
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "ChatChargePayDialogFragment");
    }

    @Override
    public void onChargeClick(CoinPayBean coinPayBean) {
        if (mPayPresenter != null && mCheckedCoinBean != null) {
            String href = coinPayBean.getHref();
            if (TextUtils.isEmpty(href)) {
                String money = mCheckedCoinBean.getMoney();
                String coin = Constants.PAY_TYPE_PAYPAL.equals(coinPayBean.getId()) ? mCheckedCoinBean.getCoinPaypal() : mCheckedCoinBean.getCoin();
                String goodsName = StringUtil.contact(coin, CommonAppConfig.getInstance().getCoinName());
                Map<String, String> orderParams = new HashMap<>();
                orderParams.put("uid", CommonAppConfig.getInstance().getUid());
                orderParams.put("token", CommonAppConfig.getInstance().getToken());
                orderParams.put("money", money);
                orderParams.put("changeid", mCheckedCoinBean.getId());
                orderParams.put("coin", coin);
                mPayPresenter.pay(coinPayBean.getId(), money, goodsName, orderParams);
            } else {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(href));
                mContext.startActivity(intent);
            }
        }
        dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChargeTypeEvent(ChargeTypeEvent e) {
        boolean isPayPal = Constants.PAY_TYPE_PAYPAL.equals(e.getPayType());
        if (mIsPayPal != isPayPal) {
            if (mAdapter != null) {
                CoinBean coinBean = mAdapter.getCheckedBean();
                if (coinBean != null && mBtnCharge != null) {
                    String money = StringUtil.contact(isPayPal ? "$" : "￥", coinBean.getMoney());
                    mBtnCharge.setText(String.format(WordUtil.getString(R.string.chat_charge_tip), money));
                }
            }
        }
        mIsPayPal = isPayPal;
        if (mAdapter != null) {
            mAdapter.setIsPaypal(isPayPal);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_BALANCE);
        mPayPresenter = null;
        super.onDestroy();
    }

    public void setPayPresenter(PayPresenter payPresenter) {
        mPayPresenter = payPresenter;
    }
}
