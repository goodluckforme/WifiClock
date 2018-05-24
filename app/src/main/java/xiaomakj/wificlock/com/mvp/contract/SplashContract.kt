package xiaomakj.wificlock.com.mvp.contract

import  xiaomakj.wificlock.com.common.BaseContract

import xiaomakj.wificlock.com.databinding.ActivitySplashBinding

interface SplashContract {
    interface View : BaseContract.BaseView

    interface Presenter : BaseContract.BasePresenter<View, ActivitySplashBinding>
}
