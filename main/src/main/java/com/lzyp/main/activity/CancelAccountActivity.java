package com.lzyp.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.lzyp.common.CommonAppConfig;
import com.lzyp.common.Constants;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.utils.DialogUitl;
import com.lzyp.common.utils.L;
import com.lzyp.common.utils.ToastUtil;
import com.lzyp.common.utils.UmengUtil;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.im.utils.ImMessageUtil;
import com.lzyp.main.R;
import com.lzyp.main.http.MainHttpConsts;
import com.lzyp.main.http.MainHttpUtil;

/**
 * 注销账号
 */
public class CancelAccountActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, String url) {
        Intent intent = new Intent(context, CancelAccountActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }

    private ProgressBar mProgressBar;
    private WebView mWebView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cancel_account;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.cancel_account_0));
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        String url = CommonAppConfig.getHtmlUrl(getIntent().getStringExtra(Constants.URL));
        L.e("H5--->" + url);
        mProgressBar = findViewById(com.lzyp.common.R.id.progressbar);
        mWebView = new WebView(mContext);
        mWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        ViewGroup container = findViewById(R.id.container);
        container.addView(mWebView);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                }
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        mWebView.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_confirm) {
            cancelAccount();
        } else if (id == R.id.btn_cancel) {
            finish();
        }
    }

    private void cancelAccount() {
        new DialogUitl.Builder(mContext)
                .setContent(WordUtil.getString(R.string.cancel_account_2))
                .setBackgroundDimEnabled(true)
                .setCancelable(true)
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        MainHttpUtil.cancelAccount(new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0) {
                                    finish();
                                    CommonAppConfig.getInstance().clearLoginInfo();
                                    //退出IM
                                    ImMessageUtil.getInstance().logoutImClient();
                                    //友盟统计登出
                                    UmengUtil.userLogout();
                                    MainActivity.forward(mContext);
                                }
                                ToastUtil.show(msg);
                            }
                        });
                    }
                })
                .build()
                .show();


    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.CANCEL_ACCOUNT);
        if (mWebView != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.destroy();
        }
        super.onDestroy();
    }
}
