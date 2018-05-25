package xiaomakj.wificlock.com.mvp.contract

import  xiaomakj.wificlock.com.common.BaseContract

import xiaomakj.wificlock.com.databinding.ActivityUserCenterBinding
import java.io.File

interface UserCenterContract {
    interface View : BaseContract.BaseView

    interface Presenter : BaseContract.BasePresenter<View, ActivityUserCenterBinding> {
        fun uploadHeadImg(selectedPhotos: File)
    }
}
