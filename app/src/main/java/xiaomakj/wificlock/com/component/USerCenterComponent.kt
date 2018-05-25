package xiaomakj.wificlock.com.component

import xiaomakj.wificlock.com.mvp.ui.activity.UserCenterActivity

//import xiaomakj.wificlock.com.module.ActivityUSerCenterBindingModule

import dagger.Component

@Component(dependencies = [(AppComponent::class)])
interface USerCenterComponent {
    fun inject(activity: UserCenterActivity)
}