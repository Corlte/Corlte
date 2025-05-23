package com.lzyp.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzyp.common.adapter.RefreshAdapter;
import com.lzyp.common.glide.ImgLoader;
import com.lzyp.live.bean.LiveBean;
import com.lzyp.main.R;
import com.lzyp.main.utils.MainIconUtil;

/**
 * Created by cxf on 2018/9/26.
 * 首页 直播
 */

public class MainHomeLiveAdapter extends RefreshAdapter<LiveBean> {

    private static final int HEAD = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;

    private View.OnClickListener mOnClickListener;
    private View mHeadView;

    public MainHomeLiveAdapter(Context context) {
        super(context);
        mHeadView = mInflater.inflate(R.layout.item_main_home_live_head, null, false);
        mHeadView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                int position = (int) v.getTag();
                mOnItemClickListener.onItemClick(mList.get(position), position);
            }
        };
    }


    public View getHeadView() {
        return mHeadView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        } else if (position % 2 == 0) {
            return RIGHT;
        }
        return LEFT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            ViewParent viewParent = mHeadView.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(mHeadView);
            }
            HeadVh headVh = new HeadVh(mHeadView);
            headVh.setIsRecyclable(false);
            return headVh;
        } else if (viewType == LEFT) {
            return new Vh(mInflater.inflate(R.layout.item_main_home_live_left, parent, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_main_home_live_right, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position - 1), position - 1);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    class HeadVh extends RecyclerView.ViewHolder {

        public HeadVh(View itemView) {
            super(itemView);
        }
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mCover;
        ImageView mAvatar;
        TextView mName;
        TextView mTitle;
        TextView mNum;
        ImageView mType;
        ImageView mImgGoodsIcon;

        public Vh(View itemView) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mNum = (TextView) itemView.findViewById(R.id.num);
            mType = (ImageView) itemView.findViewById(R.id.type);
            mImgGoodsIcon = (ImageView) itemView.findViewById(R.id.img_goods_icon);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveBean bean, int position) {
            itemView.setTag(position);
            ImgLoader.display(mContext, bean.getThumb(), mCover);
            ImgLoader.display(mContext, bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            if (TextUtils.isEmpty(bean.getTitle())) {
                if (mTitle.getVisibility() == View.VISIBLE) {
                    mTitle.setVisibility(View.GONE);
                }
            } else {
                if (mTitle.getVisibility() != View.VISIBLE) {
                    mTitle.setVisibility(View.VISIBLE);
                }
                mTitle.setText(bean.getTitle());
            }

            if (mImgGoodsIcon != null) {
                if (bean.getIsshop() == 1) {
                    if (mImgGoodsIcon.getVisibility() != View.VISIBLE) {
                        mImgGoodsIcon.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mImgGoodsIcon.getVisibility() == View.VISIBLE) {
                        mImgGoodsIcon.setVisibility(View.INVISIBLE);
                    }
                }
            }
            mNum.setText(bean.getNums());
            mType.setImageResource(MainIconUtil.getLiveTypeIcon(bean.getType()));
        }
    }

}
