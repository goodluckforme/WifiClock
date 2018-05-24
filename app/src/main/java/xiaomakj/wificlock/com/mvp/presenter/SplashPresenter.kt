package xiaomakj.wificlock.com.mvp.presenter


import android.content.Context
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.OnCompositionLoadedListener
import xiaomakj.wificlock.com.App
import javax.inject.Inject
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.data.LoginDatas
import xiaomakj.wificlock.com.databinding.ActivitySplashBinding
import xiaomakj.wificlock.com.mvp.contract.SplashContract
import xiaomakj.wificlock.com.mvp.ui.activity.LoginActivity
import xiaomakj.wificlock.com.mvp.ui.activity.MainActivity
import xiaomakj.wificlock.com.mvp.ui.activity.SplashActivity
import xiaomakj.wificlock.com.utils.SharedPreferencesUtil
import xiaomakj.wificlock.com.utils.launchActivity

class SplashPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<SplashContract.View, ActivitySplashBinding>(), SplashContract.Presenter {
    //            LottieComposition.Factory.fromAssetFileName(splashActivity, "looking_for_router.json", object : OnCompositionLoadedListener {
//                override fun onCompositionLoaded(composition: LottieComposition?) {
//                }
//            })
    fun toInit() {
        mContentView.splashModel = SplashModel()
        val splashActivity = mView as SplashActivity
        App.instance.mainHandler.postDelayed({
            val userinfo = SharedPreferencesUtil.instance?.getObject("USERINFO", LoginDatas.Userinfo::class.java)
            if (null == userinfo || userinfo.token.isEmpty()) {
                splashActivity.launchActivity<LoginActivity> { }
            } else {
                splashActivity.launchActivity<MainActivity> { }
                splashActivity.finish()
            }
        }, 5000)
    }

    inner class SplashModel

}
