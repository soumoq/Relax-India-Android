package org.relaxindia.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.GeoPoint;

import org.json.JSONException;
import org.json.JSONObject;
import org.relaxindia.R;
import org.relaxindia.model.firebaseModel.DriverProfileModel;
import org.relaxindia.service.location.FetchURL;
import org.relaxindia.service.location.TaskLoadedCallback;
import org.relaxindia.util.App;

public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MarkerOptions place1, place2, driverPlace;

    //Intent Val
    private Double fromLatitude = 0.0;
    private Double fromLongitude = 0.0;
    private Double toLatitude = 0.0;
    private Double toLongitude = 0.0;
    private String driverId = "";

    //Firebase
    private DatabaseReference mDatabase;

    //view
    private GoogleMap mMap;
    private TextView etaTIme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        etaTIme = findViewById(R.id.eta_time);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        fromLatitude = Double.valueOf(intent.getStringExtra("from_latitude"));
        fromLongitude = Double.valueOf(intent.getStringExtra("from_longitude"));
        toLatitude = Double.valueOf(intent.getStringExtra("to_latitude"));
        toLongitude = Double.valueOf(intent.getStringExtra("to_longitude"));
        driverId = intent.getStringExtra("driver_id");

        //App.INSTANCE.openDialog(this, "T", fromLatitude + " : " + fromLongitude + " : " + toLatitude + " : " + toLongitude);

        //27.658143,85.3199503
        //27.667491,85.3208583
        place1 = new MarkerOptions().position(new LatLng(fromLatitude, fromLongitude)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(toLatitude, toLongitude)).title("Location 2");
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(TrackActivity.this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        mMap.addMarker(place1);
        mMap.addMarker(place2).
                setIcon(bitmapDescriptorFromVector(R.drawable.ic_baseline_local_hospital_24));

        //Add driver
        mDatabase.child("driver_data").child(driverId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase_ADAS", "Error getting data", task.getException());
                } else {
                    Log.d("firebase_ADAS", String.valueOf(task.getResult().getValue()));
                    try {
                        JSONObject driverObj = new JSONObject(String.valueOf(task.getResult().getValue()));

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(driverObj.getDouble("lat"), driverObj.getDouble("lon")))
                                .title("This is my title")
                                .snippet("and snippet")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
                                .setIcon(bitmapDescriptorFromVector(R.drawable.amb_vec));


                        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                                .clickable(true)
                                .add(new LatLng(fromLatitude, fromLongitude),
                                        new LatLng(driverObj.getDouble("lat"), driverObj.getDouble("lon")))
                                .width(20)
                                .color(getResources().getColor(R.color.app_color)));

                        Location location1 = new Location("");
                        location1.setLatitude(fromLatitude);
                        location1.setLongitude(fromLongitude);

                        Location location2 = new Location("");
                        location2.setLatitude(driverObj.getDouble("lat"));
                        location2.setLongitude(driverObj.getDouble("lon"));

                        float distanceInMeters = location1.distanceTo(location2);

                        //For example spead is 10 meters per minute.
                        int speedIs10MetersPerMinute = 583;
                        float estimatedDriveTimeInMinutes = distanceInMeters / speedIs10MetersPerMinute;
                        String time = String.format("%.2f", estimatedDriveTimeInMinutes);
                        etaTIme.setText("Estimated time: " + time + " Minutes");

                    } catch (JSONException e) {
                        Toast.makeText(TrackActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    }


                }
            }
        });

        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(fromLatitude, fromLongitude))
                .zoom(20f)
                .bearing(0)
                .tilt(45)
                .build();


        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(fromLatitude, fromLongitude), 4));

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(this, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}