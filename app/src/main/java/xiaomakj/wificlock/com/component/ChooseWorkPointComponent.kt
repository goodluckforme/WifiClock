package xiaomakj.wificlock.com.component

import xiaomakj.wificlock.com.mvp.ui.activity.ChooseWorkPointActivity

//import xiaomakj.wificlock.com.module.ActivityChooseWorkPointBindingModule

import dagger.Component

@Component(dependencies = [(AppComponent::class)])
interface ChooseWorkPointComponent {
    fun inject(activity: ChooseWorkPointActivity)
}