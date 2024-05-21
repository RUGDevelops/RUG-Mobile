package eu.virtusdevelops.backendapi

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

}

