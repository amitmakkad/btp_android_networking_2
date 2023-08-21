package com.example.fourthapplication

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private var BASE_URL = "https://amitmakkad.github.io/btp/" // Default server URL
    private var SERVER_BASE_URL = "" // ngrok server URL obtained from GitHub Pages
    private const val base_ip = "http://10.203.2.176:8000/" // local server ip

     val retrofit: Retrofit by lazy {      // for base url
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val serverRetrofit: Retrofit by lazy {     // for server base url
        Retrofit.Builder()
            .baseUrl(SERVER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val localRetrofit: Retrofit = Retrofit.Builder()    // for local server ip
        .baseUrl(base_ip)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getApiService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    fun fetchGitHubBaseUrl(callback: (String) -> Unit) {
        val apiService = retrofit.create(ApiService::class.java)
        apiService.fetchGitHubBaseUrl().enqueue(object : Callback<GitHubBaseUrlResponse> {
            override fun onResponse(call: Call<GitHubBaseUrlResponse>, response: Response<GitHubBaseUrlResponse>) {
                if (response.isSuccessful) {
                    SERVER_BASE_URL = response.body()?.serverUrl ?: "error 404";
                    callback(SERVER_BASE_URL);
                }
            }

            override fun onFailure(call: Call<GitHubBaseUrlResponse>, t: Throwable) {
                callback(SERVER_BASE_URL)
            }
        })
    }

    fun updateBaseUrl(newBaseUrl: String) {
        BASE_URL = newBaseUrl
    }
}
