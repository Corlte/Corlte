package com.lzyp.common.pay.wx;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.lzyp.common.R;
import com.lzyp.common.http.CommonHttpUtil;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.pay.PayCallback;
import com.lzyp.common.utils.DialogUitl;
import com.lzyp.common.utils.L;
import com.lzyp.common.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by cxf on 2017/9/22.
 */

public class WxPayBuilder {

    private Context mContext;
    private String mAppId;
    private PayCallback mPayCallback;

    public WxPayBuilder(Context context, String appId) {
        mContext = context;
        mAppId = appId;
        WxApiWrapper.getInstance().setAppID(appId);
        EventBus.getDefault().register(this);
    }

    public WxPayBuilder setPayCallback(PayCallback callback) {
        mPayCallback = new WeakReference<>(callback).get();
        return this;
    }

    public void pay(String serviceName, Map<String, String> orderParams) {
        CommonHttpUtil.getWxOrder(serviceName, orderParams, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String partnerId = obj.getString("partnerid");
                    String prepayId = obj.getString("prepayid");
                    String packageValue = obj.getString("package");
                    String nonceStr = obj.getString("noncestr");
                    String timestamp = obj.getString("timestamp");
                    String sign = obj.getString("sign");
                    if (TextUtils.isEmpty(partnerId) ||
                            TextUtils.isEmpty(prepayId) ||
                            TextUtils.isEmpty(packageValue) ||
                            TextUtils.isEmpty(nonceStr) ||
                            TextUtils.isEmpty(timestamp) ||
                            TextUtils.isEmpty(sign)) {
                        ToastUtil.show(R.string.pay_wx_not_enable);
                        return;
                    }
                    PayReq req = new PayReq();
                    req.appId = mAppId;
                    req.partnerId = partnerId;
                    req.prepayId = prepayId;
                    req.packageValue = packageValue;
                    req.nonceStr = nonceStr;
                    req.timeStamp = timestamp;
                    req.sign = sign;
                    IWXAPI wxApi = WxApiWrapper.getInstance().getWxApi();
                    if (wxApi == null) {
                        ToastUtil.show(R.string.coin_charge_failed);
                        return;
                    }
                    boolean result = wxApi.sendReq(req);
                    if (!result) {
                        ToastUtil.show(R.string.coin_charge_failed);
                    }
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayResponse(BaseResp resp) {
        L.e("resp---微信支付回调---->" + resp.errCode);
        if (mPayCallback != null) {
            if (0 == resp.errCode) {//支付成功
                mPayCallback.onSuccess();
            } else {//支付失败
                mPayCallback.onFailed();
            }
        }
        mContext = null;
        mPayCallback = null;
        EventBus.getDefault().unregister(this);
    }


}
