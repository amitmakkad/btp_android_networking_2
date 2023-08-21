package com.example.fourthapplication

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService {
    @Multipart
    @POST("upload")
    fun uploadImage(@Part image: MultipartBody.Part): Call<ApiResponse>

    @GET("server.json") // This is the GitHub Pages URL
    fun fetchGitHubBaseUrl(): Call<GitHubBaseUrlResponse>

    @Multipart
    @POST("uploadcs")
    fun uploadCsv(@Part file: MultipartBody.Part): Call<CsvResponse>
}
