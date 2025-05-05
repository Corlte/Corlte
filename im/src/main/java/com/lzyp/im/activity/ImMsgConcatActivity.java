package com.lzyp.im.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.adapter.RefreshAdapter;
import com.lzyp.common.custom.CommonRefreshView;
import com.lzyp.common.event.FollowEvent;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.im.R;
import com.lzyp.im.adapter.ImMsgConcatAdapter;
import com.lzyp.im.bean.VideoImConcatBean;
import com.lzyp.im.http.ImHttpConsts;
import com.lzyp.im.http.ImHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 云豹科技 on 2022/1/18.
 */
public class ImMsgConcatActivity extends AbsActivity {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ImMsgConcatActivity.class));
    }

    private CommonRefreshView mRefreshView;
    private ImMsgConcatAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_im_msg;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.a_085));
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<VideoImConcatBean>() {
            @Override
            public RefreshAdapter<VideoImConcatBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new ImMsgConcatAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                ImHttpUtil.getImConcatList(p, callback);
            }

            @Override
            public List<VideoImConcatBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), VideoImConcatBean.class);
            }

            @Override
            public void onRefreshSuccess(List<VideoImConcatBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<VideoImConcatBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        EventBus.getDefault().register(this);
        mRefreshView.initData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (mAdapter != null) {
            mAdapter.updateItem(e.getToUid(), e.getIsAttention());
        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        ImHttpUtil.cancel(ImHttpConsts.GET_IM_CONCAT_LIST);
        super.onDestroy();
    }
}
