package xiaomakj.wificlock.com.component

import xiaomakj.wificlock.com.mvp.ui.activity.LogingCaptchaActivity

//import xiaomakj.wificlock.com.module.ActivityLogingCaptchaBindingModule

import dagger.Component

@Component(dependencies = [(AppComponent::class)])
interface LogingCaptchaComponent {
    fun inject(activity: LogingCaptchaActivity)
}