package xiaomakj.wificlock.com.component

import xiaomakj.wificlock.com.module.AppModule
import xiaomakj.wificlock.com.module.ApiModule
import dagger.Component

import android.content.Context

import xiaomakj.wificlock.com.api.AppApi

@Component(modules = [(AppModule::class), (ApiModule::class)])
interface AppComponent {
    val context: Context

    val shopApi: AppApi
}