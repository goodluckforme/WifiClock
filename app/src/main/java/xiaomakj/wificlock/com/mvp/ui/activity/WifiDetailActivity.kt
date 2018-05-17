package xiaomakj.wificlock.com.mvp.ui.activity

import xiaomakj.wificlock.com.mvp.ui.base.BaseActivity
import xiaomakj.wificlock.com.mvp.presenter.WifiDetailPresenter
import xiaomakj.wificlock.com.mvp.contract.WifiDetailContract
import xiaomakj.wificlock.com.component.AppComponent
import xiaomakj.wificlock.com.component.DaggerWifiDetailComponent
import xiaomakj.wificlock.com.databinding.ActivityWifiDetailBinding
import xiaomakj.wificlock.com.R
import android.databinding.DataBindingUtil

class WifiDetailActivity : BaseActivity<WifiDetailPresenter, WifiDetailContract.View, ActivityWifiDetailBinding>(), WifiDetailContract.View {
    override fun getNameId(): Int = R.string.wifi_detail

    override fun setActivityComponent(appComponent: AppComponent) {
        DaggerWifiDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .build()
                .inject(this)
    }

    public override fun initView() {
        mPresenter.getLayoutRes(DataBindingUtil.setContentView<ActivityWifiDetailBinding>(this@WifiDetailActivity, R.layout.activity_wifi_detail))
        super.initView()
    }

    public override fun initData() {
        super.initData()
        mPresenter.attachView(this)
        mPresenter.toInit()
    }

    override fun complete(msg: String) {

    }

    override fun showError(msg: String) {

    }
}
