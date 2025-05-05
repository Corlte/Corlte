package com.lzyp.main.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.lzyp.common.Constants;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.interfaces.ActivityResultCallback;
import com.lzyp.common.utils.ActivityResultUtil;
import com.lzyp.live.activity.LiveAddImpressActivity;
import com.lzyp.main.R;
import com.lzyp.main.views.UserHomeViewHolder;

/**
 * Created by cxf on 2018/9/25.
 */
public class UserHomeActivity extends AbsActivity {

    private UserHomeViewHolder mUserHomeViewHolder;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_empty;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        String toUid = intent.getStringExtra(Constants.TO_UID);
        if (TextUtils.isEmpty(toUid)) {
            return;
        }
        boolean fromLiveRoom = intent.getBooleanExtra(Constants.FROM_LIVE_ROOM, false);
        String fromLiveUid = fromLiveRoom ? intent.getStringExtra(Constants.LIVE_UID) : null;
        mUserHomeViewHolder = new UserHomeViewHolder(mContext, (ViewGroup) findViewById(R.id.container), toUid, fromLiveRoom,fromLiveUid);
        mUserHomeViewHolder.addToParent();
        mUserHomeViewHolder.subscribeActivityLifeCycle();
        mUserHomeViewHolder.loadData();
    }


    public void addImpress(String toUid) {
        Intent intent = new Intent(mContext, LiveAddImpressActivity.class);
        intent.putExtra(Constants.TO_UID, toUid);
        ActivityResultUtil.startActivityForResult(this, intent, new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (mUserHomeViewHolder != null) {
                    mUserHomeViewHolder.refreshImpress();
                }
            }
        });
    }



    @Override
    protected void onDestroy() {
        if (mUserHomeViewHolder != null) {
            mUserHomeViewHolder.release();
        }
        super.onDestroy();
    }
}
