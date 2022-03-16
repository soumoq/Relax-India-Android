package org.relaxindia.view.activity


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.sheet_home_dashboard.*
import org.relaxindia.R
import org.relaxindia.model.getService.ServiceData
import org.relaxindia.service.location.GpsTracker
import org.relaxindia.util.App
import org.relaxindia.view.recyclerView.ServiceAdapter
import org.relaxindia.viewModel.ApiCallViewModel
import android.location.Geocoder
import android.net.Uri
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import org.relaxindia.service.VollyApi
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.sheet_booking_list.*
import org.relaxindia.model.SupportList
import org.relaxindia.view.recyclerView.SupportListAdapter
import com.google.gson.Gson
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.json.JSONObject
import org.relaxindia.model.HospitalList
import org.relaxindia.view.recyclerView.HospitalListAdapter
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.security.auth.callback.Callback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

import androidx.annotation.NonNull

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback


class HomeActivity : AppCompatActivity(), OnMapReadyCallback {


    //location
    private var currentLocation: Location? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val REQUEST_CODE = 101
    private lateinit var supportMapFragment: SupportMapFragment

    //view-model
    lateinit var apiCallViewModel: ApiCallViewModel

    //nav-header
    lateinit var navHeader: View

    //Bottom sheet
    lateinit var homeDashboardSheet: BottomSheetDialog

    //adapter
    private lateinit var serviceAdapter: ServiceAdapter

    //Service Price
    var servicePrice: Double = 0.0
    var serviceId = -1
    var shortDec = ""
    var serviceName = ""

    var sourceLocation = ""
    var desLocation = ""
    var sourceLat = ""
    var sourceLon = ""
    var desLat = ""
    var desLon = ""

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.e("DEVICE_TOLEN", it)
            VollyApi.updateDeviceToken(this, it)
        }

        //Buttom sheet
        homeDashboardSheet = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        homeDashboardSheet.setContentView(R.layout.sheet_home_dashboard)

        open_bottom_sheet.setOnClickListener {
            homeDashboardSheet.show()
        }

        homeDashboardSheet.book_now.setOnClickListener {
            val intent = Intent(this, BookNowActivity::class.java)
            intent.putExtra("service_price", servicePrice.toString())
            intent.putExtra("service_id", serviceId.toString())
            intent.putExtra("service_name", serviceName)
            intent.putExtra("short_description", shortDec)
            intent.putExtra("source_loc", sourceLocation)
            intent.putExtra("des_loc", desLocation)
            intent.putExtra("sourceLat", sourceLat)
            intent.putExtra("sourceLon", sourceLon)
            intent.putExtra("desLat", desLat)
            intent.putExtra("desLon", desLon)
            startActivity(intent)
        }


        val gpsTracker = GpsTracker(this)
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address?> =
            geocoder.getFromLocation(gpsTracker.latitude, gpsTracker.longitude, 1);
        /*
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();pickup_address
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        */
        if (addresses.isNotEmpty()) {
            sourceLocation = addresses[0]?.getAddressLine(0).toString()
        } else {
            sourceLocation = "No Address Found"
        }
        sourceLat = gpsTracker.latitude.toString()
        sourceLon = gpsTracker.longitude.toString()

        val apiKey = App.googleApiKey
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        Places.createClient(this)
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment!!.setPlaceFields(
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        )
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                //Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
                //Toast.makeText(getApplicationContext(),"Success" + place.getName(),Toast.LENGTH_LONG).show();
                val queriedLocation = place.latLng
                val place1 = place.address.toString()
                getSearch(
                    queriedLocation?.latitude.toString(),
                    queriedLocation?.longitude.toString(),
                    place1
                )
            }

            override fun onError(status: Status) {
                Log.e("SEARCH_ERROR", status.toString())
            }
        })

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        observeViewModel()

        //cart_view_home.setBackgroundResource(R.drawable.cart_view_top_radius)


        home_logout.setOnClickListener {
            VollyApi.updateDeviceToken(this, "")
        }


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()


        navHeader = nav_view.getHeaderView(0)
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {

                }
                R.id.menu_your_booking -> {
                    val intent = Intent(this, MyBookingActivity::class.java)
                    startActivity(intent)
                }
                R.id.menu_profile -> {
                    val intent = Intent(this, MyProfileActivity::class.java)
                    startActivity(intent)
                }
                R.id.menu_transactions -> {
                    val intent = Intent(this, TransactionsActivity::class.java)
                    startActivity(intent)
                }
                R.id.menu_schedule_booking -> {
                    val intent = Intent(this, ScheduleBookingActivity::class.java)
                    startActivity(intent)
                }
                R.id.menu_support -> {
                    startActivity(Intent(this, SupportActivity::class.java));
                }
                R.id.menu_support_list -> {
                    VollyApi.getSupportList(this)
                }
                R.id.menu_guide -> {
                    VollyApi.getGuide(this)
                }
                R.id.menu_store -> {
                    val httpIntent = Intent(Intent.ACTION_VIEW)
                    httpIntent.data = Uri.parse(App.STORE_URL)
                    startActivity(httpIntent)
                }
                R.id.menu_share -> {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "https://relaxindia.org/")
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            }
            true
        }

        val myLoc = GpsTracker(this)
        VollyApi.getHospital(
            this,
            myLoc?.latitude.toString(),
            myLoc?.longitude.toString()
        )


    }

    fun logout() {
        val sp = applicationContext.getSharedPreferences("user_info", MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun getSearch(lat: String, lon: String, place: String) {
        desLat = lat
        desLon = lon
        homeDashboardSheet.select_ambulance_layout.visibility = View.VISIBLE
        apiCallViewModel.serviceInfo(this@HomeActivity)
        homeDashboardSheet.show()
        desLocation = place
        homeDashboardSheet.sheet_des.text = desLocation
        homeDashboardSheet.sheet_pickup.text = sourceLocation
        //toast("$desLocation : $sourceLocation")
        VollyApi.saveSearch(this@HomeActivity, sourceLocation, desLocation)
    }

    private val hospitalListAdapter = HospitalListAdapter(this)
    fun getHospital(hospitalList: ArrayList<HospitalList>, click: Boolean = false) {
        if (hospitalList.size > 0) {
            nh_layout.visibility = View.VISIBLE
            hospital_list.adapter = hospitalListAdapter
            hospitalListAdapter.updateData(hospitalList)
        } else {
            nh_layout.visibility = View.GONE
        }
    }

    fun getClickHospital(pos: Int) {
        hospitalListAdapter.updateClick(pos)
    }

    private fun observeViewModel() {
        apiCallViewModel.profileInfo.observe(this, Observer {
            if (it.data.name == null) {
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivity(intent)
            } else {
                val sp = getSharedPreferences("user_info", Context.MODE_PRIVATE)
                val editor = sp.edit()
                editor.putString(App.preferenceUserId, it.data.id)
                editor.putString(App.preferenceUserPhone, it.data.phone)
                editor.putString(App.preferenceUserEmail, it.data.email)
                editor.putString(App.preferenceUserName, it.data.name)
                editor.commit()

                navHeader.nav_username.text = it.data.name
                navHeader.nav_phone.text = it.data.phone
                navHeader.nav_image.text = it.data.name.take(1)
            }
        })

        apiCallViewModel.getService.observe(this, Observer {
            if (!it.error) {
                serviceAdapter = ServiceAdapter(this@HomeActivity)
                homeDashboardSheet.service_recycler_view.adapter = serviceAdapter
                serviceAdapter.updateData(it.data)
            }
        })
    }

    fun changeBackGround(
        position: Int,
        price: Double,
        serviceId: Int,
        serviceName: String,
        shortDescription: String
    ) {
        servicePrice = price * 100
        this.serviceId = serviceId
        this.serviceName = serviceName
        this.shortDec = shortDescription
        homeDashboardSheet.book_now.visibility = View.VISIBLE
        val serviceInfo = ArrayList<ServiceData>()
        serviceInfo.addAll(apiCallViewModel.getService.value?.data!!)
        for (i in 0 until serviceInfo.size) {
            serviceInfo[i].select = false
        }
        serviceInfo[position].select = true
        apiCallViewModel.getService.value!!.data = serviceInfo
        serviceAdapter.notifyDataSetChanged()
    }

    fun getSupportList(objList: ArrayList<SupportList>) {
        val bookingListSheet = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        bookingListSheet.setContentView(R.layout.sheet_booking_list)
        bookingListSheet.title_sheet.text = "Support List"
        bookingListSheet.show()

        bookingListSheet.back_sheet_schedule.setOnClickListener {
            bookingListSheet.dismiss()
        }

        val supportListAdapter = SupportListAdapter(this)
        bookingListSheet.schedule_booking_list.adapter = supportListAdapter
        supportListAdapter.updateData(objList)


    }

    fun startVideoPlayActivity(url: String) {
        val builder = AlertDialog.Builder(this)
        //val videoPlayer = VideoView(this)
        //builder.setView(videoPlayer)
        //videoPlayer.setVideoPath(url)
        //videoPlayer.start()
        val youtube = YouTubePlayerView(this)
        lifecycle.addObserver(youtube)
        youtube.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(url, 0f)
            }
        })
        builder.setView(youtube)

        // add a button
        builder.setPositiveButton("Close", DialogInterface.OnClickListener { dialog, which ->

        })

        val dialog = builder.create()
        dialog.show()
    }


    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            return
        }
        val task = fusedLocationProviderClient!!.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                //Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                supportMapFragment =
                    (supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment?)!!
                //requireActivity().supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment
                assert(supportMapFragment != null)
                supportMapFragment!!.getMapAsync(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.clear()
        val handler = Handler()
        handler.post {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        }
        val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)

        FirebaseDatabase.getInstance().reference.child("driver_data")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val timeList = ArrayList<Int>()
                    for (ds in dataSnapshot.children) {
                        var getObject: Object = ds.getValue(Object::class.java)!!
                        val objectStr: String = Gson().toJson(getObject)
                        val jsonObject = JSONObject(objectStr)
                        if (jsonObject.getBoolean("locationActive") && jsonObject.getBoolean("online")) {
                            val place = MarkerOptions().position(
                                LatLng(
                                    jsonObject.getDouble("lat"),
                                    jsonObject.getDouble("lon")
                                )
                            ).title("Ambulance")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))

                            googleMap.addMarker(place)
                                .setIcon(
                                    App.bitmapDescriptorFromVector(
                                        R.drawable.amb_vec,
                                        this@HomeActivity
                                    )
                                )

                            Log.e("C_LAT_LON", "${currentLocation!!.latitude}")
                            val location1 = Location("")
                            location1.latitude = currentLocation!!.latitude
                            location1.longitude = currentLocation!!.longitude

                            val location2 = Location("")
                            location2.latitude = jsonObject.getDouble("lat")
                            location2.longitude = jsonObject.getDouble("lon")

                            val time = App.calculateTime(location1, location2)

                            timeList.add(time.toInt())

                        }
                    }

                    val timeListSort = timeList.stream().sorted().collect(Collectors.toList())
                    timeListSort.forEach(Consumer {
                        Log.e("ASDDDW", "$it")
                    })
                    if (timeListSort.size > 0) {
                        if (timeListSort.get(0) <= 0) {
                            eta_nearest_ambulance.text = "${1} Mins"
                        } else {
                            eta_nearest_ambulance.text = "${timeListSort.get(0)} Mins"
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(16f).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        apiCallViewModel.profileInfo(this)
        if (App.isLocationEnabled(this)) {
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this)
            fetchLocation()
        } else {
            //App.openLocationDialog(this, "Enable Location", App.locationAlert)
        }
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }


}