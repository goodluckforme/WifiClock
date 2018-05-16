package xiaomakj.wificlock.com.mvp.presenter


import android.content.Context
import xiaomakj.wificlock.com.api.AppApi
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.databinding.ActivityMainBinding
import xiaomakj.wificlock.com.mvp.contract.MainContract
import javax.inject.Inject

class MainPresenter @Inject constructor(private val appApi: AppApi, private val context: Context) :
        RxPresenter<MainContract.View, ActivityMainBinding>(), MainContract.Presenter {

    fun toInit() {
        mContentView.mainModel = MainModel()
        
    }

    inner class MainModel

}
