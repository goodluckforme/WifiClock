package xiaomakj.wificlock.com.component

import xiaomakj.wificlock.com.mvp.ui.activity.RegisterActivity

//import xiaomakj.wificlock.com.module.ActivityRegisterBindingModule

import dagger.Component

@Component(dependencies = [(AppComponent::class)])
interface RegisterComponent {
    fun inject(activity: RegisterActivity)
}