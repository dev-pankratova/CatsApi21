package com.project.catsapi21

import android.telecom.Call
import com.project.catsapi21.model.CatsList
import retrofit2.http.GET
import retrofit2.http.Query

interface CatsModelApi {

    @GET("v1/images/search/")
    fun loadCats(
        @Query("api_key") apiKey: String?,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?
    ): Call<ArrayList<CatsList>>
}