package org.relaxindia.viewModel

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.relaxindia.model.login.LoginData
import org.relaxindia.model.login.LoginResponse
import org.relaxindia.model.otp.OtpResponse
import org.relaxindia.model.userProfile.ProfileResponse
import org.relaxindia.retrofit.ApiCallService
import org.relaxindia.retrofit.RestApiServiceBuilder
import org.relaxindia.util.App
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiCallViewModel : ViewModel() {
    val LOG = "ApiCallViewModel"
    val loginInfo = MutableLiveData<LoginResponse>()
    val otpInfo = MutableLiveData<OtpResponse>()
    val profileInfo = MutableLiveData<ProfileResponse>()

    lateinit var progressDialog: ProgressDialog

    private val restApiService = RestApiServiceBuilder().buildService(ApiCallService::class.java)

    fun loginInfo(context: Context, phone: String) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait will send you a otp")
        progressDialog.show()

        val response: Call<LoginResponse> = restApiService.sendOtp(phone)
        response.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    loginInfo.value = response.body()
                    Log.e("$LOG-loginInfo-if", "Success")

                } else {
                    Log.e("$LOG-loginInfo-else", "Else")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-loginInfo-onFailure", "${t.message}")
            }
        })


    }

    fun otpInfo(context: Context, phone: String, otp: String) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait we are verifying your OTP")
        progressDialog.show()

        val response: Call<OtpResponse> = restApiService.checkOtp(phone,otp)
        response.enqueue(object :Callback<OtpResponse>{
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    otpInfo.value = response.body()
                    Log.e("$LOG-otpInfo-if", "Success")

                } else {
                    Log.e("$LOG-otpInfo-else", "Else")
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-otpInfo-onFailure", "${t.message}")

            }

        })


    }

    fun profileInfo(context: Context){
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait we are fetching profile info")
        progressDialog.show()

        val response: Call<ProfileResponse> = restApiService.profile(App.getUserToken(context))
        response.enqueue(object : Callback<ProfileResponse>{
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                progressDialog.dismiss()
                if (response.isSuccessful){
                    Log.e("$LOG-profileInfo-if", "Success")
                    profileInfo.value = response.body()
                }else{
                    Log.e("$LOG-profileInfo-else", "Error")

                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-profileInfo-onFailure", "${t}")
            }

        })

    }

}