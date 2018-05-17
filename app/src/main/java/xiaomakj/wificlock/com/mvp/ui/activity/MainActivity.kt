package xiaomakj.wificlock.com.mvp.ui.activity

import android.databinding.DataBindingUtil
import xiaomakj.wificlock.com.R
import xiaomakj.wificlock.com.component.AppComponent
import xiaomakj.wificlock.com.component.DaggerMainComponent
import xiaomakj.wificlock.com.databinding.ActivityMainBinding
import xiaomakj.wificlock.com.mvp.contract.MainContract
import xiaomakj.wificlock.com.mvp.presenter.MainPresenter
import xiaomakj.wificlock.com.mvp.ui.base.BaseActivity

class MainActivity : BaseActivity<MainPresenter, MainContract.View, ActivityMainBinding>(), MainContract.View {

    override fun getNameId(): Int = R.string.main

    override fun setActivityComponent(appComponent: AppComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .build()
                .inject(this)
    }

    public override fun initView() {
        mPresenter.getLayoutRes(DataBindingUtil.setContentView<ActivityMainBinding>(this@MainActivity, R.layout.activity_main))
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 555) {
            mPresenter.getPermission()
        }
    }
}
