package com.lzyp.mall.activity;

import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.lzyp.common.Constants;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.utils.ToastUtil;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.mall.R;
import com.lzyp.mall.adapter.ManageClassAdapter;
import com.lzyp.mall.bean.ManageClassBean;
import com.lzyp.mall.http.MallHttpConsts;
import com.lzyp.mall.http.MallHttpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 经营类目设置 经营类目设置
 */
public class ManageClassActivity extends AbsActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private ManageClassAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manage_class;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.mall_042));
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        findViewById(R.id.btn_submit).setOnClickListener(this);
        final List<String> idList = getIntent().getStringArrayListExtra(Constants.MALL_APPLY_MANAGE_CLASS);
        MallHttpUtil.getManageClass(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<ManageClassBean> list = JSON.parseArray(Arrays.toString(info), ManageClassBean.class);
                    if (idList != null && idList.size() > 0) {
                        for (ManageClassBean bean : list) {
                            for (String id : idList) {
                                if (id.equals(bean.getId())) {
                                    bean.setChecked(true);
                                    break;
                                }
                            }
                        }
                    }
                    if (mRecyclerView != null) {
                        mAdapter = new ManageClassAdapter(mContext, list);
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
        if (mAdapter != null) {
            ArrayList<ManageClassBean> list = mAdapter.getCheckedList();
            if (list != null && list.size() > 0) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(Constants.MALL_APPLY_MANAGE_CLASS, list);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                ToastUtil.show(R.string.mall_044);
            }
        }
    }

    @Override
    protected void onDestroy() {
        MallHttpUtil.cancel(MallHttpConsts.GET_MANAGE_CLASS);
        super.onDestroy();
    }
}
