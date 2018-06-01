package xiaomakj.wificlock.com.api


import xiaomakj.wificlock.com.common.*
import xiaomakj.wificlock.com.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by MaQi on 2017/12/21.
 */
interface AppService {

    @POST(LOGIN)
    @FormUrlEncoded
    fun login(
            @Field("account") username: String,
            @Field("password") password: String,
            @Field("client") client: String = "android"
    ): Observable<HttpResult<LoginDatas>>


    @POST(REGISTER)
    @FormUrlEncoded
    fun register(
            @FieldMap mobile: HashMap<String, String>
    ): Observable<HttpResult<LoginDatas>>

    @POST(SENDCODE)
    @FormUrlEncoded
    fun sendCode(
            @Field("mobile") mobile: String,
            @Field("event") event: String = "",
            @Field("timeStamp") timeStamp: String = "" + (Calendar.getInstance().timeInMillis / 1000)
    ): Observable<HttpResult<Any>>

    @POST("FINDPWD")
    @FormUrlEncoded
    fun findPwd(
            @Field("mobile") mobile: String,
            @Field("mobilecode") mobilecode: String,
            @Field("password") password: String,
            @Field("client") client: String
    ): Observable<HttpResult<Any>>

    @Multipart
    @POST("UPLOADPIC")
    fun uploadPic(
            @Part("key") path: RequestBody,
            @Part file: MultipartBody.Part,
            @Part("client") client: RequestBody
    ): Observable<HttpResult<Any>>

    @Multipart
    @POST("ADDIMG")
    fun addImg(
            @Part("key") path: RequestBody,
            @Part file: MultipartBody.Part,
            @Part("client") client: RequestBody
    ): Observable<HttpResult<Any>>

    @Multipart
    @POST(UPLOAD)
    fun upload(
            @Header("token") token: String,
            @Part("key") path: RequestBody,
            @Part file: MultipartBody.Part,
            @Part("client") client: RequestBody
    ): Observable<HttpResult<Any>>

    @GET("GETFAVORITE")
    fun getFavorite(
            @Query("key") key: String,
            @Query("coordinate") coordinate: String,
            @Query("curpage") curpage: Int,
            @Query("page") page: Int,
            @Query("client") client: String = "android"
    ): Observable<HttpResult<Any>>

    @GET(TEST)
    fun getTEST(): Observable<HttpResult<List<TestDatas>>>


    @POST(ADDCLOCKRECORD)
    @FormUrlEncoded
    fun addClockRecord(
            @Field("admin_id") admin_id: String,
            @Field("clock_place") clock_place: String,
            @Field("lat") lat: Double,
            @Field("lon") lon: Double,
            @Field("wifiname") wifiname: String,
            @Field("wifi_distance") wifi_distance: Int,
            @Field("gps_distance") gps_distance: Int
    ): Observable<HttpResult<Any>>


    @POST(GETCLOCKRECORD)
    @FormUrlEncoded
    fun getClockRecord(
            @Field("admin_id") id: Int
    ): Observable<HttpResult<ClockRecordDatas>>

    @POST(CHANEGPROFILE)
    @FormUrlEncoded
    fun changeProfile(
            @Header("token") token: String,
            @Field("username") username: String,
            @Field("nickname") nickname: String,
            @Field("avatar") avatar: String,
            @Field("bio") bio: String = ""
    ): Observable<HttpResult<Any>>
}
