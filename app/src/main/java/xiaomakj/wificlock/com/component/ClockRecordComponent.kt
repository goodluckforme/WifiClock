package xiaomakj.wificlock.com.component

import xiaomakj.wificlock.com.mvp.ui.activity.ClockRecordActivity

//import xiaomakj.wificlock.com.module.ActivityClockRecordBindingModule

import dagger.Component

@Component(dependencies = [(AppComponent::class)])
interface ClockRecordComponent {
    fun inject(activity: ClockRecordActivity)
}