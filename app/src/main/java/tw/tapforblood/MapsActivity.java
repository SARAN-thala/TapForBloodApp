package tw.tapforblood;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import tw.tapforblood.R;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    Context context = this.getApplicationContext();
    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String registrationId = null;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        registrationId = registerCloudMessaging(this.getApplicationContext());

        Log.d("here",registrationId);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) this);


    }

    String registerCloudMessaging(Context context) {


        // repeated calls to this method will return the same registration ID
        // a new registration is needed if the app is updated or backup & restore happens
        String registrationId = null;
        try {
            registrationId = gcm.register("380431285352");
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Pass to server

        // then uploads the registration ID to your server
        return registrationId;
    }



    @Override
    public void onLocationChanged(Location location) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("My current location"));
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     * Copy (x,y)51.503363051° 30' 12.11'' N
     Longitude:Copy (x°,y°)-0.12762500° 7' 39.45'' W

     */
    private void setUpMap() {
        /*Focussing map in a particular city*/
        LatLng NewYork= new LatLng(13.060422,80.249583);
        CameraPosition camPos = new CameraPosition.Builder().target(NewYork).zoom(14).build();
        CameraUpdate cam = CameraUpdateFactory.newCameraPosition(camPos);
        mMap.animateCamera(cam);

        /*getting Latlong from given loction*/

        getLatLongFromAddress("Royapuram");
        getLatLongFromAddress("T Nagar");
        getLatLongFromAddress("Guindy");
//        getLatLongFromAddress("Velachery");
        getLatLongFromAddress("Choolaimedu");

        /*getting latlong of current location */
        LocationManager lm = (LocationManager) this.getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        Location location = lm
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location != null){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("My current location"));
        }

    }

    private void getLatLongFromAddress(String address)
    {
        double lat= 0.0, lng= 0.0;

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try
        {
            List<Address> addresses = geoCoder.getFromLocationName(address , 1);
            if (addresses.size() > 0)
            {
                lat = addresses.get(0).getLatitude();
                lng = addresses.get(0).getLongitude();
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(addresses.get(0).getSubLocality()));

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

