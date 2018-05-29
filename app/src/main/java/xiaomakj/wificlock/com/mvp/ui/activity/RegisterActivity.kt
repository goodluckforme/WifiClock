package xiaomakj.wificlock.com.mvp.ui.activity

import xiaomakj.wificlock.com.mvp.ui.base.BaseActivity
import xiaomakj.wificlock.com.mvp.presenter.RegisterPresenter
import xiaomakj.wificlock.com.mvp.contract.RegisterContract
import xiaomakj.wificlock.com.component.AppComponent
import xiaomakj.wificlock.com.component.DaggerRegisterComponent
import xiaomakj.wificlock.com.databinding.ActivityRegisterBinding
import xiaomakj.wificlock.com.R
import android.databinding.DataBindingUtil

class RegisterActivity : BaseActivity<RegisterPresenter, RegisterContract.View, ActivityRegisterBinding>(), RegisterContract.View {
    override fun getNameId(): Int =R.string.register

    override fun setActivityComponent(appComponent: AppComponent) {
        DaggerRegisterComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .build()
                .inject(this)
    }

    public override fun initView() {
        mPresenter.getLayoutRes(DataBindingUtil.setContentView<ActivityRegisterBinding>(this@RegisterActivity, R.layout.activity_register))
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
