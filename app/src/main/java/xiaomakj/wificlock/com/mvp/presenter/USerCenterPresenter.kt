package xiaomakj.wificlock.com.mvp.presenter


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Environment
import android.os.IBinder
import android.view.View
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.api.BaseObserver
import xiaomakj.wificlock.com.common.BASEURL
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.data.LoginDatas
import xiaomakj.wificlock.com.data.UploadDats
import xiaomakj.wificlock.com.databinding.ActivityUserCenterBinding
import xiaomakj.wificlock.com.mvp.contract.UserCenterContract
import xiaomakj.wificlock.com.mvp.ui.activity.ClockRecordActivity
import xiaomakj.wificlock.com.mvp.ui.activity.LoginActivity
import xiaomakj.wificlock.com.mvp.ui.activity.UserCenterActivity
import xiaomakj.wificlock.com.services.ColockSevice
import xiaomakj.wificlock.com.utils.GlideCircleTransform
import xiaomakj.wificlock.com.utils.LocationUtils
import xiaomakj.wificlock.com.utils.SharedPreferencesUtil
import xiaomakj.wificlock.com.utils.launchActivity
import java.io.File
import javax.inject.Inject


class USerCenterPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<UserCenterContract.View, ActivityUserCenterBinding>(), UserCenterContract.Presenter {

    companion object {
        val RC_CHOOSE_PHOTO = 1002
    }

    var mClockBinder: ColockSevice.ClockBinder? = null
    lateinit var loginDatas: LoginDatas.Userinfo
    fun toInit() {
        val userCenterActivity = mView as UserCenterActivity
        loginDatas = SharedPreferencesUtil.instance?.getObject("USERINFO", LoginDatas.Userinfo::class.java) ?: return
        mContentView.uSerCenterModel = UserCenterModel()
        val avatar = BASEURL + loginDatas.avatar
        mContentView.username.text = loginDatas.nickname
        Glide.with(userCenterActivity).load(avatar).transform(GlideCircleTransform(userCenterActivity)).into(mContentView.userHeader)
        val intent = Intent(userCenterActivity, ColockSevice::class.java)
        userCenterActivity.bindService(intent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mClockBinder = service as ColockSevice.ClockBinder
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }

        }, Context.BIND_AUTO_CREATE)
        mContentView.AutoClockIv.isEnabled = !(SharedPreferencesUtil.instance?.getBoolean("AutoClock", false) ?: false)
    }

    override fun uploadHeadImg(selectedPhotos: File) {
        val userCenterActivity = mView as UserCenterActivity
        appApi.upload(file = selectedPhotos, token = loginDatas.token, observer = object : BaseObserver<Any>(userCenterActivity) {
            override fun onRequestFail(e: Throwable) {
                userCenterActivity.toast(e.message.toString())
            }

            override fun onNetSuccess(datas: Any) {
                loginDatas.avatar = Gson().fromJson<UploadDats>(Gson().toJson(datas), UploadDats::class.java).url
                changeProfile(userCenterActivity, selectedPhotos)
            }
        })
    }

    private fun changeProfile(userCenterActivity: UserCenterActivity, selectedPhotos: File) {
        appApi.changeProfile(loginDatas, observer = object : BaseObserver<Any>(userCenterActivity) {
            override fun onRequestFail(e: Throwable) {
                userCenterActivity.toast(e.message.toString())
            }

            override fun onNetSuccess(datas: Any) {
                Glide.with(userCenterActivity).load(selectedPhotos).transform(GlideCircleTransform(userCenterActivity)).into(mContentView.userHeader)
            }
        })
    }

    inner class UserCenterModel {
        fun outlogin(v: View) {
            val userCenterActivity = mView as UserCenterActivity
            userCenterActivity.alert("退出App将销毁所有用户信息", "登出") {
                positiveButton("确定") {
                    userCenterActivity.launchActivity<LoginActivity>()
                    mClockBinder?.stopForeground()
                    SharedPreferencesUtil.instance?.removeAll()
                }
                negativeButton("取消") {

                }
            }.show()
        }

        fun feedback(v: View) {

        }

        fun changeAvatar(v: View) {
            val userCenterActivity = mView as UserCenterActivity
            val takePhotoDir = File(Environment.getExternalStorageDirectory(), "${userCenterActivity.packageName}_PIC")
            val newIntent = BGAPhotoPickerActivity.newIntent(userCenterActivity, takePhotoDir, 1, arrayListOf(), false)
            userCenterActivity.startActivityForResult(newIntent, RC_CHOOSE_PHOTO)
        }

        fun star(v: View) {
        }

        fun autoClock(v: View) {
            var isChecked = mContentView.AutoClockIv.isEnabled
            mContentView.AutoClockIv.isEnabled = !isChecked
            SharedPreferencesUtil.instance?.putBoolean("AutoClock", !isChecked)
            if (isChecked) {
                mClockBinder?.startForeground(object : ColockSevice.ColockOnLocationChangeListener {
                    override fun enableGps() {

                    }

                    override fun disableGps() {
                        toEnableGPS(mView as UserCenterActivity)
                    }
                })
            } else {
                mClockBinder?.stopForeground()
            }
        }

        fun clockRecord(v: View) {
            val userCenterActivity = mView as UserCenterActivity
            userCenterActivity.launchActivity<ClockRecordActivity> { }
        }
    }


    private fun toEnableGPS(userCenterActivity: UserCenterActivity) {
        if (!LocationUtils.isGpsEnabled()) {
            userCenterActivity.alert("API22以上的手机只有开启GPS后才有能力获取WIFI信息") {
                positiveButton("开启") {
                    LocationUtils.openGpsSettings()
                }
                negativeButton("拒绝") {}
            }.show()
        } else userCenterActivity.toast("GPS已经开启")

    }

}
