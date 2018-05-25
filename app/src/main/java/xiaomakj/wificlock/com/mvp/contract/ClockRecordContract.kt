package xiaomakj.wificlock.com.mvp.contract

import  xiaomakj.wificlock.com.common.BaseContract

import xiaomakj.wificlock.com.databinding.ActivityClockRecordBinding

interface ClockRecordContract {
    interface View : BaseContract.BaseView

    interface Presenter : BaseContract.BasePresenter<View, ActivityClockRecordBinding>
}
