package org.relaxindia.service.retrofit


import okhttp3.RequestBody
import org.relaxindia.model.GlobalResponse
import org.relaxindia.model.bookingHistory.BookingList
import org.relaxindia.model.driverList.DriverList
import org.relaxindia.model.getSelectedService.SelectedServiceResponse
import org.relaxindia.model.getService.ServiceResponse
import org.relaxindia.model.login.LoginResponse
import org.relaxindia.model.otp.OtpResponse
import org.relaxindia.model.saveBooking.SaveBooking
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

    @POST(App.API_PROFILE)
    fun profile(
        @Header("Authorization")
        authHeader: String,
    ): Call<ProfileResponse>

    @FormUrlEncoded
    @PATCH(App.API_PROFILE)
    fun updateProfile(
        @Header("Authorization")
        authHeader: String,
        @Field("name")
        name: String,
        @Field("email")
        email: String,
        @Field("address")
        address: String,
        @Field("pincode")
        pincode: String,
        @Field("device_token")
        device_token: String,
    ): Call<GlobalResponse>

    @FormUrlEncoded
    @POST(App.getService)
    fun getService(
        @Header("Authorization")
        authHeader: String,
        @Field("service_type")
        serviceType: String,
    ): Call<ServiceResponse>

    @Headers("Content-Type: application/json")
    @POST(App.getSelectedService)
    fun getSelectedService(
        @Header("Authorization")
        authHeader: String,
        @Body
        body: RequestBody
    ): Call<SelectedServiceResponse>

    @Headers("Content-Type: application/json")
    @POST(App.getSaveBooking)
    fun getSaveBooking(
        @Header("Authorization")
        authHeader: String,
        @Body
        body: RequestBody
    ): Call<SaveBooking>


    @FormUrlEncoded
    @PATCH(App.updateBooking)
    fun updateBooking(
        @Header("Authorization")
        authHeader: String,
        @Field("id")
        id: String,
        @Field("driver_id")
        driver_id: String,
    ): Call<GlobalResponse>


    @POST(App.getBookingHistory)
    fun getBookingHistory(
        @Header("Authorization")
        authHeader: String,
    ): Call<BookingList>

    @Headers("Content-Type: application/json")
    @POST(App.getAllDrivers)
    fun getDriverList(
        @Header("Authorization")
        authHeader: String,
    ): Call<DriverList>


}