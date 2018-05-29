package xiaomakj.wificlock.com.mvp.presenter


import android.annotation.SuppressLint
import android.content.Context
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.databinding.ActivityFeedBackBinding
import xiaomakj.wificlock.com.mvp.contract.FeedBackContract
import xiaomakj.wificlock.com.mvp.ui.activity.FeedBackActivity
import javax.inject.Inject

class FeedBackPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<FeedBackContract.View, ActivityFeedBackBinding>(), FeedBackContract.Presenter {


    fun toInit() {
        mContentView.feedBackModel = FeedBackModel()
        val activity = mView as FeedBackActivity
        val mWebView = mContentView.feedBackWb
    }
    inner class FeedBackModel

}
