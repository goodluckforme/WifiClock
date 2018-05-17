package xiaomakj.wificlock.com.component

import xiaomakj.wificlock.com.mvp.ui.activity.WifiDetailActivity

//import xiaomakj.wificlock.com.module.ActivityWifiDetailBindingModule

import dagger.Component

@Component(dependencies = [(AppComponent::class)])
interface WifiDetailComponent {
    fun inject(activity: WifiDetailActivity)
}