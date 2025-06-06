package com.lzyp.live.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.lzyp.common.Constants
import com.lzyp.common.adapter.RefreshAdapter
import com.lzyp.common.custom.CommonRefreshView
import com.lzyp.common.custom.CommonRefreshView.DataHelper
import com.lzyp.common.dialog.AbsDialogFragment
import com.lzyp.common.http.HttpCallback
import com.lzyp.common.interfaces.OnItemClickListener
import com.lzyp.common.utils.ScreenDimenUtil
import com.lzyp.live.R
import com.lzyp.live.activity.LiveActivity
import com.lzyp.live.adapter.LiveUserDialogAdapter
import com.lzyp.live.bean.LiveUserGiftBean
import com.lzyp.live.http.LiveHttpUtil

/**
 * Created by http://www.yunbaokj.com on 2023/6/29.
 */
class LiveUserListDialog : AbsDialogFragment(), OnItemClickListener<LiveUserGiftBean> {
    override fun getLayoutId() = R.layout.dialog_live_user_list

    override fun getDialogStyle() = R.style.dialog

    override fun canCancel() = true

    override fun setWindowAttributes(window: Window?) {
        window?.apply {
            setWindowAnimations(R.style.bottomToTopAnim)
            attributes.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = (ScreenDimenUtil.getInstance().screenHeight * 0.6f).toInt()
                gravity = Gravity.BOTTOM
            }
        }
    }

    private var mAdapter: LiveUserDialogAdapter? = null
    private var mLiveUid: String? = null
    private var mStream: String? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.apply {
            mLiveUid = getString(Constants.LIVE_UID)
            mStream = getString(Constants.STREAM)
        }
        findViewById<View>(R.id.btn_close).setOnClickListener {
            dismiss()
        }
        findViewById<CommonRefreshView>(R.id.refreshView).apply {
            setLayoutManager(LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false))
            setDataHelper(object : DataHelper<LiveUserGiftBean> {
                override fun getAdapter(): RefreshAdapter<LiveUserGiftBean> {
                    return mAdapter ?: LiveUserDialogAdapter(mContext).also {
                        mAdapter = it
                        it.setOnItemClickListener(this@LiveUserListDialog)
                    }
                }

                override fun loadData(p: Int, callback: HttpCallback) {
                    LiveHttpUtil.getLiveUserRank(mLiveUid, mStream, callback)
                }

                override fun processData(info: Array<out String>): MutableList<LiveUserGiftBean> {
                    return JSON.parseArray(info.contentToString(), LiveUserGiftBean::class.java)
                }

                override fun onRefreshFailure() {
                }

                override fun onLoadMoreFailure() {
                }

                override fun onLoadMoreSuccess(
                    loadItemList: MutableList<LiveUserGiftBean>?,
                    loadItemCount: Int
                ) {
                }

                override fun onRefreshSuccess(
                    list: MutableList<LiveUserGiftBean>?,
                    listCount: Int
                ) {
                }

            })
            initData()
        }
    }

    override fun onItemClick(bean: LiveUserGiftBean?, position: Int) {
        (mContext as? LiveActivity)?.showUserDialog(bean?.id)
    }


}