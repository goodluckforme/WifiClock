package xiaomakj.wificlock.com.mvp.ui.activity

import xiaomakj.wificlock.com.mvp.ui.base.BaseActivity
import xiaomakj.wificlock.com.mvp.presenter.SplashPresenter
import xiaomakj.wificlock.com.mvp.contract.SplashContract
import xiaomakj.wificlock.com.component.AppComponent
import xiaomakj.wificlock.com.component.DaggerSplashComponent
import xiaomakj.wificlock.com.databinding.ActivitySplashBinding
import xiaomakj.wificlock.com.R
import android.databinding.DataBindingUtil

class SplashActivity : BaseActivity<SplashPresenter, SplashContract.View, ActivitySplashBinding>(), SplashContract.View {
    override fun getNameId(): Int = R.string.nullstring

    override fun setActivityComponent(appComponent: AppComponent) {
        DaggerSplashComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .build()
                .inject(this)
    }

    public override fun initView() {
        mPresenter.getLayoutRes(DataBindingUtil.setContentView<ActivitySplashBinding>(this@SplashActivity, R.layout.activity_splash))
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
