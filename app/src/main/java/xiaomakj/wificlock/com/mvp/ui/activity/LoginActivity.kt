package xiaomakj.wificlock.com.mvp.ui.activity

import xiaomakj.wificlock.com.mvp.ui.base.BaseActivity
import xiaomakj.wificlock.com.mvp.presenter.LoginPresenter
import xiaomakj.wificlock.com.mvp.contract.LoginContract
import xiaomakj.wificlock.com.component.AppComponent
import xiaomakj.wificlock.com.component.DaggerLoginComponent
import xiaomakj.wificlock.com.databinding.ActivityLoginBinding
import xiaomakj.wificlock.com.R
import android.databinding.DataBindingUtil

class LoginActivity : BaseActivity<LoginPresenter, LoginContract.View, ActivityLoginBinding>(), LoginContract.View {
    override fun getNameId(): Int = R.string.login

    override fun setActivityComponent(appComponent: AppComponent) {
        DaggerLoginComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .build()
                .inject(this)
    }

    public override fun initView() {
        mPresenter.getLayoutRes(DataBindingUtil.setContentView<ActivityLoginBinding>(this@LoginActivity, R.layout.activity_login))
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
