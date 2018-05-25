package xiaomakj.wificlock.com.data

/**
 * Created by Administrator on 2018/5/25.
 */
data class ClockRecordDatas(var total: Int,
                            var list: List<ClockList>) {
    data class ClockList(var id: Int,
                         var admin_id: Int,
                         var createtime: Long,
                         var clock_place: String,
                         var lat: Double,
                         var lon: Double,
                         var wifiname: String,
                         var wifi_distance: Int,
                         var gps_distance: Int)
}