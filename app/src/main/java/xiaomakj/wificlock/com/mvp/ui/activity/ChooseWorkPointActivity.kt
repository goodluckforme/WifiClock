package xiaomakj.wificlock.com.mvp.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_choose_work_point.*
import kotlinx.android.synthetic.main.header.*
import xiaomakj.wificlock.com.R
import xiaomakj.wificlock.com.component.AppComponent
import xiaomakj.wificlock.com.component.DaggerChooseWorkPointComponent
import xiaomakj.wificlock.com.databinding.ActivityChooseWorkPointBinding
import xiaomakj.wificlock.com.mvp.contract.ChooseWorkPointContract
import xiaomakj.wificlock.com.mvp.presenter.ChooseWorkPointPresenter
import xiaomakj.wificlock.com.mvp.ui.base.BaseActivity
import xiaomakj.wificlock.com.utils.SharedPreferencesUtil

class ChooseWorkPointActivity : BaseActivity<ChooseWorkPointPresenter, ChooseWorkPointContract.View, ActivityChooseWorkPointBinding>(), ChooseWorkPointContract.View {
    override fun getNameId(): Int = R.string.ChooseWorkPoint

    override fun setActivityComponent(appComponent: AppComponent) {
        DaggerChooseWorkPointComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .build()
                .inject(this)
    }

    public override fun initView() {
        mPresenter.getLayoutRes(DataBindingUtil.setContentView<ActivityChooseWorkPointBinding>(this@ChooseWorkPointActivity, R.layout.activity_choose_work_point))
        super.initView()
        head_title.text = SharedPreferencesUtil.instance?.getString("formatAddress", "").toString()
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


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMapView.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }
}
