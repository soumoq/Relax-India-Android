package org.relaxindia.viewModel

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.relaxindia.model.login.LoginData
import org.relaxindia.model.login.LoginResponse
import org.relaxindia.model.otp.OtpResponse
import org.relaxindia.retrofit.ApiCallService
import org.relaxindia.retrofit.RestApiServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiCallViewModel : ViewModel() {
    val LOG = "ApiCallViewModel"
    val loginInfo = MutableLiveData<LoginResponse>()
    val otpInfo = MutableLiveData<OtpResponse>()

    private val restApiService = RestApiServiceBuilder().buildService(ApiCallService::class.java)

    fun loginInfo(context: Context, phone: String) {
        val progressDialog = ProgressDialog(context)
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
        val progressDialog = ProgressDialog(context)
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

}