package com.keno.getlocation.api

import com.keno.getlocation.model.CurrentLocation
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("test/LocationTest")
    fun postData(
        @Body location: CurrentLocation,
        @Header("apiKey") apiKey: String = "V0NzTXozNWdLSUY0dUROL3J0MzgrTDJIaU5xSDk4TlVhRis0clVESEJaRT0"
    ): Call<LocationResponse>
}