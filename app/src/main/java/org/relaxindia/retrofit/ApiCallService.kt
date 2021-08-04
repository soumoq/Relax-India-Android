package org.relaxindia.retrofit



import org.relaxindia.model.login.LoginResponse
import org.relaxindia.util.App
import retrofit2.Call
import retrofit2.http.*

interface ApiCallService {

    @FormUrlEncoded
    @POST(App.apiLogin)
    fun sendOtp(
        @Field("phone")
        phone: String
    ): Call<LoginResponse>




}