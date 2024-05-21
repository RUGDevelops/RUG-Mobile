package eu.virtusdevelops.backendapi

import com.google.gson.Gson
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {

    // todo: change this
    private const val BASE_URL = "http://192.168.1.69:8080/api/v1/"


    val api: ApiRequest by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiRequest::class.java)
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

}



