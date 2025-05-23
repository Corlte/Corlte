package com.lzyp.im.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.adapter.RefreshAdapter;
import com.lzyp.common.custom.CommonRefreshView;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.im.R;
import com.lzyp.im.adapter.ImMsgLikeAdapter;
import com.lzyp.im.bean.VideoImMsgBean;
import com.lzyp.im.http.ImHttpConsts;
import com.lzyp.im.http.ImHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 云豹科技 on 2022/1/18.
 */
public class ImMsgLikeActivity extends AbsActivity {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ImMsgLikeActivity.class));
    }

    private CommonRefreshView mRefreshView;
    private ImMsgLikeAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_im_msg;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.a_086));
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<VideoImMsgBean>() {
            @Override
            public RefreshAdapter<VideoImMsgBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new ImMsgLikeAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                ImHttpUtil.getImLikeList(p, callback);
            }

            @Override
            public List<VideoImMsgBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), VideoImMsgBean.class);
            }

            @Override
            public void onRefreshSuccess(List<VideoImMsgBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<VideoImMsgBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }


    @Override
    protected void onDestroy() {
        ImHttpUtil.cancel(ImHttpConsts.GET_IM_LIKE_LISTS);
        super.onDestroy();
    }
}
