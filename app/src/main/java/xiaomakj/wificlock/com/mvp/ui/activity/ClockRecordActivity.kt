package xiaomakj.wificlock.com.mvp.ui.activity

import xiaomakj.wificlock.com.mvp.ui.base.BaseActivity
import xiaomakj.wificlock.com.mvp.presenter.ClockRecordPresenter
import xiaomakj.wificlock.com.mvp.contract.ClockRecordContract
import xiaomakj.wificlock.com.component.AppComponent
import xiaomakj.wificlock.com.component.DaggerClockRecordComponent
import xiaomakj.wificlock.com.databinding.ActivityClockRecordBinding
import xiaomakj.wificlock.com.R
import android.databinding.DataBindingUtil

class ClockRecordActivity : BaseActivity<ClockRecordPresenter, ClockRecordContract.View, ActivityClockRecordBinding>(), ClockRecordContract.View {
    override fun getNameId(): Int = R.string.clockrecord

    override fun setActivityComponent(appComponent: AppComponent) {
        DaggerClockRecordComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .build()
                .inject(this)
    }

    public override fun initView() {
        mPresenter.getLayoutRes(DataBindingUtil.setContentView<ActivityClockRecordBinding>(this@ClockRecordActivity, R.layout.activity_clock_record))
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
