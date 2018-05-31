package xiaomakj.wificlock.com.mvp.presenter


import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.widget.TextView
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import com.bumptech.glide.Glide
import org.jetbrains.anko.toast
import xiaomakj.wificlock.com.R
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.api.BaseObserver
import xiaomakj.wificlock.com.common.BASEURL
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.data.ClockRecordDatas
import xiaomakj.wificlock.com.data.LoginDatas
import xiaomakj.wificlock.com.databinding.ActivityClockRecordBinding
import xiaomakj.wificlock.com.mvp.contract.ClockRecordContract
import xiaomakj.wificlock.com.mvp.ui.activity.ClockRecordActivity
import xiaomakj.wificlock.com.utils.GlideCircleTransform
import xiaomakj.wificlock.com.utils.SharedPreferencesUtil
import javax.inject.Inject

class ClockRecordPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<ClockRecordContract.View, ActivityClockRecordBinding>(), ClockRecordContract.Presenter {
    lateinit var baseReclyerViewAdapter: CommonRecycleViewAdapter<ClockRecordDatas.ClockList>
    fun toInit() {
        val loginDatas = SharedPreferencesUtil.instance?.getObject("USERINFO", LoginDatas.Userinfo::class.java) ?: return
        val clockRecordActivity = mView as ClockRecordActivity
        mContentView.clockRecordModel = ClockRecordModel()
        mContentView.layoutManager = LinearLayoutManager(context)
        baseReclyerViewAdapter = object : CommonRecycleViewAdapter<ClockRecordDatas.ClockList>(clockRecordActivity, R.layout.item_clock_list) {
            override fun convert(helper: ViewHolderHelper, data: ClockRecordDatas.ClockList, position: Int) {
                helper.getView<TextView>(R.id.createtime).text = DateFormat.format("yyyy-MM-dd:HH:mm", data.createtime*1000)
                helper.getView<TextView>(R.id.username).text = loginDatas.nickname
                helper.getView<TextView>(R.id.clock_place).text = data.clock_place
                helper.getView<TextView>(R.id.wifiname).text = data.wifiname ?: "未知"
                helper.getView<TextView>(R.id.wifi_distance).text = "路由器距离:${data.wifi_distance}m"
                helper.getView<TextView>(R.id.gps_distance).text = "公司的距离:${data.gps_distance}m"
                val avatar = BASEURL + loginDatas.avatar
                Glide.with(clockRecordActivity).load(avatar).transform(GlideCircleTransform(clockRecordActivity)).into(helper.getView(R.id.user_header))
            }
        }
        mContentView.adapter = baseReclyerViewAdapter
        appApi.getClockRecord(loginDatas.id, object : BaseObserver<ClockRecordDatas>(clockRecordActivity) {
            override fun onRequestFail(e: Throwable) {
                clockRecordActivity.toast(e.message.toString())
            }

            override fun onNetSuccess(datas: ClockRecordDatas) {
                baseReclyerViewAdapter.replaceAll(datas.list)
            }
        })
    }

    inner class ClockRecordModel

}
