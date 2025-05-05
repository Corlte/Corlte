package com.lzyp.mall.views;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzyp.common.adapter.RefreshAdapter;
import com.lzyp.common.custom.CommonRefreshView;
import com.lzyp.common.custom.ItemDecoration;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.interfaces.OnItemClickListener;
import com.lzyp.common.views.AbsCommonViewHolder;
import com.lzyp.mall.R;
import com.lzyp.mall.activity.GoodsDetailActivity;
import com.lzyp.mall.activity.ShopHomeActivity;
import com.lzyp.mall.adapter.ShopHomeMyAdapter;
import com.lzyp.mall.bean.GoodsSimpleBean;
import com.lzyp.mall.http.MallHttpConsts;
import com.lzyp.mall.http.MallHttpUtil;

import java.util.List;

/**
 * 店铺 我的商品
 */
public class ShopHomeMyViewHolder extends AbsCommonViewHolder implements OnItemClickListener<GoodsSimpleBean> {

    private CommonRefreshView mRefreshView;
    private ShopHomeMyAdapter mAdapter;
    private String mToUid;

    public ShopHomeMyViewHolder(Context context, ViewGroup parentView, String toUid) {
        super(context, parentView, toUid);
    }

    @Override
    protected void processArguments(Object... args) {
        if (args.length > 0) {
            mToUid = (String) args[0];
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_shop_home_my;
    }

    @Override
    public void init() {
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_goods_seller_2);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 10, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mAdapter = new ShopHomeMyAdapter(mContext);
        mAdapter.setOnItemClickListener(this);
        mRefreshView.setRecyclerViewAdapter(mAdapter);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<GoodsSimpleBean>() {
            @Override
            public RefreshAdapter<GoodsSimpleBean> getAdapter() {
                return null;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MallHttpUtil.getShopHome(mToUid, p, callback);
            }

            @Override
            public List<GoodsSimpleBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                ((ShopHomeActivity) mContext).showShopInfo(obj.getJSONObject("shop_info"));
                return JSON.parseArray(obj.getString("list"), GoodsSimpleBean.class);
            }

            @Override
            public void onRefreshSuccess(List<GoodsSimpleBean> list, int listCount) {
                ((ShopHomeActivity) mContext).finishRefresh();
            }

            @Override
            public void onRefreshFailure() {
                ((ShopHomeActivity) mContext).finishRefresh();
            }

            @Override
            public void onLoadMoreSuccess(List<GoodsSimpleBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }


    @Override
    public void onItemClick(GoodsSimpleBean bean, int position) {
        GoodsDetailActivity.forward(mContext, bean.getId(), true, bean.getType());
    }

    @Override
    public void onDestroy() {
        MallHttpUtil.cancel(MallHttpConsts.GET_SHOP_HOME);
        super.onDestroy();
    }
}
