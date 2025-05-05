package com.lzyp.main.activity;

import android.view.View;
import android.widget.TextView;

import com.lzyp.common.CommonAppConfig;
import com.lzyp.common.Constants;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.utils.RouteUtil;
import com.lzyp.im.utils.ImMessageUtil;
import com.lzyp.main.R;

/**
 * Created by cxf on 2017/10/9.
 * 登录失效的时候以dialog形式弹出的activity
 */
public class LoginInvalidActivity extends AbsActivity implements View.OnClickListener {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_invalid;
    }

    @Override
    protected void main() {
        TextView textView = (TextView) findViewById(R.id.content);
        String tip = getIntent().getStringExtra(Constants.TIP);
        textView.setText(tip);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        CommonAppConfig.getInstance().clearLoginInfo();
        //退出IM
        ImMessageUtil.getInstance().logoutImClient();
        RouteUtil.forwardLogin(mContext);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
