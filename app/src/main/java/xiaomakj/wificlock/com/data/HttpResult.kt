package xiaomakj.wificlock.com.data

data class HttpResult<T>(val code: Int, val data: T, val msg: String)
