package com.lzyp.beauty.views;

import android.content.Context;
import android.view.ViewGroup;

import com.lzyp.beauty.R;
import com.lzyp.beauty.interfaces.OnTieZhiActionClickListener;
import com.lzyp.beauty.interfaces.OnTieZhiActionDownloadListener;
import com.lzyp.beauty.interfaces.OnTieZhiActionListener;
import com.lzyp.common.views.AbsCommonViewHolder;

public abstract class MhTeXiaoChildViewHolder extends AbsCommonViewHolder {

    protected OnTieZhiActionClickListener mOnTieZhiActionClickListener;
    protected OnTieZhiActionListener mOnTieZhiActionListener;
    protected OnTieZhiActionDownloadListener mOnTieZhiActionDownloadListener;


    public MhTeXiaoChildViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_meiyan_child;
    }


    public void setOnTieZhiActionClickListener(OnTieZhiActionClickListener onTieZhiActionClickListener){
        mOnTieZhiActionClickListener = onTieZhiActionClickListener;
    }

    public void setOnTieZhiActionListener(OnTieZhiActionListener onTieZhiActionListener){
         mOnTieZhiActionListener = onTieZhiActionListener;
    }

    public void setOnTieZhiActionDownloadListener(OnTieZhiActionDownloadListener onTieZhiActionDownloadListener){
        mOnTieZhiActionDownloadListener = onTieZhiActionDownloadListener;
    }

}
