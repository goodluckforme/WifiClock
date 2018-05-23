package xiaomakj.wificlock.com.data

/**
 * Created by Administrator on 2018/5/23.
 */
data class WifiParams(val admin_id: String,
                      val clock_place: String,
                      val lat: Double,
                      val lon: Double,
                      val wifiname: String,
                      val wifi_distance: Int,
                      val gps_distance: Int
)