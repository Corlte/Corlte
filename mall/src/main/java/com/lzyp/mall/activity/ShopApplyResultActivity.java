package com.lzyp.mall.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzyp.common.Constants;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.utils.ToastUtil;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.mall.R;
import com.lzyp.mall.http.MallHttpConsts;
import com.lzyp.mall.http.MallHttpUtil;

/**
 * 开通小店申请结果
 */
public class ShopApplyResultActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, boolean applyFailed, String failedReason) {
        Intent intent = new Intent(context, ShopApplyResultActivity.class);
        intent.putExtra(Constants.MALL_APPLY_FAILED, applyFailed);
        intent.putExtra(Constants.TIP, failedReason);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shop_apply_result;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.mall_014));
        boolean applyFailed = getIntent().getBooleanExtra(Constants.MALL_APPLY_FAILED, false);
        if (applyFailed) {
            findViewById(R.id.group_failed).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_auth).setOnClickListener(this);
            String failedReason = getIntent().getStringExtra(Constants.TIP);
            TextView tvReason = findViewById(R.id.reason);
            if (tvReason != null) {
                tvReason.setText(failedReason);
            }
        } else {
            findViewById(R.id.group_wait).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        MallHttpUtil.getUserAuthInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        finish();
                        ShopApplyActivity.forward(mContext, obj.getString("cer_no"), obj.getString("real_name"), true);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        MallHttpUtil.cancel(MallHttpConsts.GET_USER_AUTH_INFO);
        super.onDestroy();
    }
}
