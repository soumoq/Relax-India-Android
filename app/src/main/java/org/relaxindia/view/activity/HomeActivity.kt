package org.relaxindia.view.activity


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.sheet_home_dashboard.*
import org.relaxindia.R
import org.relaxindia.util.App
import org.relaxindia.util.toast
import org.relaxindia.viewModel.ApiCallViewModel


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

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        apiCallViewModel = ViewModelProvider(this).get(ApiCallViewModel::class.java)
        observeViewModel()


        //cart_view_home.setBackgroundResource(R.drawable.cart_view_top_radius)


        home_logout.setOnClickListener {
            val sp = applicationContext.getSharedPreferences("user_info", MODE_PRIVATE)
            val editor = sp.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
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
            }
            true
        }

        val homeDashboardSheet = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        homeDashboardSheet.setContentView(R.layout.sheet_home_dashboard)

        open_bottom_sheet.setOnClickListener {
            homeDashboardSheet.show()
        }

        homeDashboardSheet.book_now.setOnClickListener {
            val intent = Intent(this, BookNowActivity::class.java)
            startActivity(intent)
        }

    }

    private fun observeViewModel() {
        apiCallViewModel.profileInfo.observe(this, Observer {
            if (it.data.name == null) {
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivity(intent)
            } else {
                navHeader.nav_username.text = it.data.name
                navHeader.nav_phone.text = it.data.phone
                navHeader.nav_image.text = it.data.name.take(1)
            }
        })
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
            App.openLocationDialog(this, "Enable Location", App.locationAlert)
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