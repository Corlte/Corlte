package com.lzyp.video.activity;

import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.lzyp.common.Constants;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.adapter.RefreshAdapter;
import com.lzyp.common.bean.UserBean;
import com.lzyp.common.custom.CommonRefreshView;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.interfaces.OnItemClickListener;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.video.R;
import com.lzyp.video.adapter.AtFriendAdapter;
import com.lzyp.video.http.VideoHttpConsts;
import com.lzyp.video.http.VideoHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 云豹科技 on 2022/1/13.
 */
public class AtFriendActivity extends AbsActivity {


    private CommonRefreshView mRefreshView;
    private AtFriendAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_at_friend;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.a_084));
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<UserBean>() {
            @Override
            public RefreshAdapter<UserBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new AtFriendAdapter(mContext);
                    mAdapter.setOnItemClickListener(new OnItemClickListener<UserBean>() {
                        @Override
                        public void onItemClick(UserBean bean, int position) {
                            Intent intent = new Intent();
                            intent.putExtra(Constants.USER_BEAN, bean);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                VideoHttpUtil.getAtFriendList(p, callback);
            }

            @Override
            public List<UserBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), UserBean.class);
            }

            @Override
            public void onRefreshSuccess(List<UserBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<UserBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }

    @Override
    protected void onDestroy() {
        VideoHttpUtil.cancel(VideoHttpConsts.GET_AT_FRIEND_LIST);
        super.onDestroy();
    }
}
