package xiaomakj.wificlock.com.mvp.presenter


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiScan.ScanResultsListener
import com.thanosfisherman.wifiutils.wifiState.WifiStateListener
import com.thanosfisherman.wifiutils.wifiWps.ConnectionWpsListener
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.customView
import org.jetbrains.anko.sdk25.coroutines.onClick
import xiaomakj.wificlock.com.R
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.databinding.ActivityMainBinding
import xiaomakj.wificlock.com.mvp.contract.MainContract
import xiaomakj.wificlock.com.mvp.ui.activity.MainActivity
import xiaomakj.wificlock.com.mvp.ui.activity.WifiDetailActivity
import xiaomakj.wificlock.com.utils.launchActivity
import javax.inject.Inject

class MainPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<MainContract.View, ActivityMainBinding>(), MainContract.Presenter {

    lateinit var baseReclyerViewAdapter: CommonRecycleViewAdapter<ScanResult>

    override fun getPermission() {
        WifiUtils.enableLog(true)
        val mainActivity = mView as MainActivity
        mContentView.OpenWifi.onClick {
            WifiUtils.withContext(mainActivity).enableWifi(object : WifiStateListener {
                override fun isSuccess(isSuccess: Boolean) {
                    mainActivity.toast("Wifi连接上了吗？===$isSuccess")
                }
            })
        }
        mContentView.CloseWifi.onClick {
            WifiUtils.withContext(mainActivity).disableWifi()
        }
        mContentView.getWiftList.onClick {
            mainActivity.dailog.apply {
                setMessage(mainActivity.getString(R.string.loading))
                show()
            }
            WifiUtils.withContext(mainActivity).scanWifi(object : ScanResultsListener {
                override fun onScanResults(scanResults: MutableList<ScanResult>) {
                    //Log.i("onScanResults", scanResults.toString())
                    baseReclyerViewAdapter.replaceAll(scanResults)
                    mainActivity.dailog.dismiss()
                }
            }).start()
        }
        mContentView.ConnectWithWpa.onClick {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                baseReclyerViewAdapter.mDatas.forEachIndexed { index, scanResult ->
                    if (!scanResult.SSID.isEmpty() && scanResult.SSID.contains("chuyukeji")) {
                        WifiUtils.withContext(mainActivity).
                                connectWith(scanResult.SSID, "chuyukeji302")
                                .onConnectionResult { isSuccess -> mainActivity.toast("连接${scanResult.SSID}${if (isSuccess) "成功" else "失败"}") }
                                .start()
                        return@onClick
                    }
                }
            }
        }
    }

    fun toInit() {
        val mainActivity = mView as MainActivity
        mContentView.mainModel = MainModel()
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 555)
            mainActivity.toast("请赋予权限")
        } else getPermission()

        mContentView.layoutManager = LinearLayoutManager(context)
        baseReclyerViewAdapter = object : CommonRecycleViewAdapter<ScanResult>(context, R.layout.wifi_item) {
            override fun convert(helper: ViewHolderHelper?, t: ScanResult?, position: Int) {
                val wifiName = t?.SSID ?: "未知"
                val BSSID = t?.BSSID ?: ""
                helper?.getView<TextView>(R.id.wifi_name)?.text = wifiName
                helper?.convertView?.onClick {
                    mainActivity.
                            alert("BSSID:$BSSID", wifiName) {
                                positiveButton("详情") { mainActivity.launchActivity<WifiDetailActivity> { } }
                                negativeButton("连接该WIFI") {
                                    mainActivity.dailog.show()
                                    toConnectWithWpa(BSSID)
                                }
                            }.show()
                }
            }
        }
        mContentView.adapter = baseReclyerViewAdapter
    }


    private fun toConnectWithWpa(bssid: String) {
        val mainActivity = mView as MainActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mainActivity.alert("${bssid}:请输入PIN码") {
                customView {
                    verticalLayout {
                        val pin = editText {
                            hint = "请输入PIN码"
                        }
                        positiveButton("连接该WIFI") {
                            WifiUtils.withContext(mainActivity)
                                    .connectWithWps(bssid, pin.text.toString())
                                    .setWpsTimeout(3000)
                                    .onConnectionWpsResult { isSuccess ->
                                        mainActivity.dailog.dismiss()
                                        mainActivity.toast("连接${pin.text.toString()}${if (isSuccess) "成功" else "失败"}")
                                    }
                                    .start()
                        }
                    }
                }

            }.show()
        }
    }


    inner class MainModel

}
