package xiaomakj.wificlock.com.component

import xiaomakj.wificlock.com.mvp.ui.activity.MainActivity

//import xiaomakj.wificlock.com.module.ActivityMainBindingModule

import dagger.Component

@Component(dependencies = [(AppComponent::class)])
interface MainComponent {
    fun inject(activity: MainActivity)
}