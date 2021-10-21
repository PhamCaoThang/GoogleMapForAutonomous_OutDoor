package com.itshareplus.googlemapdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itshareplus.googlemapdemo.data.ServerManager;
//import com.itshareplus.googlemapdemo.data.ServerManager;

import java.io.UnsupportedEncodingException;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.FirebaseHelper;
import Modules.Point;
import Modules.Route;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private Button btnFindPath,btnSendData, btnClear;
    private EditText etOrigin;
    private EditText etDestination;

    private List<Marker> RealtimeMarker = new ArrayList<>();
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private List<Route> routes;
    private String temp;

    private String pointA;

    // Add new feature : Get GPS of Car from firebase to App
    private Button btnStart;
    private TextView tvCurrentPos;
    DatabaseReference RefCurrentPost;

    DatabaseReference RefState;
    String currentPos1;
    String state;

    boolean firstStart = false;
    boolean newRoute = true;
    double preLat = 0;
    double preLon = 0;

    //TRACKING FEATURE VARIABLE ===============================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        btnSendData= findViewById(R.id.btnSendData);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnClear = (Button) findViewById(R.id.btnClear);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etDestination = (EditText) findViewById(R.id.etDestination);
        tvCurrentPos = (TextView) findViewById(R.id.tvCurrentPos);

        // Add new feature : Get GPS of Car from firebase to App
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefCurrentPost = FirebaseDatabase.getInstance().getReference().child("currentPos");
                final DatabaseReference RefState1 = FirebaseDatabase.getInstance().getReference().child("state");

                if(firstStart){
                    etOrigin.setText(pointA);
                }

                RefState1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        state = snapshot.getValue().toString();
                        // xe chua chay, gan Current Post cho diem A de lam diem bat dau

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                RefCurrentPost.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        pointA = dataSnapshot.getValue().toString();
                        Log.d("log1", "data changed");
                        firstStart = true;
                        //etOrigin.setText(currentPos1);
                            if(state.equals("-1")){
                                if(newRoute == true && firstStart == true) {
                                    etOrigin.setText(pointA);
                                    newRoute = false;
                                }
                                //etOrigin.setText(pointA);
                            }
                        tvCurrentPos.setText(pointA);

                        // PHUC
                        try {
                            tvCurrentPos.setText(pointA);
                            String[] latlong = etOrigin.getText().toString().split(",");  // DIEM A
                            String[] latlong_debug = tvCurrentPos.getText().toString().split(","); // TRACKING

                            double latitude = Double.parseDouble(latlong[0]);  //ADD MARKER
                            double longitude = Double.parseDouble(latlong[1]);
                            //Debug
                            double latitude_debug = Double.parseDouble(latlong_debug[0]);  //ADD MARKER Debug
                            double longitude_debug = Double.parseDouble(latlong_debug[1]);

                            //mMap.addPolyline(polylineOptions_tracking);
                            if(preLon != 0 && preLat != 0) {
                                Polyline polyline_tracking = mMap.addPolyline(new PolylineOptions()
                                        .add(new LatLng(preLat, preLon), new LatLng(latitude_debug, longitude_debug)).width(10).color(Color.RED));
                            }
                            preLat = latitude_debug;
                            preLon = longitude_debug;

                            LatLng location = new LatLng(latitude, longitude);
                            //Debug
                            LatLng location_debug = new LatLng(latitude_debug, longitude_debug);

                            MarkerOptions markerOptions = new MarkerOptions().position(location);
                            MarkerOptions markerOptions_debug = new MarkerOptions().position(location_debug);
                            if (originMarkers.size() == 0) {
                                // Add origin
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
                                Marker marker = mMap.addMarker(markerOptions);

                                marker.setTitle("origin");
                                originMarkers.add(marker);

                                //RealtimeMarker.add(marker_debug);

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));

                                //etOrigin.setText(latLng.latitude+","+latLng.longitude);
                            }
//                            if (RealtimeMarker.size() == 0) {
                                if(true){
                                // Add origin
                                markerOptions_debug.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
                                Marker marker_debug = mMap.addMarker(markerOptions_debug);
                                Log.d("log2", "add marker");
                                marker_debug.setTitle("Tracking");
                                RealtimeMarker.add(marker_debug);

                                //DRAW POLYLINE
//                                    PolylineOptions polylineOptions_tracking = new PolylineOptions().
//                                            geodesic(true).
//                                            color(Color.RED).
//                                            width(10);
//                                    for (int i = 0; i < RealtimeMarker.size() ; i++)
//                                    {
//                                        polylineOptions_tracking.add(location_debug);
//                                    }
//                                    mMap.addPolyline(polylineOptions_tracking);
//                                    Polyline polyline_tracking = mMap.addPolyline(new PolylineOptions()
//                                    .add(new LatLng(10.883392, 106.780269), new LatLng(10.883209, 106.780427)).width(10).color(Color.RED));
                                    Log.d("LogTracking", "Polylinetracking");

                            }
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(MapsActivity.this, "Location is wrong!",
                                Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        ServerManager.getInstance().getData(new ServerManager.IServerManagerGetData() {
            @Override
            public void OnGetDataSuccess(List<String> data) {
            }

            @Override
            public void OnGetDataFail(String error) {

            }
        });
        btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefState = FirebaseDatabase.getInstance().getReference().child("state");
                if(routes!=null&&routes.size()>0){
                    ServerManager.getInstance().putData(routes, new ServerManager.IServerManagerPutData() {
                        @Override
                        public void OnPutDataSuccess() {

                        }

                        @Override
                        public void OnPutDataFail(String error) {
                            //Log.d("taaaaa",error);

                        }
                    });
                    FirebaseHelper.PutDataToFirebase(temp);
                    RefState.push().setValue(0); //SEND STATE == 0
                }
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etOrigin.setText("");
                etDestination.setText("");
                originMarkers.clear();
                destinationMarkers.clear();
                polylinePaths.clear();
                mMap.clear();
            }
        });
    }

    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng hcmus = new LatLng(10.883111500819666, 106.78172512249931); // Show first address is KTX khu B
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));

        //TRACKING
        //fetchLocationUpdates();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                if(marker.getTitle().equals("origin")){
                    originMarkers.clear();
                }else {
                    destinationMarkers.clear();
                }
                return false;
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                if(originMarkers.size()==0){
                    // Add origin
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
                    Marker marker =mMap.addMarker(markerOptions);
                    marker.setTitle("origin");
                    originMarkers.add(marker);
                    etOrigin.setText(latLng.latitude+","+latLng.longitude);
                }else if(originMarkers.size()==1&&originMarkers.get(0).getTitle().equals("origin")){
                    if(destinationMarkers.size()==1&&destinationMarkers.get(0).getTitle().equals("destination")){
                        destinationMarkers.get(0).remove();
                        destinationMarkers.clear();
                    }else {
                        for (Marker maker:destinationMarkers) {
                            maker.remove();
                        }
                        destinationMarkers.clear();
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
                        Marker marker =mMap.addMarker(markerOptions);
                        marker.setTitle("destination");
                        destinationMarkers.add(marker);
                        etDestination.setText(latLng.latitude+","+latLng.longitude);
                    }
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
                    Marker marker =mMap.addMarker(markerOptions);
                    marker.setTitle("destination");
                    destinationMarkers.add(marker);
                    etDestination.setText(latLng.latitude+","+latLng.longitude);
                    // Phuc
                    newRoute = false;
                }else {
                    for (Marker maker:originMarkers) {
                        maker.remove();
                    }
                    originMarkers.clear();
                    for (Polyline polyline:polylinePaths) {
                        polyline.remove();
                    }
                    polylinePaths.clear();
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
                    Marker marker =mMap.addMarker(markerOptions);
                    marker.setTitle("origin");
                    originMarkers.add(marker);
                    etOrigin.setText(latLng.latitude+","+latLng.longitude);
                }

            }
        });
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        this.routes=routes;
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
            this.temp = ToStringListPoints(route.ListPoint);
            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }


    //FORMAT STRING SEND TO FIREBASE
    public String ToStringListPoints(List<Point> List_Points)
    {
        String temp1 = "";
        for (Point point :List_Points)
        {
            temp1 += point.toString() + "m";
        }
        return temp1;
    }
}
