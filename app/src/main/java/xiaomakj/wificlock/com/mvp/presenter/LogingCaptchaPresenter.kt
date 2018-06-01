package xiaomakj.wificlock.com.mvp.presenter


import android.content.Context
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import xiaomakj.wificlock.com.R
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.api.BaseObserver
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.databinding.ActivityLogingCaptchaBinding
import xiaomakj.wificlock.com.mvp.contract.LogingCaptchaContract
import xiaomakj.wificlock.com.mvp.ui.activity.LogingCaptchaActivity
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LogingCaptchaPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<LogingCaptchaContract.View, ActivityLogingCaptchaBinding>(), LogingCaptchaContract.Presenter {
    companion object {
        var count = 60
    }

    fun toInit() {
        mContentView.logingCaptchaModel = LogingCaptchaModel()
        val logingCaptchaActivity = mView as LogingCaptchaActivity
        mContentView.LaLogin.onClick {
            val edCapchaString = mContentView.edCapcha.text.toString().trim()
            val edPhoneString = mContentView.edPhone.text.toString().trim()
            if (edPhoneString.isEmpty() || edCapchaString.isEmpty()) {
                logingCaptchaActivity.toast(R.string.err_captcha)
                return@onClick
            }
        }
        val codeButton = mContentView.getCode
        if (count != 60) {
            subscribe = Observable
                    .interval(0, 1, TimeUnit.SECONDS)
                    .map {
                        count--
                    }
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        codeButton.isEnabled = false
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (count == 0) {
                            count = 60
                            codeButton.isEnabled = true
                            codeButton.text = "获取验证码"
                            subscribe.unsubscribe()
                        } else codeButton.text = "${count}重新发送"
                    }
        }
        codeButton.onClick {
            subscribe = Observable
                    .interval(0, 1, TimeUnit.SECONDS)
                    .map {
                        count--
                    }
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        codeButton.isEnabled = false
                        toSendCode()
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (count == 0) {
                            count = 60
                            codeButton.text = "获取验证码"
                            codeButton.isEnabled = true
                            subscribe.unsubscribe()
                        } else codeButton.text = "${count}重新发送"
                    }
        }
    }

    private fun toSendCode() {
        val logingCaptchaActivity = mView as LogingCaptchaActivity
        val edPhoneString = mContentView.edPhone.text.toString().trim()
        appApi.sendCode(edPhoneString,object :BaseObserver<Any>(logingCaptchaActivity){
            override fun onRequestFail(e: Throwable) {
                logingCaptchaActivity.toast(e.message.toString())
            }

            override fun onNetSuccess(datas: Any) {

            }
        })
    }

    override fun detachView() {
        super.detachView()
        if (null != subscribe) subscribe.unsubscribe()
    }

    lateinit var subscribe: Subscription

    inner class LogingCaptchaModel

}

