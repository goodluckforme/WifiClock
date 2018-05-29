package xiaomakj.wificlock.com.component

import xiaomakj.wificlock.com.mvp.ui.activity.WebActivity

//import xiaomakj.wificlock.com.module.ActivityWebBindingModule

import dagger.Component

@Component(dependencies = [(AppComponent::class)])
interface WebComponent {
    fun inject(activity: WebActivity)
}