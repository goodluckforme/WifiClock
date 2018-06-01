package xiaomakj.wificlock.com.mvp.ui.activity

import xiaomakj.wificlock.com.mvp.ui.base.BaseActivity
import xiaomakj.wificlock.com.mvp.presenter.LogingCaptchaPresenter
import xiaomakj.wificlock.com.mvp.contract.LogingCaptchaContract
import xiaomakj.wificlock.com.component.AppComponent
import xiaomakj.wificlock.com.component.DaggerLogingCaptchaComponent
import xiaomakj.wificlock.com.databinding.ActivityLogingCaptchaBinding
import xiaomakj.wificlock.com.R
import android.databinding.DataBindingUtil

class LogingCaptchaActivity : BaseActivity<LogingCaptchaPresenter, LogingCaptchaContract.View, ActivityLogingCaptchaBinding>(), LogingCaptchaContract.View {
    override fun getNameId(): Int = R.string.loginBycaptcha

    override fun setActivityComponent(appComponent: AppComponent) {
        DaggerLogingCaptchaComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .build()
                .inject(this)
    }

    public override fun initView() {
        mPresenter.getLayoutRes(DataBindingUtil.setContentView<ActivityLogingCaptchaBinding>(this@LogingCaptchaActivity, R.layout.activity_loging_captcha))
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
