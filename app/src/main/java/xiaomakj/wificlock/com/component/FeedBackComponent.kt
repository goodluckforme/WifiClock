package xiaomakj.wificlock.com.component

import xiaomakj.wificlock.com.mvp.ui.activity.FeedBackActivity

//import xiaomakj.wificlock.com.module.ActivityFeedBackBindingModule

import dagger.Component

@Component(dependencies = [(AppComponent::class)])
interface FeedBackComponent {
    fun inject(activity: FeedBackActivity)
}