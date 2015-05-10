package tw.tapforblood;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import tw.tapforblood.R;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String value;
    Context context;
    private JSONObject bloodResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("myObject");
            bloodResponse = new Gson().fromJson(value, JSONObject.class);
        }
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
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
//        mMap.setMyLocationEnabled(true);
        JSONObject response = null;
        JSONObject active_request = null;
        LatLng NewYork= new LatLng(13.060422,80.249583);
        final HashMap<String,String> donorNameNumberMap = new HashMap<>();

        try {
            JSONObject bloodResponse1 = new JSONObject(value);
            JSONObject bloodResponse = (JSONObject) bloodResponse1.get("nameValuePairs");

            JSONObject active_request1 = (JSONObject) bloodResponse.get("active_request");
            active_request = (JSONObject) active_request1.get("nameValuePairs");
            String latitude1 = active_request.getString("latitude");
            String longitude1 = active_request.getString("longitude");
            LatLng currentPoint1 = new LatLng(new Double(latitude1),new Double(longitude1));
            CameraPosition camPos = new CameraPosition.Builder().target(currentPoint1).zoom(14).build();
            CameraUpdate cam = CameraUpdateFactory.newCameraPosition(camPos);
            mMap.animateCamera(cam);
            String userId = active_request.getString("user_id");

            mMap.addMarker(new MarkerOptions().position(currentPoint1).title(userId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

            JSONObject requests1 = bloodResponse.getJSONObject("responses");
            JSONArray requests = requests1.getJSONArray("values");



            for(int i=0;i<requests.length();i++){
                 response = requests.getJSONObject(i);
                JSONObject res2 = response.getJSONObject("nameValuePairs");
                String latitude = res2.getString("latitude");
                String longitude = res2.getString("longitude");
                LatLng currentPoint = new LatLng(new Double(latitude),new Double(longitude));
                String nameOfDonor = res2.getString("name");
                String phoneNumber = res2.getString("phone_number");
                donorNameNumberMap.put(nameOfDonor,phoneNumber);
                mMap.addMarker(new MarkerOptions().position(currentPoint).title(nameOfDonor)).showInfoWindow();

            }
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker arg0) {
                    String phoneNumber = donorNameNumberMap.get(arg0.getTitle());
                    if (phoneNumber != null) {
                        //Make a call
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + phoneNumber));
                        startActivity(callIntent);
                        return true;
                    }
//
                    return false;
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(NewYork).title(value));
    }
}

