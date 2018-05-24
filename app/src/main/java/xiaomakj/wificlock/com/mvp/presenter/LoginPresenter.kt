package xiaomakj.wificlock.com.mvp.presenter


import android.animation.Animator
import android.content.Context
import android.util.Log
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import javax.inject.Inject
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.api.BaseObserver
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.data.LoginDatas
import xiaomakj.wificlock.com.databinding.ActivityLoginBinding
import xiaomakj.wificlock.com.mvp.contract.LoginContract
import xiaomakj.wificlock.com.mvp.ui.activity.LoginActivity
import xiaomakj.wificlock.com.mvp.ui.activity.MainActivity
import xiaomakj.wificlock.com.utils.SharedPreferencesUtil
import xiaomakj.wificlock.com.utils.launchActivity

class LoginPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<LoginContract.View, ActivityLoginBinding>(), LoginContract.Presenter {

    fun toInit() {
        mContentView.loginModel = LoginModel()
        val loginActivity = mView as LoginActivity
        val laLogin = mContentView.LaLogin
        laLogin.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                laLogin.clearAnimation()
                loginActivity.launchActivity<MainActivity> { }
                loginActivity.finish()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        laLogin.onClick {
            if (laLogin.isAnimating) return@onClick
            val account = mContentView.edAccound.text.toString().trim()
            val psw = mContentView.edPsw.text.toString().trim()
            if (account.isEmpty() || psw.isEmpty()) {
                loginActivity.toast("账号或密码不能为空")
            } else {
                appApi.toLogin(account, psw, object : BaseObserver<LoginDatas>(loginActivity) {
                    override fun onRequestFail(e: Throwable) {
                        loginActivity.toast(e.message.toString())
                    }

                    override fun onNetSuccess(datas: LoginDatas) {
                        Log.i("LoginDatas", "LoginDatas====================$datas")
                        SharedPreferencesUtil.instance?.putObject("USERINFO", datas.userinfo)
                        laLogin.playAnimation()
                    }
                })
            }
        }
    }

    inner class LoginModel

}
