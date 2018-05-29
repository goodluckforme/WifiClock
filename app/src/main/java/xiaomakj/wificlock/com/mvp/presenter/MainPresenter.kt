package xiaomakj.wificlock.com.mvp.presenter

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.WIFI_SERVICE
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiState.WifiStateListener
import kotlinx.android.synthetic.main.header.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import xiaomakj.wificlock.com.App
import xiaomakj.wificlock.com.R
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.api.BaseObserver
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.data.ClockRecordDatas
import xiaomakj.wificlock.com.data.LoginDatas
import xiaomakj.wificlock.com.data.WifiParams
import xiaomakj.wificlock.com.databinding.ActivityMainBinding
import xiaomakj.wificlock.com.mvp.contract.MainContract
import xiaomakj.wificlock.com.mvp.ui.activity.*
import xiaomakj.wificlock.com.services.ColockSevice
import xiaomakj.wificlock.com.utils.LocationUtils
import xiaomakj.wificlock.com.utils.SharedPreferencesUtil
import xiaomakj.wificlock.com.utils.Utils
import xiaomakj.wificlock.com.utils.launchActivity
import javax.inject.Inject
import kotlin.experimental.and


class MainPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<MainContract.View, ActivityMainBinding>(), MainContract.Presenter {
    lateinit var baseReclyerViewAdapter: CommonRecycleViewAdapter<ScanResult>
    lateinit var mClockBinder: ColockSevice.ClockBinder
    var choose_place_br: BroadcastReceiver? = null
    lateinit var loginDatas: LoginDatas.Userinfo

    override fun getPermission() {
        val mainActivity = mView as MainActivity
        loginDatas = SharedPreferencesUtil.instance?.getObject("USERINFO", LoginDatas.Userinfo::class.java) ?: return
        //mainActivity.toast("欢迎：${loginDatas.nickname}")
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
        mContentView.mainHead.head_right.onClick {
            showSellerPop()
        }
        mContentView.mainHead.head_right.visibility = View.VISIBLE
        val intent = Intent(mainActivity, ColockSevice::class.java)
        mainActivity.startService(intent)
        mainActivity.bindService(intent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mClockBinder = service as ColockSevice.ClockBinder
                val isCheck = SharedPreferencesUtil.instance?.getBoolean("AutoClock", false) ?: false
                if (isCheck) mClockBinder?.startForeground()

            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }

        }, Context.BIND_AUTO_CREATE)

        getWifiList(mainActivity)
    }

    var mPopupWindow: PopupWindow? = null

    private fun showSellerPop() {
        val mainActivity = mView as MainActivity
        if (mPopupWindow == null)
            mPopupWindow = PopupWindow(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                val inflate = View.inflate(mainActivity, R.layout.layout_popwin_menu_list, null)
                setBackgroundDrawable(ColorDrawable())
                contentView = inflate
                val recyclerView = inflate.find<RecyclerView>(R.id.popwin_list_view)
                recyclerView?.layoutManager = LinearLayoutManager(mainActivity)
                recyclerView.hasFixedSize()
                val sellerAdapter = object : CommonRecycleViewAdapter<String>(mainActivity, R.layout.item_popwin_list, arrayListOf(
                        "获取WIFI列表",
                        "WIFI状态监听",
                        "打开WIFI",
                        "打开GPS",
                        "关闭WIFI",
                        "连接WIFI",
                        "WIFI的信息",
                        "打卡上班",
                        "选择上班地点",
                        "查询打卡记录",
                        "个人中心"
                )) {
                    @SuppressLint("WifiManagerLeak")
                    override fun convert(helper: ViewHolderHelper?, good: String, position: Int) {
                        val textView = helper?.getView<TextView>(R.id.item_tv)
                        textView?.text = good
                        textView?.onClick {
                            when (position) {
                                0 -> {
                                    getWifiList(mainActivity)
                                }
                                1 -> {
                                    mClockBinder.starWIFIStatetListenter()
                                }
                                2 -> {
                                    WifiUtils.withContext(mainActivity).enableWifi(object : WifiStateListener {
                                        override fun isSuccess(isSuccess: Boolean) {
                                            mainActivity.toast("Wifi连接上了吗？===$isSuccess")
                                        }
                                    })
                                }
                                3 -> {
                                    toEnableGPS()
                                }
                                4 -> {
                                    WifiUtils.withContext(mainActivity).disableWifi()
                                }
                                5 -> {
                                    val mSSID = SharedPreferencesUtil.instance?.getString("WORK_SSID") ?: ""
                                    val mBSSID = SharedPreferencesUtil.instance?.getString("WORK_BSSID") ?: ""
                                    val psw = SharedPreferencesUtil.instance?.getString(mSSID) ?: ""
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        mainActivity.dailog.show()
                                        WifiUtils.withContext(mainActivity).
                                                connectWith(mSSID, psw)
                                                .onConnectionResult { isSuccess ->
                                                    mainActivity.dailog.dismiss()
                                                    mainActivity.toast("连接${mSSID}${if (isSuccess) "成功" else "失败"} 密码 : $psw")
                                                }
                                                .start()
                                    }
                                }
                                6 -> {
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
                                    mainActivity.alert("$wifiProperty", wifiInfo.ssid) {
                                        positiveButton("确定") {}
                                    }.show()
                                }
                                7 -> {
                                    val coordinate = SharedPreferencesUtil.instance?.getString("coordinate") ?: ""
                                    if (!coordinate.contains(",")) return@onClick
                                    val currentAmapLocation = App.instance.amapLocation ?: return@onClick
                                    mainActivity.dailog.show()
                                    val split = coordinate.split(",")
                                    val wifiManager = mainActivity.getSystemService(WIFI_SERVICE) as WifiManager
                                    val disByRssi = DisByRssi(wifiManager.connectionInfo.rssi)
                                    val distanceOfTwoPoints = Utils.DistanceOfTwoPoints(currentAmapLocation.latitude, currentAmapLocation.longitude, split[0].toDouble(), split[1].toDouble()) * 1000
                                    appApi.addClockRecord(
                                            WifiParams(loginDatas.user_id.toString(),
                                                    App.instance.amapLocation?.address ?: "",
                                                    App.instance.amapLocation?.latitude ?: 0.0,
                                                    App.instance.amapLocation?.longitude ?: 0.0,
                                                    wifiManager.connectionInfo.ssid,
                                                    disByRssi.toInt(),
                                                    distanceOfTwoPoints.toInt()),
                                            object : BaseObserver<Any>(mainActivity) {
                                                override fun onRequestFail(e: Throwable) {
                                                    mainActivity.toast(e.message.toString())
                                                    mainActivity.dailog.dismiss()
                                                }

                                                override fun onNetSuccess(result: Any) {
                                                    mainActivity.dailog.dismiss()
                                                    mainActivity.alert {
                                                        message = "打卡成功"
                                                        positiveButton("确定") {

                                                        }
                                                    }.show()
                                                }
                                            })
                                }
                                8 -> {
                                    mainActivity.launchActivity<ChooseWorkPointActivity>(1102)
                                }
                                9 -> {
                                    mainActivity.launchActivity<ClockRecordActivity>()

                                }
                                10 -> {
                                    mainActivity.launchActivity<UserCenterActivity>()
                                }
                            }
                        }
                    }
                }
                recyclerView?.adapter = sellerAdapter
                isFocusable = true
                isOutsideTouchable = true
            }
        mPopupWindow?.showAsDropDown(mContentView.mainHead.head_right, 0, 0)
    }

    private fun getWifiList(mainActivity: MainActivity) {
        mainActivity.dailog.apply {
            setMessage(mainActivity.getString(R.string.loading))
            show()
        }
        WifiUtils.withContext(mainActivity).scanWifi { scanResults ->
            baseReclyerViewAdapter.replaceAll(scanResults)
            mainActivity.dailog.dismiss()
        }.start()
    }


    private fun toEnableGPS() {
        val mainActivity = mView as MainActivity
        if (!LocationUtils.isGpsEnabled()) {
            mainActivity.alert("API22以上的手机只有开启GPS后才有能力获取WIFI信息") {
                positiveButton("开启") {
                    LocationUtils.openGpsSettings()
                }
                negativeButton("拒绝") {}
            }.show()
        } else mainActivity.toast("GPS已经开启")

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
                val wifiName = t?.SSID ?: ""
                val BSSID = t?.BSSID ?: ""
                helper?.getView<TextView>(R.id.wifi_name)?.text = if (wifiName.isEmpty()) {
                    "未知"
                } else wifiName
                helper?.convertView?.onLongClick {
                    val mSSID = SharedPreferencesUtil.instance?.getString("WORK_SSID")
                    val mBSSID = SharedPreferencesUtil.instance?.getString("WORK_BSSID")
                    if (wifiName == mSSID && BSSID == mBSSID) {
                        mainActivity.
                                alert("是否删除常用打卡点:${wifiName}", wifiName) {
                                    positiveButton("是") {
                                        SharedPreferencesUtil.instance?.putString("WORK_SSID", "")
                                        SharedPreferencesUtil.instance?.putString("WORK_BSSID", "")
                                    }
                                    negativeButton("否") {

                                    }
                                }.show()
                    } else {
                        val psw = SharedPreferencesUtil.instance?.getString(wifiName) ?: ""
                        mainActivity.
                                alert("是否设置:${wifiName}为常用打卡点", wifiName) {
                                    customView {
                                        val pswed = editText {
                                            hint = "请输入psw码"
                                            setText(psw)
                                        }
                                        positiveButton("是") {
                                            val trim = pswed.text.toString().trim()
                                            if (!trim.isEmpty()) {
                                                SharedPreferencesUtil.instance?.putString("WORK_SSID", wifiName)
                                                SharedPreferencesUtil.instance?.putString("WORK_BSSID", BSSID)
                                                SharedPreferencesUtil.instance?.putString(wifiName, trim)
                                            }
                                        }
                                        negativeButton("否") {

                                        }
                                    }
                                }.show()
                    }
                }
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
