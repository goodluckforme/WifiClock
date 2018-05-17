package xiaomakj.wificlock.com.mvp.ui.base

import android.app.ProgressDialog
import android.content.pm.ActivityInfo
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.header.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import xiaomakj.wificlock.com.App
import xiaomakj.wificlock.com.common.BaseContract
import xiaomakj.wificlock.com.common.RxPresenter
import xiaomakj.wificlock.com.component.AppComponent
import javax.inject.Inject


abstract class BaseActivity<T : RxPresenter<V, M>, V : BaseContract.BaseView, M : ViewDataBinding> : AppCompatActivity(), BaseContract.BaseView {
    var isFirst: Boolean = false
    @Inject lateinit var mPresenter: T
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //QMUIStatusBarHelper.translucent(this, Color.parseColor("#ffffff"))
        //竖屏设置
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setActivityComponent(App.instance.appComponent)
        initView()
    }

    val dailog: ProgressDialog  by lazy {
        ProgressDialog(this)
    }

    abstract fun setActivityComponent(appComponent: AppComponent)

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !isFirst) {
            isFirst = true
            initData()
        }
    }

    protected open fun initView() {
        head_back.onClick { finish() }
        head_title.setText(getNameId())
    }

    abstract fun getNameId(): Int

    protected open fun initData() {
    }


    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
        dailog.dismiss()
    }
}