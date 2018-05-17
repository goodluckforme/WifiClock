package xiaomakj.wificlock.com.mvp.presenter


import android.content.Context
import javax.inject.Inject
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.databinding.ActivityWifiDetailBinding
import xiaomakj.wificlock.com.mvp.contract.WifiDetailContract

class WifiDetailPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<WifiDetailContract.View, ActivityWifiDetailBinding>(), WifiDetailContract.Presenter {

    fun toInit() {
        mContentView.wifiDetailModel = WifiDetailModel()
    }

    inner class WifiDetailModel

}
