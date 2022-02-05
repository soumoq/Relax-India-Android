package org.relaxindia.viewModel

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.MediaType
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
import okhttp3.RequestBody
import org.relaxindia.model.bookingHistory.BookingList
import org.relaxindia.model.driverList.DriverList
import org.relaxindia.model.saveBooking.SaveBooking
import org.relaxindia.util.toast


class ApiCallViewModel : ViewModel() {
    val LOG = "ApiCallViewModel"
    val loginInfo = MutableLiveData<LoginResponse>()
    val otpInfo = MutableLiveData<OtpResponse>()
    val profileInfo = MutableLiveData<ProfileResponse>()
    val updateProfile = MutableLiveData<GlobalResponse>()
    val getService = MutableLiveData<ServiceResponse>()
    val getSelectedService = MutableLiveData<SelectedServiceResponse>()
    val getSaveService = MutableLiveData<SaveBooking>()
    val updateBooking = MutableLiveData<GlobalResponse>()
    val getBookingHistory = MutableLiveData<BookingList>()
    val getDriverList = MutableLiveData<DriverList>()

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
                    App.openDialog(context, "Error", "Something went wrong with otp.")
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

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            val deviceToken = it
            val response: Call<GlobalResponse> =
                restApiService.updateProfile(
                    App.getUserToken(context),
                    name,
                    email,
                    address,
                    pinCode,
                    deviceToken
                )

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

    fun selectedServiceInfo(context: Context, jsonStr: String) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait we are calculating total")
        progressDialog.show()

        Log.e("ALL_SERVICE_JSON",jsonStr)

        val body: RequestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            JSONObject(jsonStr).toString()
        )

        val response: Call<SelectedServiceResponse> =
            restApiService.getSelectedService(App.getUserToken(context), body)
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


    fun saveServiceInfo(context: Context, jsonStr: String) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait we saving your booking")
        progressDialog.show()

        val body: RequestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            JSONObject(jsonStr).toString()
        )

        val response: Call<SaveBooking> =
            restApiService.getSaveBooking(App.getUserToken(context), body)
        response.enqueue(object : Callback<SaveBooking> {
            override fun onResponse(
                call: Call<SaveBooking>,
                response: Response<SaveBooking>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Log.e("$LOG-saveServiceInfo-if", "success")
                    getSaveService.value = response.body()
                } else {
                    Log.e("$LOG-saveServiceInfo-else", "error ${response.code()}")

                }
            }

            override fun onFailure(call: Call<SaveBooking>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-saveServiceInfo-onFailure: ", t.message.toString())
            }

        })

    }

    fun updateBookingInfo(context: Context, id: String) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait we updating your booking")
        progressDialog.show()

        val response: Call<GlobalResponse> =
            restApiService.updateBooking(App.getUserToken(context), id, "1")
        response.enqueue(object : Callback<GlobalResponse> {
            override fun onResponse(
                call: Call<GlobalResponse>,
                response: Response<GlobalResponse>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Log.e("$LOG-updateBookingInfo-if", "success")
                    updateBooking.value = response.body()
                } else {
                    progressDialog.dismiss()
                    Log.e("$LOG-updateBookingInfo-else", "error ${response.code()}")

                }
            }

            override fun onFailure(call: Call<GlobalResponse>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-updateBookingInfo-onFailure: ", t.message.toString() + "\t$id")
            }

        })

    }


    fun getBookingInfo(context: Context) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait we saving your booking")
        progressDialog.show()

        val response: Call<BookingList> =
            restApiService.getBookingHistory(App.getUserToken(context))
        response.enqueue(object : Callback<BookingList> {
            override fun onResponse(call: Call<BookingList>, response: Response<BookingList>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Log.e("$LOG-getBookingInfo-if", "success")
                    getBookingHistory.value = response.body()
                } else {
                    Log.e("$LOG-getBookingInfo-else", "error ${response.code()}")

                }
            }

            override fun onFailure(call: Call<BookingList>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-getBookingInfo-onFailure: ", t.message.toString())
            }

        })

    }


    fun getDriverListInfo(context: Context) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Please wait we saving your booking")
        progressDialog.show()


        val response: Call<DriverList> =
            restApiService.getDriverList(App.getUserToken(context))

        response.enqueue(object : Callback<DriverList> {
            override fun onResponse(
                call: Call<DriverList>,
                response: Response<DriverList>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Log.e("$LOG-getDriverListInfo-if", "success")
                    getDriverList.value = response.body()
                } else {
                    progressDialog.dismiss()
                    Log.e("$LOG-getDriverListInfo-else", "error ${response.code()}")

                }
            }

            override fun onFailure(call: Call<DriverList>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("$LOG-getDriverListInfo-onFailure: ", t.message.toString())
            }

        })

    }

}