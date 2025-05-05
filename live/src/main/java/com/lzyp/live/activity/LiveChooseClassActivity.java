package com.lzyp.live.activity;

import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lzyp.common.CommonAppConfig;
import com.lzyp.common.Constants;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.bean.ConfigBean;
import com.lzyp.common.bean.LiveClassBean;
import com.lzyp.common.interfaces.CommonCallback;
import com.lzyp.common.interfaces.OnItemClickListener;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.live.R;
import com.lzyp.live.adapter.LiveReadyClassAdapter;

import java.util.List;

/**
 * Created by cxf on 2018/10/7.
 * 选择直播频道
 */

public class LiveChooseClassActivity extends AbsActivity implements OnItemClickListener<LiveClassBean> {

    private RecyclerView mRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_choose_class;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.live_class_choose));
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        final int checkedClassId = getIntent().getIntExtra(Constants.CLASS_ID, 0);
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    List<LiveClassBean> list = configBean.getLiveClass();
                    if (list == null) {
                        return;
                    }
                    for (int i = 0, size = list.size(); i < size; i++) {
                        LiveClassBean bean = list.get(i);
                        if (bean.getId() == checkedClassId) {
                            bean.setChecked(true);
                        } else {
                            bean.setChecked(false);
                        }
                    }
                    LiveReadyClassAdapter adapter = new LiveReadyClassAdapter(mContext, list);
                    adapter.setOnItemClickListener(LiveChooseClassActivity.this);
                    mRecyclerView.setAdapter(adapter);
                }
            }
        });
    }


    @Override
    public void onItemClick(LiveClassBean bean, int position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.CLASS_ID, bean.getId());
        intent.putExtra(Constants.CLASS_NAME, bean.getName());
        setResult(RESULT_OK, intent);
        finish();
    }
}
