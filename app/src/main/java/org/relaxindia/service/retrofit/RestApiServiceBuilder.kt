package org.relaxindia.service.retrofit

import com.google.gson.*
import okhttp3.OkHttpClient
import org.relaxindia.util.App
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestApiServiceBuilder {
    private var retrofit: Retrofit? = null

    private fun getRetrofit(): Retrofit? {
        if (retrofit == null) {
            //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            val client = OkHttpClient.Builder() //.addInterceptor(logging)
                .build()
            val gson = GsonBuilder()
                .setLenient()
                .create()
            retrofit = Retrofit.Builder()
                .baseUrl(App.apiBaseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit
    }

    fun <T> buildService(serviceType: Class<T>?): T {
        return getRetrofit()!!.create(serviceType)
    }
}