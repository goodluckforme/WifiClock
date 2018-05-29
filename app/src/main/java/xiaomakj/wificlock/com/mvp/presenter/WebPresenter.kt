package xiaomakj.wificlock.com.mvp.presenter


import android.content.Context
import javax.inject.Inject
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.databinding.ActivityWebBinding
import xiaomakj.wificlock.com.mvp.contract.WebContract

class WebPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<WebContract.View, ActivityWebBinding>(), WebContract.Presenter {

    fun toInit() {
        mContentView.setWebModel(WebModel())
    }

    inner class WebModel

}
