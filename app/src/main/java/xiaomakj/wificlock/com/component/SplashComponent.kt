package xiaomakj.wificlock.com.component

import xiaomakj.wificlock.com.mvp.ui.activity.SplashActivity

//import xiaomakj.wificlock.com.module.ActivitySplashBindingModule

import dagger.Component

@Component(dependencies = [(AppComponent::class)])
interface SplashComponent {
    fun inject(activity: SplashActivity)
}