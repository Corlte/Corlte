package com.lzyp.main.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzyp.common.CommonAppConfig;
import com.lzyp.common.Constants;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.adapter.RefreshAdapter;
import com.lzyp.common.bean.LiveClassBean;
import com.lzyp.common.custom.CommonRefreshView;
import com.lzyp.common.custom.ItemDecoration;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.interfaces.OnItemClickListener;
import com.lzyp.common.utils.FloatWindowHelper;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.live.bean.LiveBean;
import com.lzyp.live.utils.LiveStorge;
import com.lzyp.main.R;
import com.lzyp.main.adapter.MainHomeFollowAdapter;
import com.lzyp.main.adapter.MainLiveClassAdapter;
import com.lzyp.main.http.MainHttpConsts;
import com.lzyp.main.http.MainHttpUtil;
import com.lzyp.main.presenter.CheckLivePresenter;

import java.util.List;

/**
 * Created by 云豹科技 on 2022/5/31.
 */
public class LiveFollowActivity extends AbsActivity implements OnItemClickListener<LiveBean> {


    private CommonRefreshView mRefreshView;
    private MainHomeFollowAdapter mAdapter;
    private int mLiveClassId;
    private CheckLivePresenter mCheckLivePresenter;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, LiveFollowActivity.class));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_follow;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.a_104));
        RecyclerView classRecyclerView = findViewById(R.id.recyclerView_class);
        classRecyclerView.setHasFixedSize(true);
        classRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        MainLiveClassAdapter classAdapter = new MainLiveClassAdapter(mContext);
        classAdapter.setOnItemClickListener(new OnItemClickListener<LiveClassBean>() {
            @Override
            public void onItemClick(LiveClassBean bean, int position) {
                mLiveClassId = bean.getId();
                if (mRefreshView != null) {
                    mRefreshView.initData();
                }
            }
        });
        classRecyclerView.setAdapter(classAdapter);
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live_follow);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveBean>() {
            @Override
            public RefreshAdapter<LiveBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeFollowAdapter(mContext);
                    mAdapter.setOnItemClickListener(LiveFollowActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getFollow(p, mLiveClassId, callback);
            }

            @Override
            public List<LiveBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                return JSON.parseArray(obj.getString("list"), LiveBean.class);
            }

            @Override
            public void onRefreshSuccess(List<LiveBean> adapterItemList, int allItemCount) {
                if (CommonAppConfig.LIVE_ROOM_SCROLL) {
                    LiveStorge.getInstance().put(Constants.LIVE_FOLLOW, adapterItemList);
                }
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
    public void onItemClick(LiveBean liveBean, int position) {
        if (!FloatWindowHelper.checkVoice(true)) {
            return;
        }
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            mCheckLivePresenter.watchLive(liveBean, Constants.LIVE_FOLLOW, position);
        } else {
            mCheckLivePresenter.watchLive(liveBean);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    public void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_FOLLOW);
        super.onDestroy();

    }

}
