package xiaomakj.wificlock.com.mvp.presenter


import android.content.Context
import android.net.wifi.ScanResult
import javax.inject.Inject
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.databinding.ActivityWifiDetailBinding
import xiaomakj.wificlock.com.mvp.contract.WifiDetailContract
import xiaomakj.wificlock.com.mvp.ui.activity.WifiDetailActivity

class WifiDetailPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<WifiDetailContract.View, ActivityWifiDetailBinding>(), WifiDetailContract.Presenter {

    fun toInit() {
        mContentView.wifiDetailModel = WifiDetailModel()
        val wifiDetailActivity = mView as WifiDetailActivity
        val wifiInfo = wifiDetailActivity.intent.getParcelableExtra<ScanResult>("WIFIINFO")
        mContentView.WifiDetailTV.text = wifiInfo.toString()
    }

    inner class WifiDetailModel

}
