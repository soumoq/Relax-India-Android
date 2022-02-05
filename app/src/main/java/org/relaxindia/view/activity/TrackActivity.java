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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.SphericalUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.relaxindia.R;
import org.relaxindia.model.firebaseModel.DriverProfileModel;
import org.relaxindia.service.location.FetchURL;
import org.relaxindia.service.location.TaskLoadedCallback;
import org.relaxindia.util.App;

import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MarkerOptions place1, place2, driverPlace;

    //Intent Val
    private Double fromLatitude = 0.0;
    private Double fromLongitude = 0.0;
    private Double toLatitude = 0.0;
    private Double toLongitude = 0.0;
    private String driverId = "";
    private String driverName = "";
    private String driverImage = "";
    private String fromLocation = "";
    private String toLocation = "";
    private String phone = "";


    //Firebase
    private DatabaseReference mDatabase;

    //view
    private GoogleMap mMap;
    private TextView etaTIme;
    private TextView viewTrackPhone;
    private TextView viewDriverName;
    private TextView viewFromLocation;
    private TextView viewToLocation;
    private CircleImageView viewDriverImage;
    private LinearLayout viewCustomerSupport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        etaTIme = findViewById(R.id.eta_time);
        viewTrackPhone = findViewById(R.id.track_phone);
        viewDriverName = findViewById(R.id.track_driver_name);
        viewFromLocation = findViewById(R.id.track_from_location);
        viewToLocation = findViewById(R.id.track_to_location);
        viewDriverImage = findViewById(R.id.track_driver_image);
        viewTrackPhone = findViewById(R.id.track_phone);
        viewCustomerSupport = findViewById(R.id.customer_support);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        fromLatitude = Double.valueOf(intent.getStringExtra("from_latitude"));
        fromLongitude = Double.valueOf(intent.getStringExtra("from_longitude"));
        toLatitude = Double.valueOf(intent.getStringExtra("to_latitude"));
        toLongitude = Double.valueOf(intent.getStringExtra("to_longitude"));
        driverId = intent.getStringExtra("driver_id");
        driverName = intent.getStringExtra("driver_name");
        driverImage = intent.getStringExtra("driver_image");
        fromLocation = intent.getStringExtra("from_location");
        toLocation = intent.getStringExtra("to_location");
        phone = intent.getStringExtra("driver_phone");

        viewDriverName.setText(driverName);
        viewFromLocation.setText(fromLocation);
        viewToLocation.setText(toLocation);
        viewTrackPhone.setText("Call: " + phone);

        viewTrackPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });

        viewCustomerSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "9874408080"));
                startActivity(intent);
            }
        });

        Glide.with(this)
                .load(driverImage)
                .into(viewDriverImage);


        //App.INSTANCE.openDialog(this, "T", fromLatitude + " : " + fromLongitude + " : " + toLatitude + " : " + toLongitude);

        //27.658143,85.3199503
        //27.667491,85.3208583
        place1 = new MarkerOptions().position(new LatLng(fromLatitude, fromLongitude)).title("Your location");
        place2 = new MarkerOptions().position(new LatLng(toLatitude, toLongitude)).title("Destination Location");
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(TrackActivity.this);


    }

    private void showCurvedPolyline(LatLng p1, LatLng p2, double k) {
        //Calculate distance and heading between two points
        double d = SphericalUtil.computeDistanceBetween(p1, p2);
        double h = SphericalUtil.computeHeading(p1, p2);

        //Midpoint position
        LatLng p = SphericalUtil.computeOffset(p1, d * 0.5, h);

        //Apply some mathematics to calculate position of the circle center
        double x = (1 - k * k) * d * 0.5 / (2 * k);
        double r = (1 + k * k) * d * 0.5 / (2 * k);

        LatLng c = SphericalUtil.computeOffset(p, x, h + 90.0);

        //Polyline options
        PolylineOptions options = new PolylineOptions();
        List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dash(30), new Gap(0));

        //Calculate heading between circle center and two points
        double h1 = SphericalUtil.computeHeading(c, p1);
        double h2 = SphericalUtil.computeHeading(c, p2);

        //Calculate positions of points on circle border and add them to polyline options
        int numpoints = 100;
        double step = (h2 - h1) / numpoints;

        for (int i = 0; i < numpoints; i++) {
            LatLng pi = SphericalUtil.computeOffset(c, r, h1 + i * step);
            options.add(pi);
        }

        //Draw polyline
        mMap.addPolyline(options.width(10).color(getResources().getColor(R.color.black)).geodesic(false).pattern(pattern));
    }

    public static void createDashedLine(GoogleMap map, LatLng latLngOrig, LatLng latLngDest, int color) {
        double difLat = latLngDest.latitude - latLngOrig.latitude;
        double difLng = latLngDest.longitude - latLngOrig.longitude;

        double zoom = map.getCameraPosition().zoom;
        int z = 10;

        double divLat = difLat / (zoom * z);
        double divLng = difLng / (zoom * z);

        LatLng tmpLatOri = latLngOrig;

        for (int i = 0; i < (zoom * z); i++) {
            LatLng loopLatLng = tmpLatOri;

            if (i > 0) {
                loopLatLng = new LatLng(tmpLatOri.latitude + (divLat * 0.25f), tmpLatOri.longitude + (divLng * 0.25f));
            }

            Polyline polyline = map.addPolyline(new PolylineOptions()
                    .add(loopLatLng)
                    .add(new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng))
                    .color(color)
                    .width(10f));

            tmpLatOri = new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng);
        }
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


//                        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
//                                .clickable(true)
//                                .add(new LatLng(fromLatitude, fromLongitude),
//                                        new LatLng(driverObj.getDouble("lat"), driverObj.getDouble("lon")))
//                                .width(15)
//                                .color(getResources().getColor(R.color.black)));

                        showCurvedPolyline(new LatLng(fromLatitude, fromLongitude), new LatLng(driverObj.getDouble("lat"), driverObj.getDouble("lon")), 0.5);
                        createDashedLine(googleMap, new LatLng(fromLatitude, fromLongitude), new LatLng(driverObj.getDouble("lat"), driverObj.getDouble("lon")), getResources().getColor(R.color.black));

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
                        String time = String.format("%.0f", estimatedDriveTimeInMinutes);
                        etaTIme.setText("Arriving in " + time + " minutes");

                    } catch (JSONException e) {
                        Toast.makeText(TrackActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    }


                }
            }
        });

        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(fromLatitude, fromLongitude))
                .zoom(13f)
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