package xiaomakj.wificlock.com.mvp.ui.activity

import xiaomakj.wificlock.com.mvp.ui.base.BaseActivity
import xiaomakj.wificlock.com.mvp.presenter.WebPresenter
import xiaomakj.wificlock.com.mvp.contract.WebContract
import xiaomakj.wificlock.com.component.AppComponent
import xiaomakj.wificlock.com.component.DaggerWebComponent
import xiaomakj.wificlock.com.databinding.ActivityWebBinding
import xiaomakj.wificlock.com.R
import android.databinding.DataBindingUtil
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import kotlinx.android.synthetic.main.activity_web.*
import kotlinx.android.synthetic.main.header.*

class WebActivity : BaseActivity<WebPresenter, WebContract.View, ActivityWebBinding>(), WebContract.View {
    override fun getNameId(): Int = R.string.star

    override fun setActivityComponent(appComponent: AppComponent) {
        DaggerWebComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .build()
                .inject(this)
    }

    //https://github.com/goodluckforme/WifiClock
    public override fun initView() {
        mPresenter.getLayoutRes(DataBindingUtil.setContentView<ActivityWebBinding>(this@WebActivity, R.layout.activity_web))
        super.initView()
        val myUrl = intent.getStringExtra("URL").toString()
        val settings = web.settings
        settings.javaScriptCanOpenWindowsAutomatically = true// 设置js可以直接打开窗口，如window.open()，默认为false
        settings.javaScriptEnabled = true// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        settings.setSupportZoom(true)// 是否可以缩放，默认true
        //        settings.setBuiltInZoomControls(true);// 是否显示缩放按钮，默认false  这个东西是会出现bug的
        settings.useWideViewPort = true// 设置此属性，可任意比例缩放。大视图模式
        settings.loadWithOverviewMode = true// 和setUseWideViewPort(true)一起解决网页自适应问题
        settings.setAppCacheEnabled(true)// 是否使用缓存
        settings.domStorageEnabled = true// DOM Storage
        settings.allowFileAccess = true
        // 不允许存储敏感信息
        // settings.setSavePassword(false);
        // 恢复用户设置的字体
        //settings.setTextSize(WebSettings.TextSize.SMALLER);
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        web.canGoBack()
        web.loadUrl(myUrl)
        web.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                pb.progress = newProgress
                if (newProgress >= 90) pb.visibility = View.GONE
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
//                head_title.text = title.toString()
            }
        }
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

    override fun onDestroy() {
        if (web != null) {
            web.visibility = View.GONE
            web.removeAllViews()
            web.destroy()
        }
        super.onDestroy()
    }

    /**
     * 按键响应，在WebView中查看网页时，按返回键的时候按浏览历史退回,如果不做此项处理则整个WebView返回退出
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) {
            // 返回键退回
            web.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
