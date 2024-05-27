package eu.virtusdevelops.backendapi

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {

    // todo: change this
    private const val BASE_URL = "https://rug-be.virtus.lol/api/v1/"
//    private const val BASE_URL = "http://192.168.1.69:8080/api/v1/"
    private var cookieValue: String? = null


    val api: ApiRequest by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiRequest::class.java)
    }


    fun setCookie(cookieValue: String) {
        this.cookieValue = cookieValue
    }

    fun getCookie(): String?{
        return cookieValue
    }

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(CookieInterceptor("auth_sid"))
            .build()
    }


    inline fun <reified T> Response<*>.getErrorResponse(): T?{
        val gson = Gson()
        val response = errorBody()?.string()
        if(response != null){
            try{
                return gson.fromJson(response, T::class.java)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        return null
    }

    class CookieInterceptor(private val cookieName: String?) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val request = chain.request().newBuilder()
            if (cookieName != null && cookieValue != null) {
                request.addHeader("Cookie", "$cookieName=$cookieValue")
                println("Cookie: $cookieValue")
            }
            return chain.proceed(request.build())
        }
    }

}



