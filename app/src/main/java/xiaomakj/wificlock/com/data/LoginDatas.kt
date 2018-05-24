package xiaomakj.wificlock.com.data

import java.io.Serializable

/**
 * Created by Administrator on 2018/5/24.
 */
data class LoginDatas(var userinfo: Userinfo) {
    data class Userinfo(var id: Int,
                        var username: String,
                        var nickname: String,
                        var mobile: String,
                        var avatar: String,
                        var score: Int,
                        var token: String,
                        var user_id: Int,
                        var createtime: Int,
                        var expiretime: Int,
                        var expires_in: Int) : Serializable
}