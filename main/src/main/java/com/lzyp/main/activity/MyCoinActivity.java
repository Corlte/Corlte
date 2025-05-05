package com.lzyp.main.activity;

import android.content.Intent;
import android.net.Uri;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzyp.common.CommonAppConfig;
import com.lzyp.common.Constants;
import com.lzyp.common.HtmlConfig;
import com.lzyp.common.activity.AbsActivity;
import com.lzyp.common.activity.WebViewActivity;
import com.lzyp.common.bean.CoinBean;
import com.lzyp.common.bean.CoinPayBean;
import com.lzyp.common.custom.ItemDecoration;
import com.lzyp.common.event.CoinChangeEvent;
import com.lzyp.common.event.FirstChargeEvent;
import com.lzyp.common.http.CommonHttpConsts;
import com.lzyp.common.http.CommonHttpUtil;
import com.lzyp.common.http.HttpCallback;
import com.lzyp.common.http.HttpClientCallback;
import com.lzyp.common.interfaces.OnItemClickListener;
import com.lzyp.common.pay.PayCallback;
import com.lzyp.common.pay.PayPresenter;
import com.lzyp.common.utils.StringUtil;
import com.lzyp.common.utils.ToastUtil;
import com.lzyp.common.utils.WordUtil;
import com.lzyp.main.R;
import com.lzyp.main.adapter.CoinAdapter;
import com.lzyp.main.adapter.CoinPayAdapter;
import com.lzyp.common.dialog.FirstChargeDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2018/10/23.
 * 充值
 */
public class MyCoinActivity extends AbsActivity implements View.OnClickListener {

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView mPayRecyclerView;
    private CoinAdapter mAdapter;
    private CoinPayAdapter mPayAdapter;
    private TextView mBalance;
    private long mBalanceValue;
    private boolean mFirstLoad = true;
    private PayPresenter mPayPresenter;
    private String mCoinName;
    //    private TextView mTip1;
//    private TextView mTip2;
    private TextView mCoin2;
    private TextView mBtnCharge;
    private boolean mIsPayPal;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_coin;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.wallet));
//        mTip1 = findViewById(R.id.tip_1);
//        mTip2 = findViewById(R.id.tip_2);
        mBtnCharge = findViewById(R.id.btn_charge);
        mBtnCharge.setOnClickListener(this);
        findViewById(R.id.btn_charge_first).setOnClickListener(this);
        findViewById(R.id.btn_charge_detail).setOnClickListener(this);
        mCoin2 = findViewById(R.id.coin_2);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setColorSchemeResources(R.color.blue3);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mBalance = findViewById(R.id.coin);
        TextView coinNameTextView = findViewById(R.id.coin_name);
        coinNameTextView.setText(String.format(WordUtil.getString(R.string.wallet_coin_name), mCoinName));
        TextView scoreName = findViewById(R.id.score_name);
        scoreName.setText(String.format(WordUtil.getString(R.string.wallet_coin_name), CommonAppConfig.getInstance().getScoreName()));
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 10);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mAdapter = new CoinAdapter(mContext, mCoinName);
        mAdapter.setOnItemClickListener(new OnItemClickListener<CoinBean>() {
            @Override
            public void onItemClick(CoinBean bean, int position) {
                if (bean != null && mBtnCharge != null) {
                    String money = StringUtil.contact(mIsPayPal ? "$" : "￥", bean.getMoney());
                    mBtnCharge.setText(String.format(WordUtil.getString(R.string.chat_charge_tip), money));
                }
            }
        });
//        mAdapter.setContactView(findViewById(R.id.top));
        mRecyclerView.setAdapter(mAdapter);
        findViewById(R.id.btn_tip).setOnClickListener(this);
//        View headView = mAdapter.getHeadView();
        mPayRecyclerView = findViewById(R.id.pay_recyclerView);
        ItemDecoration decoration2 = new ItemDecoration(mContext, 0x00000000, 14, 10);
        decoration2.setOnlySetItemOffsetsButNoDraw(true);
        mPayRecyclerView.addItemDecoration(decoration2);
        mPayRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mPayAdapter = new CoinPayAdapter(mContext);
        mPayAdapter.setOnItemClickListener(new OnItemClickListener<CoinPayBean>() {
            @Override
            public void onItemClick(CoinPayBean bean, int position) {
                boolean isPayPal = Constants.PAY_TYPE_PAYPAL.equals(bean.getId());
                if (mIsPayPal != isPayPal) {
                    if (mAdapter != null) {
                        CoinBean coinBean = mAdapter.getCheckedBean();
                        if (coinBean != null && mBtnCharge != null) {
                            String money = StringUtil.contact(isPayPal ? "$" : "￥", coinBean.getMoney());
                            mBtnCharge.setText(String.format(WordUtil.getString(R.string.chat_charge_tip), money));
                        }
                    }
                }
                mIsPayPal = isPayPal;
                if (mAdapter != null) {
                    mAdapter.setIsPaypal(isPayPal);
                }
            }
        });
        mPayRecyclerView.setAdapter(mPayAdapter);
        mPayPresenter = new PayPresenter(this);
        mPayPresenter.setServiceNameAli(Constants.PAY_BUY_COIN_ALI);
        mPayPresenter.setServiceNameWx(Constants.PAY_BUY_COIN_WX);
        mPayPresenter.setServiceNamePaypal(Constants.PAY_BUY_COIN_PAYPAL);
        mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_COIN_URL);
        mPayPresenter.setPayCallback(new PayCallback() {
            @Override
            public void onSuccess() {
                if (mPayPresenter != null) {
                    mPayPresenter.checkPayResult();
                }
            }

            @Override
            public void onFailed() {

            }
        });
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFirstLoad) {
            mFirstLoad = false;
            loadData();
        }else{
            Log.i("DoPay","browser callback");
            if (mPayPresenter != null) {
                mPayPresenter.checkPayResult();
            }
        }
    }

    private void loadData() {
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    mBalanceValue = Long.parseLong(coin);
                    mBalance.setText(coin);
//                    mTip1.setText(obj.getString("tip_t"));
//                    mTip2.setText(obj.getString("tip_d"));
                    mCoin2.setText(obj.getString("score"));
                    List<CoinPayBean> payList = JSON.parseArray(obj.getString("paylist"), CoinPayBean.class);
                    if (mPayAdapter != null) {
                        mPayAdapter.setList(payList);
                    }
                    List<CoinBean> list = JSON.parseArray(obj.getString("rules"), CoinBean.class);
                    if (mAdapter != null) {
                        mAdapter.setList(list);
                        if (payList != null && payList.size() > 0) {
                            mIsPayPal = Constants.PAY_TYPE_PAYPAL.equals(payList.get(0).getId());
                            mAdapter.setIsPaypal(mIsPayPal);
                        }
                        CoinBean coinBean = mAdapter.getCheckedBean();
                        if (coinBean != null && mBtnCharge != null) {
                            String money = StringUtil.contact(mIsPayPal ? "$" : "￥", coinBean.getMoney());
                            mBtnCharge.setText(String.format(WordUtil.getString(R.string.chat_charge_tip), money));
                        }
                    }
                    if (mPayPresenter != null) {
                        mPayPresenter.setBalanceValue(mBalanceValue);
                        mPayPresenter.setAliPartner(obj.getString("aliapp_partner"));
                        mPayPresenter.setAliSellerId(obj.getString("aliapp_seller_id"));
                        mPayPresenter.setAliPrivateKey(obj.getString("aliapp_key_android"));
                        mPayPresenter.setWxAppID(obj.getString("wx_appid"));
                    }
                }
            }

            @Override
            public void onFinish() {
                if (mRefreshLayout != null) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 充值
     */
    private void charge() {
        if (mAdapter == null) {
            return;
        }
        if (mPayPresenter == null) {
            return;
        }
        if (mPayAdapter == null) {
            return;
        }
        CoinBean bean = mAdapter.getCheckedBean();
        if (bean == null) {
            return;
        }
        CoinPayBean coinPayBean = mPayAdapter.getPayCoinPayBean();
        if (coinPayBean == null) {
            ToastUtil.show(R.string.wallet_tip_5);
            return;
        }
        String href = coinPayBean.getHref();
        if (TextUtils.isEmpty(href)) {
            String money = bean.getMoney();
            String coin = Constants.PAY_TYPE_PAYPAL.equals(coinPayBean.getId()) ? bean.getCoinPaypal() : bean.getCoin();
            String goodsName = StringUtil.contact(coin, mCoinName);
//            Map<String, String> orderParams = new HashMap<>();
//            orderParams.put("uid", CommonAppConfig.getInstance().getUid());//用户id
//            orderParams.put("token", CommonAppConfig.getInstance().getToken());//token
//            orderParams.put("money", money);//金额
//            orderParams.put("changeid", bean.getId());//支付方式
//            orderParams.put("coin", coin);//支付方式
//            //TODO: 待完善
//            Log.i("DoPay","orderParams:"+JSON.toJSONString(orderParams)+"\ngoodsName:"+goodsName);
//            Log.i("DoPay","payType:"+coinPayBean.getId());


            Map<String, String> ePayParam = new HashMap<>();
            ePayParam.put("uid",CommonAppConfig.getInstance().getUid());
            ePayParam.put("token",CommonAppConfig.getInstance().getToken());
            ePayParam.put("type",coinPayBean.getId().contains("ali")?"alipay":"wxpay");
            ePayParam.put("name",goodsName);
            ePayParam.put("money",money);
//            ePayParam.put("money","1");
            ePayParam.put("coin", coin);
            ePayParam.put("is_first", "0");

            Log.i("Dopay","ePayParam:"+ePayParam);

            mPayPresenter.ePay(ePayParam, new HttpClientCallback() {


                @Override
                public void onSuccess(int code, String msg) {
                    //            openUrl("https://www.baidu.com");
                    try{
                        Log.i("dopay","epay result code="+code+",msg:"+msg);
                        JSONObject jsonObject = JSONObject.parseObject(msg);
                        if(jsonObject!=null){
                            String payInfo = jsonObject.getString("pay_info");
                            openUrl(payInfo);
                        }
                    }catch (Exception e){
                        ToastUtil.show("error:"+e.getMessage()+",msg:"+msg);
                    }

//                   openUrl(payUrl);
                }
            });
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(href));
            mContext.startActivity(intent);
        }
    }

    private void openUrl(String payInfo) {
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangeEvent(CoinChangeEvent e) {
        if (mBalance != null) {
            mBalance.setText(e.getCoin());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFirstChargeEvent(FirstChargeEvent e) {
        loadData();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_tip) {
            WebViewActivity.forward(mContext, HtmlConfig.CHARGE_PRIVCAY);
        } else if (i == R.id.btn_charge) {
            charge();
        } else if (i == R.id.btn_charge_first) {
            FirstChargeDialogFragment fragment = new FirstChargeDialogFragment();
            fragment.show(getSupportFragmentManager(), "FirstChargeDialogFragment");
        } else if (i == R.id.btn_charge_detail) {
            WebViewActivity.forward(mContext, HtmlConfig.CHARGE_DETAIL);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_BALANCE);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_ALI_ORDER);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_WX_ORDER);
        if (mRefreshLayout != null) {
            mRefreshLayout.setOnRefreshListener(null);
        }
        mRefreshLayout = null;
        if (mPayPresenter != null) {
            mPayPresenter.release();
        }
        mPayPresenter = null;
        super.onDestroy();
    }

}
