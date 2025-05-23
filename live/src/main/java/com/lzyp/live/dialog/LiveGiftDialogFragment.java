package com.lzyp.live.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzyp.common.CommonAppConfig;
import com.lzyp.common.Constants;
import com.lzyp.common.adapter.ViewPagerAdapter;
import com.lzyp.common.bean.LiveGiftBean;
import com.lzyp.common.bean.UserBean;
import com.lzyp.common.custom.DrawGiftView;
import com.lzyp.common.dialog.AbsDialogFragment;
import com.lzyp.common.glide.ImgLoader;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.utils.DpUtil;
import com.lzyp.common.utils.ScreenDimenUtil;
import com.lzyp.common.utils.ToastUtil;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.live.R;
import com.lzyp.live.activity.LiveActivity;
import com.lzyp.live.adapter.LiveVoiceGiftAdapter;
import com.lzyp.live.bean.BackPackGiftBean;
import com.lzyp.live.bean.LiveGuardInfo;
import com.lzyp.live.bean.LiveVoiceGiftBean;
import com.lzyp.live.http.LiveHttpConsts;
import com.lzyp.live.http.LiveHttpUtil;
import com.lzyp.live.views.AbsLiveGiftViewHolder;
import com.lzyp.live.views.LiveGiftDaoViewHolder;
import com.lzyp.live.views.LiveGiftGiftViewHolder;
import com.lzyp.live.views.LiveGiftPackageViewHolder;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/12.
 * 送礼物的弹窗
 */

public class LiveGiftDialogFragment extends AbsDialogFragment implements View.OnClickListener, AbsLiveGiftViewHolder.ActionListener {

    private int PAGE_COUNT = 2;
    private AbsLiveGiftViewHolder[] mViewHolders;
    private LiveGiftGiftViewHolder mLiveGiftGiftViewHolder;
    private LiveGiftDaoViewHolder mLiveGiftDaoViewHolder;
    private LiveGiftPackageViewHolder mLiveGiftPackageViewHolder;
    private List<FrameLayout> mViewList;
    private ViewPager mViewPager;
    private View mBtnSendLian;
    private LiveGiftBean mLiveGiftBean;
    private String mCount = "1";
    private String mLiveUid;
    private String mStream;
    private Handler mHandler;
    private int mLianCountDownCount;//连送倒计时的数字
    private TextView mLianText;
    private static final int WHAT_LIAN = 100;
    private boolean mShowLianBtn;//是否显示了连送按钮
    private LiveGuardInfo mLiveGuardInfo;
    private View mBtnGiftTip;
    private TextView mTvGiftTip;
    private String mStringTipGift;
    private String mStringTipDao;

    private View mGroupDrawGiftView;
    private View mTipDrawGiftView;
    private DrawGiftView mDrawGiftView;
    private TextView mDrawGiftCount;
    private String mDrawGiftCountString;
    private SpannableStringBuilder mDrawGiftCountSb;
    private ForegroundColorSpan mDrawGiftCountSpan;
    private LiveVoiceGiftAdapter mLiveVoiceGiftAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_gift;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void setLiveGuardInfo(LiveGuardInfo liveGuardInfo) {
        mLiveGuardInfo = liveGuardInfo;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = ScreenDimenUtil.getInstance().getScreenWidth() / 2 + DpUtil.dp2px(65);
        mViewPager.requestLayout();
        if (!((LiveActivity) mContext).isChatRoom() && CommonAppConfig.getInstance().isMhBeautyEnable()) {
            PAGE_COUNT = 3;
        }
        if (PAGE_COUNT > 1) {
            mViewPager.setOffscreenPageLimit(PAGE_COUNT - 1);
        }
        mViewHolders = new AbsLiveGiftViewHolder[PAGE_COUNT];
        mViewList = new ArrayList<>();
        for (int i = 0; i < PAGE_COUNT; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                loadPageData(position);
                hideLianBtn();
                if (mBtnGiftTip != null && mTvGiftTip != null) {
                    if (CommonAppConfig.getInstance().isMhBeautyEnable()) {
                        if (PAGE_COUNT == 3) {
                            if (position == 2) {
                                if (mBtnGiftTip.getVisibility() == View.VISIBLE) {
                                    mBtnGiftTip.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                if (mBtnGiftTip.getVisibility() != View.VISIBLE) {
                                    mBtnGiftTip.setVisibility(View.VISIBLE);
                                }
                                if (position == 0) {
                                    mTvGiftTip.setText(mStringTipGift);
                                } else if (position == 1) {
                                    mTvGiftTip.setText(mStringTipDao);
                                }
                            }
                        }
                    } else {
                        if (PAGE_COUNT == 2) {
                            if (position == 0) {
                                if (mBtnGiftTip.getVisibility() != View.VISIBLE) {
                                    mBtnGiftTip.setVisibility(View.VISIBLE);
                                }
                            } else if (position == 1) {
                                if (mBtnGiftTip.getVisibility() == View.VISIBLE) {
                                    mBtnGiftTip.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }
                }

                if (mViewHolders != null && mViewHolders.length > 0) {
                    AbsLiveGiftViewHolder vh = mViewHolders[0];
                    if (vh != null) {
                        LiveGiftBean bean = vh.getCurLiveGiftBean();
                        if (bean != null && bean.getType() == LiveGiftBean.TYPE_DRAW) {
                            if (position == 0) {
                                if (mGroupDrawGiftView.getVisibility() != View.VISIBLE) {
                                    mGroupDrawGiftView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (mGroupDrawGiftView.getVisibility() == View.VISIBLE) {
                                    mGroupDrawGiftView.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        MagicIndicator indicator = (MagicIndicator) findViewById(R.id.indicator);
        final String[] titles = PAGE_COUNT == 3 ?
                new String[]{
                        WordUtil.getString(R.string.live_send_gift),
                        WordUtil.getString(R.string.live_send_gift_5),
                        WordUtil.getString(R.string.live_send_gift_4)}
                :
                new String[]{
                        WordUtil.getString(R.string.live_send_gift),
                        WordUtil.getString(R.string.live_send_gift_4)};
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.textColor2));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.white));
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(13);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mViewPager != null) {
                            mViewPager.setCurrentItem(index);
                        }
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setXOffset(DpUtil.dp2px(5));
                linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.white));
                return linePagerIndicator;
            }

        });
        indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(indicator, mViewPager);

        mBtnSendLian = mRootView.findViewById(R.id.btn_send_lian);
        mBtnSendLian.setOnClickListener(this);
        mLianText = (TextView) mRootView.findViewById(R.id.lian_text);
        mBtnGiftTip = mRootView.findViewById(R.id.btn_luck_gift_tip);
        mBtnGiftTip.setOnClickListener(this);
        mTvGiftTip = mRootView.findViewById(R.id.gift_tip);
        mStringTipGift = WordUtil.getString(R.string.live_gift_luck_tip_2);
        mStringTipDao = WordUtil.getString(R.string.live_gift_luck_tip_3);

        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_close_2).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_draw_back).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_draw_delete).setOnClickListener(this);
        mGroupDrawGiftView = findViewById(R.id.group_draw_gift);
        mTipDrawGiftView = findViewById(R.id.tip_draw_gift);
        mDrawGiftCount = findViewById(R.id.draw_gift_count);
        mDrawGiftCountString = WordUtil.getString(R.string.gift_draw_03);
        mDrawGiftCountSb = new SpannableStringBuilder();
        mDrawGiftCountSpan = new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.global));
        mDrawGiftView = findViewById(R.id.draw_gift);
        mDrawGiftView.setActionListener(new DrawGiftView.ActionListener() {
            @Override
            public void onDrawCountChanged(int count) {
                showDrawGiftCount(count);
                if (count > 0) {
                    if (mTipDrawGiftView != null && mTipDrawGiftView.getVisibility() == View.VISIBLE) {
                        mTipDrawGiftView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (mTipDrawGiftView != null && mTipDrawGiftView.getVisibility() != View.VISIBLE) {
                        mTipDrawGiftView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mLianCountDownCount--;
                if (mLianCountDownCount == 0) {
                    hideLianBtn();
                } else {
                    if (mLianText != null) {
                        mLianText.setText(mLianCountDownCount + "s");
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
                        }
                    }
                }
            }
        };
        Bundle bundle = getArguments();
        boolean openPack = false;
        if (bundle != null) {
            mLiveUid = bundle.getString(Constants.LIVE_UID);
            mStream = bundle.getString(Constants.LIVE_STREAM);
            openPack = bundle.getBoolean(Constants.OPEN_PACK, false);
        }
        if (((LiveActivity) mContext).isChatRoom()) {
            RecyclerView recyclerView = findViewById(R.id.voice_recyclerView);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            List<LiveVoiceGiftBean> giftUserList = ((LiveActivity) mContext).getVoiceGiftUserList();
            mLiveVoiceGiftAdapter = new LiveVoiceGiftAdapter(mContext, giftUserList);
            recyclerView.setAdapter(mLiveVoiceGiftAdapter);
        }
        if (openPack) {
            mViewPager.setCurrentItem(2);
        } else {
            loadPageData(0);
        }
    }

    private void showDrawGiftCount(int count) {
        if (mDrawGiftCount != null && mDrawGiftCountSb != null) {
            String countStr = String.valueOf(count);
            String s = String.format(mDrawGiftCountString, countStr);
            mDrawGiftCountSb.clear();
            mDrawGiftCountSb.append(s);
            int index = s.indexOf(countStr);
            mDrawGiftCountSb.setSpan(mDrawGiftCountSpan, index, index + countStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mDrawGiftCount.setText(mDrawGiftCountSb);
        }
    }

    private void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsLiveGiftViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mLiveGiftGiftViewHolder = new LiveGiftGiftViewHolder(mContext, parent, mLiveUid, mStream);
                    mLiveGiftGiftViewHolder.setActionListener(LiveGiftDialogFragment.this);
                    vh = mLiveGiftGiftViewHolder;
                } else if (position == 1) {
                    if (PAGE_COUNT == 3) {
                        mLiveGiftDaoViewHolder = new LiveGiftDaoViewHolder(mContext, parent, mLiveUid, mStream);
                        mLiveGiftDaoViewHolder.setActionListener(LiveGiftDialogFragment.this);
                        vh = mLiveGiftDaoViewHolder;
                    } else {
                        mLiveGiftPackageViewHolder = new LiveGiftPackageViewHolder(mContext, parent, mLiveUid, mStream);
                        mLiveGiftPackageViewHolder.setActionListener(LiveGiftDialogFragment.this);
                        vh = mLiveGiftPackageViewHolder;
                    }
                } else if (position == 2) {
                    mLiveGiftPackageViewHolder = new LiveGiftPackageViewHolder(mContext, parent, mLiveUid, mStream);
                    mLiveGiftPackageViewHolder.setActionListener(LiveGiftDialogFragment.this);
                    vh = mLiveGiftPackageViewHolder;
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
            }
        }

        if (vh != null) {
            vh.loadData();
        }
    }


    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        LiveHttpUtil.cancel(LiveHttpConsts.GET_GIFT_LIST);
        LiveHttpUtil.cancel(LiveHttpConsts.GET_COIN);
        LiveHttpUtil.cancel(LiveHttpConsts.SEND_GIFT);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_send_lian) {
            sendGift();
        } else if (i == R.id.btn_luck_gift_tip) {
            dismiss();
            if (mTvGiftTip != null) {
                String s = mTvGiftTip.getText().toString();
                if (!TextUtils.isEmpty(s)) {
                    if (s.equals(mStringTipGift)) {
                        ((LiveActivity) mContext).openLuckGiftTip();
                    } else {
                        ((LiveActivity) mContext).openDaoGiftTip();
                    }
                }
            }
        } else if (i == R.id.btn_close || i == R.id.btn_close_2) {
            dismiss();

        } else if (i == R.id.btn_draw_back) {
            if (mDrawGiftView != null) {
                mDrawGiftView.drawBack();
            }
        } else if (i == R.id.btn_draw_delete) {
            if (mDrawGiftView != null) {
                mDrawGiftView.clear();
            }
        }
    }

    /**
     * 跳转到我的钻石
     */
    private void forwardMyCoin() {
        dismiss();
//        RouteUtil.forwardMyCoin(mContext);
        ((LiveActivity) mContext).openChargeWindow();
    }


    /**
     * 赠送礼物
     */
    public void sendGift() {
        AbsLiveGiftViewHolder vh = mViewHolders[mViewPager.getCurrentItem()];
        if (vh == null) {
            return;
        }
        mLiveGiftBean = vh.getCurLiveGiftBean();
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream) || mLiveGiftBean == null) {
            return;
        }
        if (mLiveGuardInfo != null) {
            if (mLiveGiftBean.getMark() == LiveGiftBean.MARK_GUARD && mLiveGuardInfo.getMyGuardType() != Constants.GUARD_TYPE_YEAR) {
                ToastUtil.show(R.string.guard_gift_tip);
                return;
            }
        }
        if (mLiveGiftBean.getType() == LiveGiftBean.TYPE_DRAW) {
            if (mDrawGiftView != null) {
                List<PointF> pointList = mDrawGiftView.getPointList();
                if (pointList != null) {
                    if (pointList.size() < 10) {
                        ToastUtil.show(R.string.gift_draw_02);
                        return;
                    }
                    mCount = String.valueOf(pointList.size());
                }
            }
        }
        String toUids = null;
        int userCount = 1;
        if (((LiveActivity) mContext).isChatRoom()) {
            if (mLiveVoiceGiftAdapter != null) {
                Object[] arr = mLiveVoiceGiftAdapter.getCheckedUids();
                toUids = (String) arr[0];
                userCount = (int) arr[1];
            }
        } else {
            toUids = mLiveUid;
            userCount = 1;
        }
        if (TextUtils.isEmpty(toUids)) {
            ToastUtil.show(R.string.a_062);
            return;
        }
        final int finalUserCount = userCount;
        LiveHttpUtil.sendGift(mLiveUid,
                mStream,
                toUids,
                mLiveGiftBean.getId(),
                mCount,
                mLiveGiftBean instanceof BackPackGiftBean ? 1 : 0,
                mLiveGiftBean.isSticker() ? 1 : 0,
                new HttpCallback() {


                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (info.length > 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                String coin = obj.getString("coin");
                                UserBean u = CommonAppConfig.getInstance().getUserBean();
                                if (u != null) {
                                    u.setLevel(obj.getIntValue("level"));
                                    u.setCoin(coin);
                                }
                                if (mLiveGiftGiftViewHolder != null) {
                                    mLiveGiftGiftViewHolder.setCoinString(coin);
                                }
                                if (mLiveGiftDaoViewHolder != null) {
                                    mLiveGiftDaoViewHolder.setCoinString(coin);
                                }
                                ((LiveActivity) mContext).onCoinChanged(coin);
                                if (mContext != null && mLiveGiftBean != null) {

                                    if (mLiveGiftBean.getType() == LiveGiftBean.TYPE_DRAW) {
                                        if (mDrawGiftView != null) {
                                            List<PointF> pointList = mDrawGiftView.getPointList();
                                            ((LiveActivity) mContext).sendGiftMessage(mLiveGiftBean, obj.getString("gifttoken"), JSON.toJSONString(pointList), mDrawGiftView.getWidth(), mDrawGiftView.getHeight());
                                        }
                                        dismiss();
                                    } else {
                                        ((LiveActivity) mContext).sendGiftMessage(mLiveGiftBean, obj.getString("gifttoken"), null, 0, 0);
                                        if (mLiveGiftBean.isSticker()) {
                                            String tip = String.format("发送了%1$s道具礼物", mLiveGiftBean.getName());
                                            String tipEn = String.format("Sent %1$s prop gift", mLiveGiftBean.getNameEn());
                                            ((LiveActivity) mContext).sendChatMessage(tip, tipEn);
                                        }
                                        if (mLiveGiftBean.getType() == LiveGiftBean.TYPE_NORMAL) {
                                            showLianBtn();
                                        }
                                        if (mLiveGiftBean instanceof BackPackGiftBean && mLiveGiftPackageViewHolder != null) {
                                            mLiveGiftPackageViewHolder.reducePackageCount(mLiveGiftBean.getId(), Integer.parseInt(mCount) * finalUserCount);
                                        }
                                    }
                                }

                            }
                        } else {
                            hideLianBtn();
                            ToastUtil.show(msg);
                        }
                    }
                });
    }

    /**
     * 隐藏连送按钮
     */
    private void hideLianBtn() {
        mShowLianBtn = false;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() == View.VISIBLE) {
            mBtnSendLian.setVisibility(View.INVISIBLE);
        }
        if (mViewPager != null) {
            AbsLiveGiftViewHolder vh = mViewHolders[mViewPager.getCurrentItem()];
            if (vh != null) {
                vh.setVisibleSendGroup(true);
            }
        }
    }

    /**
     * 显示连送按钮
     */
    private void showLianBtn() {
        if (mLianText != null) {
            mLianText.setText("5s");
        }
        mLianCountDownCount = 5;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
        }
        if (mShowLianBtn) {
            return;
        }
        mShowLianBtn = true;
        if (mViewPager != null) {
            AbsLiveGiftViewHolder vh = mViewHolders[mViewPager.getCurrentItem()];
            if (vh != null) {
                vh.setVisibleSendGroup(false);
            }
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() != View.VISIBLE) {
            mBtnSendLian.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCountChanged(String count) {
        mCount = count;
    }

    @Override
    public void onGiftChanged(LiveGiftBean bean) {
        hideLianBtn();
        if (bean.getType() == LiveGiftBean.TYPE_DRAW) {
            if (mDrawGiftView != null) {
                mDrawGiftView.clear();
            }
            if (mGroupDrawGiftView.getVisibility() != View.VISIBLE) {
                mGroupDrawGiftView.setVisibility(View.VISIBLE);
            }
            ImgLoader.displayDrawable(mContext, bean.getIcon(), new ImgLoader.DrawableCallback() {
                @Override
                public void onLoadSuccess(Drawable drawable) {
                    if (drawable != null && drawable instanceof BitmapDrawable) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        if (mDrawGiftView != null) {
                            mDrawGiftView.setBitmap(bitmap);
                        }
                    }
                }

                @Override
                public void onLoadFailed() {

                }

            });
        } else {
            if (mDrawGiftView != null) {
                mDrawGiftView.clear();
            }
            if (mGroupDrawGiftView.getVisibility() == View.VISIBLE) {
                mGroupDrawGiftView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onSendClick() {
        sendGift();
    }

    @Override
    public void onCoinClick() {
        forwardMyCoin();
    }


}
