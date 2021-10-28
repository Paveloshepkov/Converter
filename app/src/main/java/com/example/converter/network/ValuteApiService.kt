package com.example.converter.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://www.cbr.ru/scripts/XML_daily.asp/"

interface ValuteApiService {
    @GET("XML_daily.asp")
    suspend fun getValCurs(): NetworkValuteContainer
}

object ValuteNetwork {
    private val retrofit = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val getValCurs: ValuteApiService = retrofit.create(ValuteApiService::class.java)
}
