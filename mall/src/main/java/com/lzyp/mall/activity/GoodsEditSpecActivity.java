package com.lzyp.mall.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzyp.common.Constants;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.utils.ToastUtil;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.mall.R;
import com.lzyp.mall.adapter.GoodsEditSpecAdapter;
import com.lzyp.mall.bean.AddGoodsSpecBean;
import com.lzyp.mall.http.MallHttpConsts;
import com.lzyp.mall.http.MallHttpUtil;

import java.util.List;

/**
 * 卖家修改商品的价格与库存
 */
public class GoodsEditSpecActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, String goodsId) {
        Intent intent = new Intent(context, GoodsEditSpecActivity.class);
        intent.putExtra(Constants.MALL_GOODS_ID, goodsId);
        context.startActivity(intent);
    }

    private String mGoodsId;
    private RecyclerView mRecyclerView;
    private GoodsEditSpecAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_goods_edit_spec;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.mall_112));
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mGoodsId = getIntent().getStringExtra(Constants.MALL_GOODS_ID);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        MallHttpUtil.getGoodsInfo(mGoodsId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    JSONObject goodsInfo = obj.getJSONObject("goods_info");
                    List<AddGoodsSpecBean> list = JSON.parseArray(goodsInfo.getString("specs"), AddGoodsSpecBean.class);
                    mAdapter = new GoodsEditSpecAdapter(mContext, list);
                    if (mRecyclerView != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_submit) {
            submit();
        }
    }

    private void submit() {
        if (mAdapter == null) {
            return;
        }
        List<AddGoodsSpecBean> list = mAdapter.getList();
        if (list == null || list.size() == 0) {
            return;
        }
        String specs = JSON.toJSONString(list);
        MallHttpUtil.goodsModifySpec(mGoodsId, specs, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    finish();
                }
                ToastUtil.show(msg);
            }
        });
    }


    @Override
    protected void onDestroy() {
        MallHttpUtil.cancel(MallHttpConsts.GET_GOODS_INFO);
        MallHttpUtil.cancel(MallHttpConsts.GOODS_MODIFY_SPEC);
        super.onDestroy();
    }
}
