package xiaomakj.wificlock.com.mvp.presenter


import android.content.Context
import android.util.Log
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import xiaomakj.wificlock.com.R
import javax.inject.Inject
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.api.BaseObserver
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.data.LoginDatas
import xiaomakj.wificlock.com.databinding.ActivityRegisterBinding
import xiaomakj.wificlock.com.mvp.contract.RegisterContract
import xiaomakj.wificlock.com.mvp.ui.activity.MainActivity
import xiaomakj.wificlock.com.mvp.ui.activity.RegisterActivity
import xiaomakj.wificlock.com.utils.SharedPreferencesUtil
import xiaomakj.wificlock.com.utils.launchActivity
class RegisterPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<RegisterContract.View, ActivityRegisterBinding>(), RegisterContract.Presenter {

    fun toInit() {
        mContentView.registerModel = RegisterModel()
        val registerActivity = mView as RegisterActivity
        mContentView.toRegister.onClick {
            val accound = mContentView.edAccound.text.toString().trim()
            val phone = mContentView.edphone.text.toString().trim()
            val email = mContentView.edEmail.text.toString().trim()
            val spw = mContentView.edPsw.text.toString().trim()
            val spw2 = mContentView.edPsw2.text.toString().trim()
            if ((spw.isEmpty() || spw2.isEmpty()) && spw == spw2) {
                registerActivity.toast(R.string.err_psw_common)
            } else {
                appApi.register(
                        hashMapOf(
                                Pair("username", accound),
                                Pair("mobile", phone),
                                Pair("email", email),
                                Pair("password", spw)
                        ),
                        object : BaseObserver<LoginDatas>(registerActivity) {
                            override fun onRequestFail(e: Throwable) {
                                registerActivity.toast(e.message.toString())
                            }

                            override fun onNetSuccess(datas: LoginDatas) {
                                Log.i("LoginDatas", "LoginDatas====================$datas")
                                SharedPreferencesUtil.instance?.putObject("USERINFO", datas.userinfo)
                                registerActivity.launchActivity<MainActivity> {  }
                            }
                        })
            }
        }

    }

    inner class RegisterModel

}
