package com.lzyp.main.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.adapter.RefreshAdapter;
import com.lzyp.common.custom.CommonRefreshView;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.interfaces.OnItemClickListener;
import com.lzyp.common.utils.FloatWindowHelper;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.live.bean.LiveBean;
import com.lzyp.main.R;
import com.lzyp.main.adapter.LiveVoiceListAdapter;
import com.lzyp.main.http.MainHttpConsts;
import com.lzyp.main.http.MainHttpUtil;
import com.lzyp.main.presenter.CheckLivePresenter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/10/27.
 * 语音直播间列表
 */

public class LiveVoiceRoomListActivity extends AbsActivity implements OnItemClickListener<LiveBean> {

    private CommonRefreshView mRefreshView;
    private LiveVoiceListAdapter mAdapter;
    private CheckLivePresenter mCheckLivePresenter;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, LiveVoiceRoomListActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_voice;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.a_060));
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveBean>() {
            @Override
            public RefreshAdapter<LiveBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new LiveVoiceListAdapter(mContext);
                    mAdapter.setOnItemClickListener(LiveVoiceRoomListActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getVoiceRoomList(p, callback);
            }

            @Override
            public List<LiveBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), LiveBean.class);
            }

            @Override
            public void onRefreshSuccess(List<LiveBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<LiveBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    @Override
    public void onItemClick(LiveBean bean, int position) {
        watchLive(bean, position);
    }


    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, int position) {
        if (!FloatWindowHelper.checkVoice(true)) {
            return;
        }
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        mCheckLivePresenter.watchLive(liveBean);
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_VOICE_ROOM_LIST);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

}
