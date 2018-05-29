package xiaomakj.wificlock.com.mvp.ui.activity

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.webkit.WebSettings
import kotlinx.android.synthetic.main.activity_feed_back.*
import xiaomakj.wificlock.com.R
import xiaomakj.wificlock.com.component.AppComponent
import xiaomakj.wificlock.com.component.DaggerFeedBackComponent
import xiaomakj.wificlock.com.databinding.ActivityFeedBackBinding
import xiaomakj.wificlock.com.mvp.contract.FeedBackContract
import xiaomakj.wificlock.com.mvp.presenter.FeedBackPresenter
import xiaomakj.wificlock.com.mvp.ui.base.BaseActivity
import java.io.File

class FeedBackActivity : BaseActivity<FeedBackPresenter, FeedBackContract.View, ActivityFeedBackBinding>(), FeedBackContract.View {
    override fun getNameId(): Int = R.string.feedback

    override fun setActivityComponent(appComponent: AppComponent) {
        DaggerFeedBackComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .build()
                .inject(this)
    }

    public override fun initView() {
        mPresenter.getLayoutRes(DataBindingUtil.setContentView<ActivityFeedBackBinding>(this@FeedBackActivity, R.layout.activity_feed_back))
        super.initView()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        feed_back_wb.saveState(outState)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            feed_back_wb.restoreState(savedInstanceState)
        } else {
            //D:\androidSpace\WifiClock\app\src\main\assets\mui\examples\feedback.html
            //val uriForFile = FileProvider.getUriForFile(this, packageName + ".provider", file)
            feed_back_wb.loadUrl("file:///android_asset/mui/project/feedback.html")
        }
        val settings = feed_back_wb.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        settings.databasePath = filesDir.parentFile.path + "/wificlock"
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
