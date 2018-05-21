package xiaomakj.wificlock.com.mvp.presenter


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.view.animation.AccelerateInterpolator
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import org.jetbrains.anko.toast
import xiaomakj.wificlock.com.App
import xiaomakj.wificlock.com.R
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.databinding.ActivityChooseWorkPointBinding
import xiaomakj.wificlock.com.mvp.contract.ChooseWorkPointContract
import xiaomakj.wificlock.com.mvp.ui.activity.ChooseWorkPointActivity
import xiaomakj.wificlock.com.utils.SharedPreferencesUtil
import javax.inject.Inject


class ChooseWorkPointPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<ChooseWorkPointContract.View, ActivityChooseWorkPointBinding>(), ChooseWorkPointContract.Presenter, GeocodeSearch.OnGeocodeSearchListener {
    //得到逆地理编码异步查询结果
    override fun onRegeocodeSearched(regeocodeResult: RegeocodeResult, i: Int) {
        val chooseWorkPointActivity = mView as ChooseWorkPointActivity
        val regeocodeAddress = regeocodeResult.regeocodeAddress
        val formatAddress = regeocodeAddress.formatAddress
        chooseWorkPointActivity.toast(formatAddress)
        chooseWorkPointActivity.setResult(Activity.RESULT_OK, Intent().apply {
            putExtra("formatAddress", formatAddress)
            putExtra("coordinate", "${latLonPoint.latitude},${latLonPoint.longitude}")
        })
        chooseWorkPointActivity.finish()
    }


    override fun onGeocodeSearched(geocodeResult: GeocodeResult?, p1: Int) {

    }

    override fun detachView() {
        mContentView.mWaveView.stop()
        App.instance.mainHandler.removeCallbacksAndMessages(null)
        super.detachView()
    }

    lateinit var aMap: AMap

    fun toInit() {
        val chooseWorkPointActivity = mView as ChooseWorkPointActivity
        mContentView.chooseWorkPointModel = ChooseWorkPointModel()
        aMap = mContentView.mMapView.map
        aMap.isMyLocationEnabled = true// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        val myLocationStyle = MyLocationStyle()//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW)
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.cat))
        myLocationStyle.strokeColor(Color.parseColor("#FFFFFF"))//设置定位蓝点精度圆圈的边框颜色的方法。
        myLocationStyle.radiusFillColor(Color.parseColor("#AAF5F5F5"))//设置定位蓝点精度圆圈的填充颜色的方法。
        aMap.setMyLocationStyle(myLocationStyle)
        val latLngS = SharedPreferencesUtil.instance?.getString("coordinate", "") ?: ""
        mContentView.mWaveView.setDuration(5000)
        mContentView.mWaveView.setStyle(Paint.Style.STROKE)
        mContentView.mWaveView.setSpeed(500)
        mContentView.mWaveView.setColor(Color.parseColor("#AAFF7F27"))
        mContentView.mWaveView.setInterpolator(AccelerateInterpolator(1.2f))
        mContentView.mWaveView.start()
        if (latLngS.contains(",")) {
            val split = latLngS.split(",")
            val latLng = LatLng(split[0].toDouble(), split[1].toDouble())
            aMap.addMarker(MarkerOptions().position(latLng).anchor(0.5f,0.5f).title("上班地点").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round))).showInfoWindow()
            aMap.addCircle(CircleOptions().center(latLng).radius(100.0).fillColor(Color.parseColor("#55FF7F27")).strokeColor(Color.parseColor("#FFFFFF")).strokeWidth(1f))
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(
                    latLng, latLng), 16))

        }

        aMap.setOnMapLongClickListener(AMap.OnMapLongClickListener { latLng ->
            getAddressByLatlng(latLng ?: return@OnMapLongClickListener)
        })
        //地理搜索类
        geocodeSearch = GeocodeSearch(chooseWorkPointActivity)
        geocodeSearch.setOnGeocodeSearchListener(this@ChooseWorkPointPresenter)
    }


    lateinit var latLonPoint: LatLonPoint
    lateinit var geocodeSearch: GeocodeSearch

    private fun getAddressByLatlng(latLng: LatLng) {
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        latLonPoint = LatLonPoint(latLng.latitude, latLng.longitude)
        val query = RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP)
        //异步查询
        geocodeSearch.getFromLocationAsyn(query)
    }

    inner class ChooseWorkPointModel

}
