package com.example.root.alertme;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        RoutingListener, ResultCallback<Status> {

    FloatingActionButton fab1, fab2;
    boolean isFABOpen = false;
    int count = 120;
    boolean checkDistanceUpdate = false;
    Place destination;
    LatLng sourceLatLng;
    GoogleMap map;
    ProgressDialog progressDialog;
    NotificationManager notificationManager;
    String globalProvider, bufferValue, userName;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark, R.color.primary, R.color.primary_light,
            R.color.accent, R.color.primary_dark_material_light};
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setEnabled(false);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(MainActivity.this);
        if (code == ConnectionResult.SUCCESS) {
        } else {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MainActivity.this);
                    startActivityForResult(i, 1);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                destination = PlaceAutocomplete.getPlace(this, data);
                map.addMarker(new MarkerOptions().position(destination.getLatLng()).title("Destination"));
                map.animateCamera(CameraUpdateFactory.newLatLng(destination.getLatLng()));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(MainActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Cancelled ", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                bufferValue = data.getStringExtra("bufferValue");
                userName = data.getStringExtra("name");
                checkForLocationChange();
                return;
            }
        }
        drawingPath();
    }

    protected void drawingPath() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Fetching route information.", true);

        Location lastKnownLocation = getLastKnownLocation();
        sourceLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        Routing routing = new Routing.Builder()
                .withListener(this)
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .alternativeRoutes(true)
                .waypoints(sourceLatLng, destination.getLatLng())
                .build();
        routing.execute();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, Settings.class);
            startActivityForResult(settings, 2);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_manage) {
            Intent settings = new Intent(this, Settings.class);
            startActivityForResult(settings, 2);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) throws SecurityException {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        map = googleMap;
        map.setMyLocationEnabled(true);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Create a criteria object to retrieve provider
        // Get the name of the best provider

        // Get Current Location
        Location myLocation = getLastKnownLocation();


        // Get latitude of the current location
        double latitude = myLocation.getLatitude();

        // Get longitude of the current location
        double longitude = myLocation.getLongitude();

        // Create a LatLng object for the current location
        sourceLatLng = new LatLng(latitude, longitude);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLng, 15));
/*
        PendingResult<PlaceLikelihoodBuffer> currentPlace = Places.PlaceDetectionApi.getCurrentPlace(this, null);
        currentPlace.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {

            }
        });
*/
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(false);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            //globalProvider = provider;
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
                globalProvider = provider;
            }
        }
        return bestLocation;
    }

    public void checkForLocationChange() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(globalProvider, 1000, 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
/*
                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(location.getLatitude(), location.getLongitude()));
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_close_light));
                map.addMarker(options);
*/
                Routing routing = new Routing.Builder()
                        .withListener(new LocationChangeResult())
                        .travelMode(AbstractRouting.TravelMode.DRIVING)
                        .alternativeRoutes(true)
                        .waypoints(new LatLng(location.getLatitude(), location.getLongitude()), destination.getLatLng())
                        .build();
                routing.execute();

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    private void showFABMenu() {
        isFABOpen = true;
        fab2.setEnabled(true);
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab2.setEnabled(false);
        fab2.animate().translationY(0);

    }


    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
        progressDialog.dismiss();
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(this, "Starting The routing Request!!!", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(sourceLatLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        map.moveCamera(center);


        polylines = new ArrayList<>();
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        //add route(s) to the map.
        for (int k = 0; k < route.size(); k++) {
            int colorIndex = k % COLORS.length;

            Route route1 = route.get(k);
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + k * 3);
            polyOptions.addAll(route.get(k).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);

            //double dst = route.get(k).getDistanceValue()*0.000621371;
            //double timeSec = (route.get(k).getDistanceValue() / (1000*60)) % 60;
            Notification notification = new Notification.Builder(MainActivity.this).setContentTitle("All Routes")
                    .setContentText("Route " + (k + 1) + ": distance " + route.get(k).getDistanceText() + ": duration " + route.get(k).getDurationText()).setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal).build();
            notification.flags |= Notification.FLAG_GROUP_SUMMARY;
            notificationManager.notify(k, notification);

            Toast.makeText(getApplicationContext(), "Route " + (k + 1) + ": distance " + route.get(k).getDistanceText() +
                    ": duration " + route.get(k).getDurationText(), Toast.LENGTH_SHORT).show();
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(sourceLatLng);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_close_light));
        map.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(destination.getLatLng());
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cast_off_light));
        map.addMarker(options);
        addGeoFence();
    }

    public void addGeoFence() {
        LatLng latLng = destination.getLatLng();
        Geofence geofence = new Geofence.Builder().setRequestId("first")
                .setCircularRegion(latLng.latitude + 5, latLng.longitude + 5, 10)
                .setExpirationDuration(10000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT).build();

    }

    @Override
    public void onRoutingCancelled() {

    }


    @Override
    public void onResult(@NonNull Status status) {

    }

    class LocationChangeResult implements RoutingListener, ResultCallback<Status> {
        @Override
        public void onRoutingFailure(RouteException e) {

        }

        @Override
        public void onRoutingStart() {

        }

        @Override
        public void onRoutingSuccess(ArrayList<Route> route, int i) {
            HashMap<String, Integer> distanceValues = new HashMap<>();
            int distanceValue = 0;
            for (int k = 0; k < route.size(); k++) {
                distanceValue = route.get(k).getDistanceValue();
                distanceValues.put(route.get(k).getDistanceText(), route.get(k).getDistanceValue());
            }
            Notification notification = new Notification.Builder(MainActivity.this).setContentTitle("Remaining Distance For Alert")
                    .setContentText(String.valueOf(distanceValue - Integer.parseInt(bufferValue))).setSmallIcon(R.drawable.quantum_ic_stop_grey600_36).build();
            notification.flags |= Notification.FLAG_GROUP_SUMMARY;
            notificationManager.notify(++count, notification);

        }

        @Override
        public void onRoutingCancelled() {

        }

        @Override
        public void onResult(@NonNull Status status) {

        }
    }

}
