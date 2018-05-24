package xiaomakj.wificlock.com.component

import xiaomakj.wificlock.com.mvp.ui.activity.LoginActivity

//import xiaomakj.wificlock.com.module.ActivityLoginBindingModule

import dagger.Component

@Component(dependencies = [(AppComponent::class)])
interface LoginComponent {
    fun inject(activity: LoginActivity)
}