package xiaomakj.wificlock.com.mvp.presenter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.WIFI_SERVICE
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiScan.ScanResultsListener
import com.thanosfisherman.wifiutils.wifiState.WifiStateListener
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick
import xiaomakj.wificlock.com.App
import xiaomakj.wificlock.com.R
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.api.BaseObserver
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.data.TestDatas
import xiaomakj.wificlock.com.data.WifiParams
import xiaomakj.wificlock.com.databinding.ActivityMainBinding
import xiaomakj.wificlock.com.mvp.contract.MainContract
import xiaomakj.wificlock.com.mvp.ui.activity.ChooseWorkPointActivity
import xiaomakj.wificlock.com.mvp.ui.activity.MainActivity
import xiaomakj.wificlock.com.mvp.ui.activity.WifiDetailActivity
import xiaomakj.wificlock.com.services.ColockSevice
import xiaomakj.wificlock.com.utils.LocationUtils
import xiaomakj.wificlock.com.utils.SharedPreferencesUtil
import xiaomakj.wificlock.com.utils.Utils
import xiaomakj.wificlock.com.utils.launchActivity
import javax.inject.Inject
import kotlin.experimental.and


class MainPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<MainContract.View, ActivityMainBinding>(), MainContract.Presenter, ColockSevice.ColockOnLocationChangeListener {
    lateinit var baseReclyerViewAdapter: CommonRecycleViewAdapter<ScanResult>
    lateinit var mClockBinder: ColockSevice.ClockBinder
    var choose_place_br: BroadcastReceiver? = null
    @SuppressLint("WifiManagerLeak")
    override fun getPermission() {
        val mainActivity = mView as MainActivity
        if (choose_place_br == null) {
            choose_place_br = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    mainActivity.launchActivity<ChooseWorkPointActivity>(1102) {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                }
            }
            mainActivity.registerReceiver(choose_place_br, IntentFilter("COLOCKSEVICE_NEED_WORK_PLACE"))
        }
        WifiUtils.enableLog(true)
        mContentView.OpenWifi.onClick {
            WifiUtils.withContext(mainActivity).enableWifi(object : WifiStateListener {
                override fun isSuccess(isSuccess: Boolean) {
                    mainActivity.toast("Wifi连接上了吗？===$isSuccess")
                }
            })
        }
        mContentView.SetWifiListener.onClick {
            mClockBinder.starWIFIStatetListenter()
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
        mContentView.OpenGPS.onClick {
            mClockBinder.startLocationListener(this@MainPresenter)
        }
        mContentView.ConnectDistance.onClick {
            val wifiManager = mainActivity.getSystemService(WIFI_SERVICE) as WifiManager
            val connectManager = mainActivity.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val dhcpInfo = wifiManager.getDhcpInfo()
            val wifiInfo = wifiManager.getConnectionInfo()
            val list = wifiManager.getScanResults() as List<android.net.wifi.ScanResult>
            val wifiProperty = "当前连接WIFI信息如下:" + wifiInfo.getSSID() + '\n' +
                    "ip:" + FormatString(dhcpInfo.ipAddress) + '\n' +
                    "mask:" + FormatString(dhcpInfo.netmask) + '\n' +
                    "netgate:" + FormatString(dhcpInfo.gateway) + '\n' +
                    "dns:" + FormatString(dhcpInfo.dns1) + '\n' +
                    "rssi:" + wifiInfo.getRssi() + '\n' +
                    DisByRssi(wifiInfo.getRssi())
            mContentView.WifiInfoTV.text = wifiProperty
        }
        val intent = Intent(mainActivity, ColockSevice::class.java)
        mainActivity.startService(intent)
        mainActivity.bindService(intent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mClockBinder = service as ColockSevice.ClockBinder
                mContentView.AutoClock.isChecked = SharedPreferencesUtil.instance?.getBoolean("AutoClock", false) == true
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }

        }, Context.BIND_AUTO_CREATE)
        mContentView.AutoClock.onCheckedChange { _, isChecked ->
            mainActivity.toast("isChecked===================$isChecked")
            if (isChecked) {
                mClockBinder.startForeground(this@MainPresenter)
            } else {
                mClockBinder.stopForeground()
            }
            SharedPreferencesUtil.instance?.putBoolean("AutoClock", isChecked)
        }
        mContentView.ClockWork.onClick {
            //                appApi.getTest(object : BaseObserver<List<TestDatas>>(mainActivity) {
//                override fun onRequestFail(e: Throwable) {
//                    mainActivity.toast(e.message.toString())
//                }
//
//                override fun onNetSuccess(result: List<TestDatas>) {
//                    mainActivity.alert {
//                        message = result[0].post_owner + "打卡成功"
//                        positiveButton("确定") {
//
//                        }
//                    }.show()
//                }
//            })
            val coordinate = SharedPreferencesUtil.instance?.getString("coordinate") ?: ""
            if (!coordinate.contains(",")) return@onClick
            val currentAmapLocation = App.instance.amapLocation ?: return@onClick
            val split = coordinate.split(",")
            val wifiManager = mainActivity.getSystemService(WIFI_SERVICE) as WifiManager
            val disByRssi = DisByRssi(wifiManager.connectionInfo.rssi)
            val distanceOfTwoPoints = Utils.DistanceOfTwoPoints(currentAmapLocation.latitude, currentAmapLocation.longitude, split[0].toDouble(), split[1].toDouble()) * 1000
            appApi.addClockRecord(
                    WifiParams("1",
                            App.instance.amapLocation?.street ?: "",
                            App.instance.amapLocation?.latitude ?: 0.0,
                            App.instance.amapLocation?.longitude ?: 0.0,
                            wifiManager.connectionInfo.ssid,
                            disByRssi.toInt(),
                            distanceOfTwoPoints.toInt()),
                    object : BaseObserver<Any>(mainActivity) {
                        override fun onRequestFail(e: Throwable) {
                            mainActivity.toast(e.message.toString())
                        }

                        override fun onNetSuccess(result: Any) {
                            mainActivity.alert {
                                message = "打卡成功"
                                positiveButton("确定") {

                                }
                            }.show()
                        }
                    })
        }
        mContentView.ChooseWorkPoint.onClick {
            mainActivity.launchActivity<ChooseWorkPointActivity>(1102)
        }
    }

    override fun enableGps() {
    }

    override fun disableGps() {
        val mainActivity = mView as MainActivity
        if (!LocationUtils.isGpsEnabled()) {
            mainActivity.alert("API22以上的手机只有开启GPS后才有能力获取WIFI信息") {
                positiveButton("开启") {
                    LocationUtils.openGpsSettings()
                }
                negativeButton("拒绝") {}
            }.show()
        }
    }

    fun FormatString(value: Int): String {
        var strValue = ""
        val ary = intToByteArray(value)
        for (i in ary.size - 1 downTo 1) {
            strValue += ary[i] and 0xFF.toByte()
            if (i > 0) {
                strValue += "."
            }
        }
        return strValue
    }

    fun intToByteArray(n: Int): ByteArray {
        val b = ByteArray(4)
        b[0] = (n and 0xff).toByte()
        b[1] = (n shr 8 and 0xff).toByte()
        b[2] = (n shr 16 and 0xff).toByte()
        b[3] = (n shr 24 and 0xff).toByte()
        return b
    }

    fun DisByRssi(rssi: Int): Double {
        val iRssi = Math.abs(rssi)
        val power = (iRssi - 35) / (10 * 2.1)
        return Math.pow(10.0, power)
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
                                positiveButton("详情") {
                                    mainActivity.launchActivity<WifiDetailActivity> {
                                        putExtra("WIFIINFO", t)
                                    }
                                }
                                negativeButton("连接该WIFI") {
                                    toConnectWithWpa(BSSID, wifiName)
                                }
                            }.show()
                }
            }
        }
        mContentView.adapter = baseReclyerViewAdapter
    }

    private fun toConnectWithWpa(bssid: String, ssid: String) {
        val mainActivity = mView as MainActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mainActivity.alert("${bssid}:请输入PIN码") {
                customView {
                    verticalLayout {
                        val pin = editText {
                            hint = "请输入PIN码"
                            val bssid = SharedPreferencesUtil.instance?.getString(bssid, "")
                            if (bssid?.isNotEmpty() == true) {
                                setText(bssid)
                                setSelection(bssid?.length)
                            }
                        }
                        positiveButton("确定") {
                            if (pin.text.toString().isEmpty()) return@positiveButton
                            mainActivity.dailog.show()
                            WifiUtils.withContext(mainActivity)
                                    .connectWithWps(bssid, pin.text.toString())
                                    .setWpsTimeout(3000)
                                    .onConnectionWpsResult { isSuccess ->
                                        mainActivity.dailog.dismiss()
                                        if (isSuccess)
                                            SharedPreferencesUtil.instance?.putString(bssid, "${pin.text.toString().trim()}")
                                        mainActivity.toast("连接${pin.text.toString().trim()}${if (isSuccess) "成功" else "失败"}")
                                    }
                                    .start()
                        }
                        negativeButton("密码连接该WIFI") {
                            mainActivity.alert("${ssid}:请输入密码") {
                                customView {
                                    verticalLayout {
                                        val psw = editText {
                                            hint = "请输入密码"
                                            val ssid = SharedPreferencesUtil.instance?.getString(ssid, "")
                                            if (ssid?.isNotEmpty() == true) {
                                                setText(ssid)
                                                setSelection(ssid?.length)
                                            }
                                        }
                                        positiveButton("确定") {
                                            if (psw.text.toString().isEmpty()) return@positiveButton
                                            mainActivity.dailog.show()
                                            WifiUtils.withContext(mainActivity)
                                                    .connectWith(ssid, pin.text.toString())
                                                    .setTimeout(3000)
                                                    .onConnectionResult { isSuccess ->
                                                        mainActivity.dailog.dismiss()
                                                        if (isSuccess)
                                                            SharedPreferencesUtil.instance?.putString(ssid, "${psw.text.toString().trim()}")
                                                        mainActivity.toast("连接${psw.text.toString().trim()}${if (isSuccess) "成功" else "失败"}")
                                                    }
                                                    .start()
                                        }
                                    }
                                }

                            }.show()
                        }
                    }
                }

            }.show()
        }
    }

    @SuppressLint("MissingPermission")
    override fun detachView() {
        super.detachView()
        val mainActivity = mView as MainActivity
        mainActivity.unregisterReceiver(choose_place_br ?: return)
        LocationUtils.unregister()
    }

    inner class MainModel

}
