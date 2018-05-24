package xiaomakj.wificlock.com.mvp.ui.activity

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header.*
import xiaomakj.wificlock.com.R
import xiaomakj.wificlock.com.component.AppComponent
import xiaomakj.wificlock.com.component.DaggerMainComponent
import xiaomakj.wificlock.com.databinding.ActivityMainBinding
import xiaomakj.wificlock.com.mvp.contract.MainContract
import xiaomakj.wificlock.com.mvp.presenter.MainPresenter
import xiaomakj.wificlock.com.mvp.ui.base.BaseActivity
import xiaomakj.wificlock.com.utils.SharedPreferencesUtil

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
        head_back.visibility = View.INVISIBLE
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1102 && Activity.RESULT_OK == resultCode) { // 详细地址
            val formatAddress = data?.getStringExtra("formatAddress") ?: ""
            val coordinate = data?.getStringExtra("coordinate") ?: ""
            Log.i("onActivityResult", "formatAddress===========" + formatAddress)
            Log.i("onActivityResult", "coordinate===========" + coordinate)
            SharedPreferencesUtil.instance?.putString("coordinate", coordinate)
            SharedPreferencesUtil.instance?.putString("formatAddress", coordinate)
        }
    }
}
