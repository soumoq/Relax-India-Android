package org.relaxindia.retrofit


import org.relaxindia.model.login.LoginResponse
import org.relaxindia.model.otp.OtpResponse
import org.relaxindia.model.userProfile.ProfileResponse
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

    @FormUrlEncoded
    @POST(App.apiCheckOtp)
    fun checkOtp(
        @Field("phone")
        phone: String,
        @Field("otp")
        otp: String
    ): Call<OtpResponse>

    @FormUrlEncoded
    @POST(App.apiProfile)
    fun profile(
        @Field("Authorization")
        authorization: String,
    ): Call<ProfileResponse>


}