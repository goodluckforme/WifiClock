package xiaomakj.wificlock.com.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.app.PendingIntent
import xiaomakj.wificlock.com.R
import xiaomakj.wificlock.com.mvp.ui.activity.MainActivity


/**
 * Created by Administrator on 2018/5/18.
 */
class ColockSevice : Service() {
    /**
     * 先分析一下 自动打卡的条件
     * 1.WIFI连上后
     * 2.WIFI距离路由器距离到达临界值
     * 3.GPS定位到当前位置
     * 4.手动打开自定打卡APP（必须条件 长期运行可加入白名单）
     * 5.后台长期运行的服务 能够通过WIFI打开的广播 扫描WIFI环境并选择连接上指定WIFI.(目前只通过SSID判断即可)
     * 6.根据前面的条件 寻找最佳时机  请求PHP服务器 打卡
     *
     *
     * 思考：手机打开WIFI开关后,是否存在连接上指定WIFI立即发送广播的可能,自己实现是否可行?
     * 1 自己实现的方法：当距离当前路由器小于100米的时候 开启后台长期运行的服务可以轮训 遍历当前WIFI列表 直到连接上指定WIFI 查寻当前连接WIFI距离条件打卡
     *  问题1  如何确定路由器的唯一性(MAC 去掉MAC的后四位即可) 必须
     *  问题2  如何确定WIFI的唯一性(SSID?)  非必须因为一个路由器能发出几个WIFI
     * */
    override fun onBind(intent: Intent?): IBinder {
        return mClockBinder
    }

    var isPlay = false

    val mClockBinder by lazy { ClockBinder() }

    inner class ClockBinder : Binder() {
        fun startForeground() = play()
        fun stopForeground() = stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        play()
        return START_STICKY
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }

    private fun stop() {
        if (isPlay) {
            isPlay = false
            //将服务从forefround状态中移走，使得系统可以在低内存的情况下清除它。
            stopForeground(true)
        }
    }

    private fun play() {
        if (!isPlay) {
            isPlay = true
            //和上一笔记中创建通知的步骤一样，只是不需要通过通知管理器进行触发，而是用startForeground(ID,notify)来处理
            //步骤1：和上一笔记一样，通过Notification.Builder( )来创建通知
            //FakePlayer就是两个大button的activity，也即服务的界面，见最左图
            val i = Intent(this, MainActivity::class.java)
            //注意Intent的flag设置：FLAG_ACTIVITY_CLEAR_TOP: 如果activity已在当前任务中运行，在它前端的activity都会被关闭，它就成了最前端的activity。FLAG_ACTIVITY_SINGLE_TOP: 如果activity已经在最前端运行，则不需要再加载。设置这两个flag，就是让一个且唯一的一个activity（服务界面）运行在最前端。
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pi = PendingIntent.getActivity(this, 0, i, 0)
            val myNotify = Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("WIFIClock")
                    .setContentText("WIFIClock正在工作中")
                    .setContentIntent(null)
                    .getNotification()
            //设置notification的flag，表明在点击通知后，通知并不会消失，也在最右图上仍在通知栏显示图标。这是确保在activity中退出后，状态栏仍有图标可提下拉、点击，再次进入activity。
            myNotify.flags = myNotify.flags or Notification.FLAG_NO_CLEAR
            // 步骤 2：startForeground( int, Notification)将服务设置为foreground状态，使系统知道该服务是用户关注，低内存情况下不会killed，并提供通知向用户表明处于foreground状态。
            startForeground(968, myNotify)
        }
    }
}