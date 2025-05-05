package com.lzyp.main.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lzyp.common.Constants;
import com.lzyp.common.dialog.AbsDialogFragment;
import com.lzyp.common.utils.DpUtil;
import com.lzyp.main.R;

public class LoginForbiddenDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_login_forbidden;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(300);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView tip = findViewById(R.id.tip);
        TextView time = findViewById(R.id.time);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tip.setText(bundle.getString(Constants.TIP));
            time.setText(bundle.getString(Constants.UID));
        }
        findViewById(R.id.btn_close).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
