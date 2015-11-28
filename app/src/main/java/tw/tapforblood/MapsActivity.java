package tw.tapforblood;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String value;
    private String userId;
    private String bloodReqId;
    Context context;
    private Bundle extras;
    private JSONObject bloodResponse;
    View mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extras = getIntent().getExtras();
        paintMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timer autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        paintMap();
                    }
                });
            }
        }, 0, 4000);

    }


    public void paintMap() {
        System.out.println("************************************ Painting map");
        if (extras != null) {
            userId = extras.getString("userId");
            bloodReqId = extras.getString("bloodRequestId");
//            bloodResponse = new Gson().fromJson(value, JSONObject.class);
        }

        View view = findViewById(R.id.maps_activity_layout);
        if(view != null) {
            ViewGroup vg = (ViewGroup) (view.getParent());
            vg.removeView(view);
            setContentView(view);
        }
        else {
            setContentView(R.layout.activity_maps);
        }
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
                setUpMap();
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

            AsyncTask<View, Void, JSONObject> execute = new RequestHandler().execute();
            JSONObject bloodDonorDetailsJSON = execute.get();
//            JSONObject bloodResponse = (JSONObject) bloodDonorDetailsJSON.get("nameValuePairs");

            JSONObject active_request1 = (JSONObject) bloodDonorDetailsJSON.get("active_request");
//            active_request = (JSONObject) active_request1.get("nameValuePairs");
            String latitude1 = active_request1.getString("latitude");
            String longitude1 = active_request1.getString("longitude");
            LatLng currentPoint1 = new LatLng(new Double(latitude1),new Double(longitude1));
            CameraPosition camPos = new CameraPosition.Builder().target(currentPoint1).zoom(14).build();
            CameraUpdate cam = CameraUpdateFactory.newCameraPosition(camPos);
            mMap.animateCamera(cam);
            String userId = active_request1.getString("user_id");

            mMap.addMarker(new MarkerOptions().position(currentPoint1).title(userId).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

            JSONArray requests = (JSONArray) bloodDonorDetailsJSON.get("responses");
            System.out.println(requests.length());

            for(int i=0;i<requests.length();i++){
                 response = requests.getJSONObject(i);
                String latitude = response.getString("latitude");
                String longitude = response.getString("longitude");
                LatLng currentPoint = new LatLng(new Double(latitude),new Double(longitude));
                String nameOfDonor = response.getString("name");
                System.out.println(nameOfDonor);
                String phoneNumber = response.getString("phone_number");
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private JSONObject getJSONObject(HttpResponse blooddonorresponses) throws IOException {
        StringBuilder builder = new StringBuilder();
        JSONObject jsonObject = null;
        HttpEntity entity = blooddonorresponses.getEntity();
        InputStream content = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        String line;
        while((line = reader.readLine()) != null){
            builder.append(line);
        }
        try{
            jsonObject = new JSONObject(builder.toString());

        } catch(Exception e){e.printStackTrace();}
        return jsonObject;
    }

    private class RequestHandler extends AsyncTask<View, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(View... views) {
            DefaultHttpClient httpClient = new DefaultHttpClient();

            HttpGet get = new HttpGet(tw.tapforblood.helpers.Environment.getResponsesUrl(userId, bloodReqId));
            HttpResponse bloodDonorResponses = null;
            try {
                bloodDonorResponses = httpClient.execute(get);
                return getJSONObject(bloodDonorResponses);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }
    }

}

