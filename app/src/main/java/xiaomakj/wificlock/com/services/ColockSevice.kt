package xiaomakj.wificlock.com.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationProvider
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiState.WifiStateCallback
import com.thanosfisherman.wifiutils.wifiState.WifiStateReceiver
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import xiaomakj.wificlock.com.App
import xiaomakj.wificlock.com.R
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.api.BaseObserver
import xiaomakj.wificlock.com.data.TestDatas
import xiaomakj.wificlock.com.mvp.ui.activity.ChooseWorkPointActivity
import xiaomakj.wificlock.com.utils.LocationUtils
import xiaomakj.wificlock.com.utils.SharedPreferencesUtil
import xiaomakj.wificlock.com.utils.Utils
import xiaomakj.wificlock.com.utils.launchActivity
import java.util.concurrent.TimeUnit


/**
 * Created by Administrator on 2018/5/18.
 */
class ColockSevice : Service() {
    /**
     * 先分析一下 自动打卡的条件
     * 1.WIFI连上后
     * 2.WIFI距离路由器距离到达临界值
     * 3.GPS定位到当前位置
     * 4.手动打开自动打卡APP（必须条件 长期运行可加入白名单）
     * 5.后台长期运行的服务 能够通过WIFI打开的广播 扫描WIFI环境并选择连接上指定WIFI.(目前只通过SSID判断即可)
     * 6.根据前面的条件 寻找最佳时机  请求PHP服务器 打卡
     *
     *
     * 思考：手机打开WIFI开关后,是否存在连接上指定WIFI立即发送广播的可能,自己实现是否可行?
     * 1 自己实现的方法：当距离当前路由器小于100米的时候 开启后台长期运行的服务可以轮训 遍历当前WIFI列表 直到连接上指定WIFI 查寻当前连接WIFI距离条件打卡
     *  问题1  如何确定路由器的唯一性(MAC 去掉MAC的后四位即可) 必须
     *  问题2  如何确定WIFI的唯一性(SSID?)  非必须因为一个路由器能发出几个WIFI
     * */
    override fun onBind(intent: Intent?): IBinder {
        return mClockBinder
    }

    val wifiStateReceiver by lazy {
        WifiStateReceiver(object : WifiStateCallback {
            override fun onWifiEnabled() {
                //此处开启While无限循环
                toast("WiFi 已经打开")
                intervalSb?.unsubscribe()
                readyToWifiColock()
            }

            override fun onWifiDisabled() {
                toast("WiFi 已经关闭")
                intervalSb?.unsubscribe()
            }
        })
    }
    var intervalSb: Subscription? = null

    private fun readyToWifiColock() {
        intervalSb = Observable.interval(5000, 10000, TimeUnit.MILLISECONDS)
                //延时3000 ，每间隔3000，时间单位
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val coordinate = SharedPreferencesUtil.instance?.getString("coordinate") ?: ""
                    if (coordinate.contains(",")) {
                        val split = coordinate.split(",")
                        val currentAmapLocation = App.instance.amapLocation ?: return@subscribe
                        val distanceOfTwoPoints = Utils.DistanceOfTwoPoints(currentAmapLocation.latitude, currentAmapLocation.longitude, split[0].toDouble(), split[1].toDouble()) * 1000
                        if (distanceOfTwoPoints < 100) {
                            toast("距离打卡地点小于100米尝试连接公司WIFI")
                            //扫描并尝试连接Chuyukeji5.0
                            WifiUtils.withContext(applicationContext)
                                    .connectWithScanResult("chyukeji302") { scanResults ->
                                        scanResults.firstOrNull { "chuyukeji2.4" == it.SSID }
                                    }
                                    .onConnectionResult { isSuccess ->
                                        if (isSuccess) {
                                            val wifiDistance = getWIFIDistance()
                                            toast("测量WIFI和手机的距离为" + wifiDistance)
                                            if (wifiDistance < 20) {
                                                toast("WIFI和手机的距离是否小于20m,尝试自动打卡")
                                                AppApi.instance.getTest(object : BaseObserver<List<TestDatas>>(this@ColockSevice) {
                                                    override fun onRequestFail(e: Throwable) {
//                                                        this@ColockSevice.toast(e.message.toString())
                                                        toast("服务器无响应,请联系马齐383930056@qq.com")
                                                    }

                                                    override fun onNetSuccess(result: List<TestDatas>) {
                                                        val message = result[0].post_owner + "打卡成功"
                                                        this@ColockSevice.toast(message)
                                                        intervalSb?.unsubscribe()
                                                    }
                                                })
                                            }
                                        } else {
                                            toast("连接公司WIFI失败 请尝试手动打卡")
                                        }
                                    }.start()
                        } else {
                            toast("距离打卡地点${distanceOfTwoPoints}米")
                        }
                    } else {
                        toast("请选择您的上班地点")
                        sendBroadcast(Intent("COLOCKSEVICE_NEED_WORK_PLACE"))
                    }
                }
    }

    @SuppressLint("WifiManagerLeak")
    private fun getWIFIDistance(): Double {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val dhcpInfo = wifiManager.getDhcpInfo()
        val wifiInfo = wifiManager.getConnectionInfo()
        val list = wifiManager.getScanResults() as List<android.net.wifi.ScanResult>
        return DisByRssi(wifiInfo.getRssi()) ?: 9999.0
    }

    fun DisByRssi(rssi: Int): Double? {
        val iRssi = Math.abs(rssi)
        val power = (iRssi - 35) / (10 * 2.1)
        return Math.pow(10.0, power)
    }

    var isPlay = false
    var lastLocation: Location? = null
    val mClockBinder by lazy { ClockBinder() }

    interface ColockOnLocationChangeListener {
        fun enableGps()
        fun disableGps()
    }

    lateinit var mColockOnLocationChangeListener: ColockOnLocationChangeListener

    inner class ClockBinder : Binder() {
        fun startForeground(listener: ColockOnLocationChangeListener) {
            play()
            mClockBinder.starWIFIStatetListenter()
            mClockBinder.startLocationListener(listener)
        }

        fun stopForeground() = stop()
        @SuppressLint("MissingPermission")
        fun startLocationListener(listener: ColockOnLocationChangeListener) {
            this@ColockSevice.mColockOnLocationChangeListener = listener
            LocationUtils.register(0, 1, object : LocationUtils.OnLocationChangeListener {
                override fun onLocationChanged(location: Location?) {
                    if (lastLocation != null)
                        Log.i("DistanceTo", location?.distanceTo(lastLocation).toString())
                    lastLocation = location
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    when (status) {
                        LocationProvider.AVAILABLE -> {
                            Log.d("LocationUtils", "当前GPS状态为可见状态")
                            mColockOnLocationChangeListener.enableGps()
                        }
                        LocationProvider.OUT_OF_SERVICE -> {
//                            mColockOnLocationChangeListener.disableGps()
                            toast("当前GPS状态为服务区外状态")
                            Log.d("LocationUtils", "当前GPS状态为服务区外状态")
                        }
                        LocationProvider.TEMPORARILY_UNAVAILABLE -> {
                            mColockOnLocationChangeListener.disableGps()
                            Log.d("LocationUtils", "当前GPS状态为暂停服务状态")
                        }
                    }
                }

                override fun getLastKnownLocation(location: Location?) {
                    Log.i("LocationUtils", "getLastKnownLocation=========${location?.toString()}")
                }
            })
        }

        fun starWIFIStatetListenter() {
            registerReceiver(wifiStateReceiver, IntentFilter().apply { addAction(WifiManager.WIFI_STATE_CHANGED_ACTION) })
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        stop()
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    private fun stop() {
        if (isPlay) {
            isPlay = false
            //将服务从foreground状态中移走，使得系统可以在低内存的情况下清除它。
            stopForeground(true)
            unregisterReceiver(wifiStateReceiver)
            LocationUtils.unregister()
            intervalSb?.unsubscribe()
        }
    }

    private fun play() {
        if (!isPlay) {
            isPlay = true
            //和上一笔记中创建通知的步骤一样，只是不需要通过通知管理器进行触发，而是用startForeground(ID,notify)来处理
            //步骤1：和上一笔记一样，通过Notification.Builder( )来创建通知
            //FakePlayer就是两个大button的activity，也即服务的界面，见最左图
            val i = Intent(this, this@ColockSevice::class.java)
            //注意Intent的flag设置：FLAG_ACTIVITY_CLEAR_TOP: 如果activity已在当前任务中运行，在它前端的activity都会被关闭，它就成了最前端的activity。FLAG_ACTIVITY_SINGLE_TOP: 如果activity已经在最前端运行，则不需要再加载。设置这两个flag，就是让一个且唯一的一个activity（服务界面）运行在最前端。
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pi = PendingIntent.getActivity(this, 0, i, 0)
            val myNotify = Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("WIFIClock")
                    .setContentText("WIFIClock正在工作中")
                    .setContentIntent(null)
                    .getNotification()
            //设置notification的flag，表明在点击通知后，通知并不会消失，也在最右图上仍在通知栏显示图标。这是确保在activity中退出后，状态栏仍有图标可提下拉、点击，再次进入activity。
            myNotify.flags = myNotify.flags or Notification.FLAG_NO_CLEAR
            // 步骤 2：startForeground( int, Notification)将服务设置为foreground状态，使系统知道该服务是用户关注，低内存情况下不会killed，并提供通知向用户表明处于foreground状态。
            startForeground(968, myNotify)
        }
    }
}