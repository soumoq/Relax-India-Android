package org.relaxindia.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;

import org.relaxindia.R;
import org.relaxindia.service.location.FetchURL;
import org.relaxindia.service.location.TaskLoadedCallback;
import org.relaxindia.util.App;

public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    Button getDirection;
    private Polyline currentPolyline;

    //Intent Val
    private Double fromLatitude = 0.0;
    private Double fromLongitude = 0.0;
    private Double toLatitude = 0.0;
    private Double toLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Intent intent = getIntent();
        fromLatitude = Double.valueOf(intent.getStringExtra("from_latitude"));
        fromLongitude = Double.valueOf(intent.getStringExtra("from_longitude"));
        toLatitude = Double.valueOf(intent.getStringExtra("to_latitude"));
        toLongitude = Double.valueOf(intent.getStringExtra("to_longitude"));

        //App.INSTANCE.openDialog(this, "T", fromLatitude + " : " + fromLongitude + " : " + toLatitude + " : " + toLongitude);

        //27.658143,85.3199503
        //27.667491,85.3208583
        place1 = new MarkerOptions().position(new LatLng(fromLatitude, fromLongitude)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(toLatitude, toLongitude)).title("Location 2");
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        mMap.addMarker(place1);
        mMap.addMarker(place2);

        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(fromLatitude, fromLongitude))
                .zoom(12f)
                .bearing(0)
                .tilt(45)
                .build();

        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(fromLatitude, fromLongitude),
                        new LatLng(toLatitude, toLongitude)));

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(fromLatitude, fromLongitude), 4));

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null);
    }


}