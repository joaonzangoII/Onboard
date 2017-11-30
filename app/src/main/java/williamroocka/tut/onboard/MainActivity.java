package williamroocka.tut.onboard;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import williamroocka.tut.onboard.base.BaseActivity;
import williamroocka.tut.onboard.managers.Session;
import williamroocka.tut.onboard.models.Department;
import williamroocka.tut.onboard.models.Time;
import williamroocka.tut.onboard.models.User;
import williamroocka.tut.onboard.requests.AuthRequest;
import williamroocka.tut.onboard.utils.Constant;
import williamroocka.tut.onboard.utils.GeofenceTrasitionService;

public class MainActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        LocationListener,
        ResultCallback<Status> {
    //   GoogleMap.OnMarkerClickListener
    //  GoogleMap.OnMapClickListener,
    private static final String TAG = MainActivity.class.getSimpleName();
    private Session sessionManager;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private Marker locationMarker;
    private TextView textLat, textLong;
    private Circle geoFenceLimits;
    private User user;
    private Time time;
    private Department department;
    private LocationRequest locationRequest;
    private boolean isInGeofence;
    private PendingIntent geoFencePendingIntent;
    private AppCompatButton btnClockIn;

    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final int REQ_PERMISSION = 1;
    private final int UPDATE_INTERVAL = 3 * 60 * 1000; // 3 minutes
    private final int FASTEST_INTERVAL = 30 * 1000;  // 30 secs
    private final int GEOFENCE_REQ_CODE = 0;
    private static final long GEO_DURATION = 60 * 60 * 1000;

    public static Intent makeNotificationIntent(final Context applicationContext,
                                                final String msg) {
        return new Intent(MyApplication.mInstance, MainActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new Session(this);
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "User not yet activated", Toast.LENGTH_SHORT).show();
            logout(sessionManager);
        } else {
            btnClockIn = (AppCompatButton) findViewById(R.id.btn_clock_in);
            btnClockIn.setEnabled(false);
            btnClockIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    time = sessionManager.getTime();
                    if (time == null) {
                        AuthRequest.clockIn(
                                sessionManager,
                                MainActivity.this,
                                requestHandler);
                    } else {
                        AuthRequest.clockOut(
                                sessionManager,
                                MainActivity.this,
                                time.getId(),
                                requestHandler);
                    }
                }
            });

            final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                    findViewById(R.id.bottom_navigation);

            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_history:
                                    goToActivity(HistoryActivity.class);
                                    break;
                                case R.id.action_view_profile:
                                    goToActivity(MyProfileActivity.class);
                                    break;
                                case R.id.action_logout:
                                    logout(sessionManager);
                                    break;
                            }
                            return false;
                        }
                    });


            // realm.delete(GeofenceCoordinate.class);
            // fetchgeofencesAsync();
            // Defined in mili seconds.
            //final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            // setSupportActionBar(toolbar);
            user = sessionManager.getLoggedInUser();
            time = sessionManager.getTime();

            if (time == null) {
                btnClockIn.setText("CLOCK IN");
            } else {
                btnClockIn.setText("CLOCK OUT");
            }

            if (user == null) {
                Toast.makeText(this, "User not yet activated", Toast.LENGTH_SHORT).show();
                logout(sessionManager);
            } else if (user.getDepartment() == null) {
                Toast.makeText(this, "User not yet assigned a department", Toast.LENGTH_SHORT).show();
                logout(sessionManager);
            } else {
                department = user.getDepartment();
            }

            textLat = (TextView) findViewById(R.id.lat);
            textLong = (TextView) findViewById(R.id.lon);
            // initialize GoogleMaps
            initGMaps();
            // create GoogleApiClient
            createGoogleApi();
        }
    }

    private void initGMaps() {
        mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Callback called when Map is ready
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady()");
        map = googleMap;
        if (department == null) {
            Toast.makeText(this, "User not yet assigned a department", Toast.LENGTH_SHORT).show();
            logout(sessionManager);
        } else {
            final LatLng latLng = new LatLng(department.getLatitude(), department.getLongitude());
            final String title = department.getLatitude() + ", " + department.getLongitude();
            final Marker marker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .snippet(department.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            markerForGeofence(latLng);

            // map.setOnMapClickListener(this);
            // map.setOnMarkerClickListener(this);
        }
    }

    // Create GoogleApiClient instance
    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }

    // GoogleApiClient.ConnectionCallbacks suspended
    @Override
    public void onConnectionSuspended(final int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    // GoogleApiClient.OnConnectionFailedListener fail
    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed()");
    }

    // GoogleApiClient.ConnectionCallbacks connected
    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        Log.i(TAG, "onConnected()");
        getLastKnownLocation();
    }

    // Get last known location
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if (checkPermission()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        } else askPermission();
    }

    // Start location Updates
    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (checkPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    locationRequest,
                    this);
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        Log.d(TAG, "onLocationChanged [" + location + "]");
        lastLocation = location;
        writeActualLocation(location);
        if (department == null) {
            Toast.makeText(this, "User not yet assigned a department", Toast.LENGTH_SHORT).show();
            logout(sessionManager);
        } else {
            final LatLng latlng = new LatLng(department.getLatitude(), department.getLongitude());
            startGeofence(latlng, department.getRadius());
            isInGeofence = false;

            final Location geofenceLoc = new Location("");
            geofenceLoc.setLatitude(department.getLatitude());
            geofenceLoc.setLongitude(department.getLongitude());

            final float distance = geofenceLoc.distanceTo(lastLocation);
            if (distance < department.getRadius()) {
                isInGeofence = true;
            }

            Log.e("DISTANCE", distance + "");

            if (!isInGeofence) {
                btnClockIn.setEnabled(false);
                //logoutUserAfterTime(); // logout user if he is not in a geofence
            } else {
                btnClockIn.setEnabled(true);
            }
        }

    }


    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PERMISSION
        );
    }

    // Verify user's response of the permission requested
    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    getLastKnownLocation();

                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }

    // App cannot work without the permissions
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
    }

    // Write location coordinates on UI
    private void writeActualLocation(final Location location) {
        textLat.setText(String.format("Lat: %s", location.getLatitude()));
        textLong.setText(String.format("Long: %s", location.getLongitude()));
        markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    // Create a Location Marker
    private void markerLocation(final LatLng latLng) {
        Log.i(TAG, "markerLocation(" + latLng + ")");
        final String title = latLng.latitude + ", " + latLng.longitude;
        if (map != null) {
            // Remove the anterior marker
            if (locationMarker != null) {
                locationMarker.remove();
            }

            locationMarker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .snippet("My Current Location")
                    .title(title));
            //markerForGeofence(latLng);

            final float zoom = 14f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            map.animateCamera(cameraUpdate);
        }
    }

    // Create a marker for the geofence creation
    private void markerForGeofence(final LatLng latLng) {
        Log.i(TAG, "markerForGeofence(" + latLng + ")");
        final String title = latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        final MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title);
        if (map != null) {
            // Remove last geoFenceMarker
            //if (geoFenceMarker != null) {
            //geoFenceMarker.remove();
            //}
            map.addMarker(markerOptions);
        }
    }

    // Create a Geofence
    private Geofence createGeofence(final LatLng latLng,
                                    final float radius) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(GEO_DURATION)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }


    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest(final Geofence geofence) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private PendingIntent createGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;

        final Intent intent = new Intent(this, GeofenceTrasitionService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(final GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull final Status status) {
        Log.i(TAG, "onResult: " + status);
        if (status.isSuccess()) {
            LatLng latLng = new LatLng(department.getLatitude(), department.getLongitude());
            drawGeofence(latLng, department.getRadius());
        } else {
            // inform about fail
        }
    }


    private void drawGeofence(final LatLng geo,
                              final float radius) {
        Log.d(TAG, "drawGeofence()");

        if (geoFenceLimits != null)
            geoFenceLimits.remove();
        final CircleOptions circleOptions = new CircleOptions()
                .center(geo)
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(radius);
        geoFenceLimits = map.addCircle(circleOptions);
    }


    // Start Geofence creation process
    private void startGeofence(final LatLng geo,
                               final float radius) {
        Log.i(TAG, "startGeofence()");
        if (geo != null) {
            final Geofence geofence = createGeofence(geo, radius);
            final GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
            addGeofence(geofenceRequest);
        } else {
            Log.e(TAG, "Geofence marker is null");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    final Handler requestHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            final Bundle data = message.getData();
            final boolean isLoggedIn = data.getBoolean(Constant.KEY_IS_LOGGED_IN);
            final boolean success = data.getBoolean(Constant.KEY_SUCCESS);
            if (success) {

                if (data.getInt(Constant.KEY_CLOCK) == 1) {
                    btnClockIn.setText("CLOCK OUT");
                } else {
                    btnClockIn.setText("CLOCK IN");
                }

            }
            return false;
        }
    });
}
