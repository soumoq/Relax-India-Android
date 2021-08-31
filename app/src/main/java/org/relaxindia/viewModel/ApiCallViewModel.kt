package org.relaxindia.viewModel

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import org.json.JSONObject
import org.relaxindia.model.GlobalResponse
import org.relaxindia.model.getSelectedService.SelectedServiceResponse
import org.relaxindia.model.getService.ServiceResponse
import org.relaxindia.model.login.LoginResponse
import org.relaxindia.model.otp.OtpResponse
import org.relaxindia.model.userProfile.ProfileResponse
import org.relaxindia.service.retrofit.ApiCallService
import org.relaxindia.service.retrofit.RestApiServiceBuilder
import org.relaxindia.util.App
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiCallViewModel : ViewModel() {
    val LOG = "ApiCallViewModel"
    val loginInfo = MutableLiveData<LoginResponse>()
    val otpInfo = MutableLiveData<OtpResponse>()
    val profileInfo = MutableLiveData<ProfileResponse>()
    val updateProfile = MutableLiveData<GlobalResponse>()
    val getService = MutableLiveData<ServiceResponse>()
    val getSelectedService = MutableLiveData<SelectedServiceResponse>()

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

        val response: Call<OtpResponse> = restApiService.checkOtp(phone, otp)
        response.enqueue(object : Callback<OtpResponse> {
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

    fun profileInfo(context: Context) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait we are fetching profile info")
        progressDialog.show()

        val response: Call<ProfileResponse> = restApiService.profile(App.getUserToken(context))
        response.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Log.e("$LOG-profileInfo-if", "Success")
                    profileInfo.value = response.body()
                } else {
                    Log.e("$LOG-profileInfo-else", "Error")

                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-profileInfo-onFailure", "${t}")
            }

        })

    }

    fun updateProfileInfo(
        context: Context,
        name: String,
        email: String,
        address: String,
        pinCode: String
    ) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait we are updating your profile")
        progressDialog.show()

        val response: Call<GlobalResponse> =
            restApiService.updateProfile(App.getUserToken(context), name, email, address, pinCode)
        response.enqueue(object : Callback<GlobalResponse> {
            override fun onResponse(
                call: Call<GlobalResponse>,
                response: Response<GlobalResponse>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Log.e("$LOG-updateProfileInfo-if", "success")
                    updateProfile.value = response.body()
                } else {
                    Log.e("$LOG-updateProfileInfo-else", "else")
                }
            }

            override fun onFailure(call: Call<GlobalResponse>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-updateProfileInfo-onFailure", "${t}")

            }

        })

    }

    fun serviceInfo(context: Context, serviceType: String = "Main Service") {
        Log.e("$LOG-serviceInfo-value", "${App.getUserToken(context)}\t$serviceType")
        val response: Call<ServiceResponse> =
            restApiService.getService(App.getUserToken(context), serviceType)
        response.enqueue(object : Callback<ServiceResponse> {
            override fun onResponse(
                call: Call<ServiceResponse>,
                response: Response<ServiceResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("$LOG-serviceInfo-if", "success")
                    getService.value = response.body()
                } else {
                    Log.e("$LOG-serviceInfo-else", "error")
                }
            }

            override fun onFailure(call: Call<ServiceResponse>, t: Throwable) {
                Log.e("$LOG-serviceInfo-onFailure: ", t.message.toString())

            }

        })
    }

    fun selectedServiceInfo(
        context: Context,
        body: String,
        contentType: String = "application/json",
        accept: String = "application/json"
    ) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait we are calculating total")
        progressDialog.show()
        val str = "{\n" +
                "    \"service\" : [1,2]\n" +
                "}"
        Log.e("$LOG-selectedServiceInfo", App.getUserToken(context))

        val response: Call<SelectedServiceResponse> =
            restApiService.getSelectedService(App.getUserToken(context), str)
        response.enqueue(object : Callback<SelectedServiceResponse> {
            override fun onResponse(
                call: Call<SelectedServiceResponse>,
                response: Response<SelectedServiceResponse>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Log.e("$LOG-selectedServiceInfo-if", "success")
                    getSelectedService.value = response.body()
                } else {
                    Log.e("$LOG-selectedServiceInfo-else", "error ${response.code()}")

                }
            }

            override fun onFailure(call: Call<SelectedServiceResponse>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-selectedServiceInfo-onFailure: ", t.message.toString())

            }

        })
    }

}