package xiaomakj.wificlock.com

import android.app.Application
import xiaomakj.wificlock.com.component.AppComponent
import xiaomakj.wificlock.com.component.DaggerAppComponent
import xiaomakj.wificlock.com.module.AppModule
import xiaomakj.wificlock.com.utils.SharedPreferencesUtil
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.multidex.MultiDex
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by MaQi on 2018/2/1.
 */

class App : Application(), (AMapLocation) -> Unit {

    override fun onCreate() {
        super.onCreate()
        instance = this
        SharedPreferencesUtil.init(this, packageName + "_preference", Context.MODE_PRIVATE)
//        Log.i("sHA1", sHA1(this))
        initGDMap()
    }

    companion object {
        lateinit var instance: App
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    val mainHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    //声明AMapLocationClient类对象
    var mLocationClient: AMapLocationClient? = null
    //声明AMapLocationClientOption对象
    var mLocationOption: AMapLocationClientOption? = null

    private fun initGDMap() {
        //声明定位回调监听器
        val mLocationListener = AMapLocationListener(this)
        //初始化定位
        mLocationClient = AMapLocationClient(applicationContext)
        //设置定位回调监听
        mLocationClient?.setLocationListener(mLocationListener)
        //初始化AMapLocationClientOption对象
        mLocationOption = AMapLocationClientOption()
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //给定位客户端对象设置定位参数
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        //mLocationOption?.interval = 1000 * 60
        mLocationClient?.setLocationOption(mLocationOption)
        //启动定位
        mLocationClient?.startLocation()
    }

    interface LocationListener {
        fun LocationSuccess(amapLocation: AMapLocation)
    }

    lateinit var locationListener: LocationListener
    var amapLocation: AMapLocation? = null
    fun startLocationListener(locationListener: LocationListener) {
        this.locationListener = locationListener
        mLocationClient?.startLocation()
    }

    override fun invoke(amapLocation: AMapLocation) {
        if (amapLocation != null) {
            this.amapLocation = amapLocation
            if (amapLocation.errorCode == 0) {
                locationListener?.LocationSuccess(amapLocation)
                mLocationClient?.stopLocation()
                amapLocation.locationType//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.latitude//获取纬度
                amapLocation.longitude//获取经度
                amapLocation.accuracy//获取精度信息
                amapLocation.address//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.country//国家信息
                amapLocation.province//省信息
                amapLocation.city//城市信息
                amapLocation.district//城区信息
                amapLocation.street//街道信息
                amapLocation.streetNum//街道门牌号信息
                amapLocation.cityCode//城市编码
                amapLocation.adCode//地区编码
                amapLocation.aoiName//获取当前定位点的AOI信息
                amapLocation.buildingId//获取当前室内定位的建筑物Id
                amapLocation.floor//获取当前室内定位的楼层
                amapLocation.gpsAccuracyStatus//获取GPS的当前状态
                //获取定位时间
                val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val date = Date(amapLocation.time)
                df.format(date)
                Log.e("AmapError", "location:"
                        + amapLocation.getCity() + "======="
                        + amapLocation.getDistrict() + "======="
                        + amapLocation.getStreet() + "=======")
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo())
            }
        }
    }

    fun sHA1(context: Context): String? {
        try {
            val info = context.packageManager.getPackageInfo(
                    context.packageName, PackageManager.GET_SIGNATURES)
            val cert = info.signatures[0].toByteArray()
            val md = MessageDigest.getInstance("SHA1")
            val publicKey = md.digest(cert)
            val hexString = StringBuffer()
            for (i in publicKey.indices) {
                val appendString = Integer.toHexString(0xFF and publicKey[i].toInt())
                        .toUpperCase(Locale.US)
                if (appendString.length == 1)
                    hexString.append("0")
                hexString.append(appendString)
                hexString.append(":")
            }
            val result = hexString.toString()
            return result.substring(0, result.length - 1)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return null
    }
}
