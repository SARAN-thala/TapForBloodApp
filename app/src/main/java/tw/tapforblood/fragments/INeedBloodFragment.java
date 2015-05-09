package tw.tapforblood.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import tw.tapforblood.MapsActivity;
import tw.tapforblood.R;

public class INeedBloodFragment extends Fragment implements View.OnClickListener,LocationListener {
    Location location;
    String bloodGroup;
    String userId;
    String area;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.blood_selector, container, false);
        ImageButton b_minus = (ImageButton) rootView.findViewById(R.id.b_minus);
        b_minus.setOnClickListener(this);
        ImageButton b_plus = (ImageButton) rootView.findViewById(R.id.b_plus);
        b_plus.setOnClickListener(this);
        ImageButton ab_minus = (ImageButton) rootView.findViewById(R.id.ab_minus);
        ab_minus.setOnClickListener(this);
        ImageButton ab_plus = (ImageButton) rootView.findViewById(R.id.ab_plus);
        ab_plus.setOnClickListener(this);
        ImageButton a_minus = (ImageButton) rootView.findViewById(R.id.a_minus);
        a_minus.setOnClickListener(this);
        ImageButton a_plus = (ImageButton) rootView.findViewById(R.id.a_plus);
        a_plus.setOnClickListener(this);
        ImageButton o_minus = (ImageButton) rootView.findViewById(R.id.o_minus);
        o_minus.setOnClickListener(this);
        ImageButton o_plus = (ImageButton) rootView.findViewById(R.id.o_plus);
        o_plus.setOnClickListener(this);
        this.location = getLastKnownLocation();
        String tap_for_blood_prefs = "TAP_FOR_BLOOD_PREFS";
        final SharedPreferences sharedPreferences = getActivity().getBaseContext().getSharedPreferences(tap_for_blood_prefs, 0);
        this.userId = sharedPreferences.getString("user_id", "");
        return rootView;
    }

    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        Geocoder geoCoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geoCoder.getFromLocation(bestLocation.getLatitude(),bestLocation.getLongitude(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.area = addresses.get(0).getSubLocality();


        return bestLocation;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_minus:
                setBloodGroup("B-");
                break;
            case R.id.b_plus:
                setBloodGroup("B+");
                break;
            case R.id.ab_plus:
                setBloodGroup("AB+");
                break;
            case R.id.ab_minus:
                setBloodGroup("AB-");
                break;
            case R.id.a_plus:
                setBloodGroup("A+");
                break;
            case R.id.a_minus:
                setBloodGroup("A-");
                break;
            case R.id.o_plus:
                setBloodGroup("O+");
                break;
            case R.id.o_minus:
                setBloodGroup("O-");
                break;
        }

    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
        AsyncTask<View, Void, String> execute = new RequestHandler().execute();
    }

    @Override
    public void onLocationChanged(Location location) {

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

    private class RequestHandler extends AsyncTask<View, Void, String> {

        @Override
        protected String doInBackground(View... views) {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(tw.tapforblood.helpers.Environment.createBloodRequest());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", userId);
                jsonObject.put("blood_group", bloodGroup);

                jsonObject.put("area",area);
                jsonObject.put("latitude", location.getLatitude());
                jsonObject.put("longitude", location.getLongitude());

                StringEntity se = new StringEntity(jsonObject.toString());
                se.setContentType("application/json");
                post.setEntity(se);
                HttpResponse response = httpClient.execute(post);
                Log.d("TAG", response.getStatusLine().getStatusCode() + "");
                if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
                    System.out.println("created");

                    HttpGet get = new HttpGet(tw.tapforblood.helpers.Environment.getResponsesUrl(userId));
                    HttpResponse blooddonorresponses = httpClient.execute(get);
                    JSONObject bloodDonorDetailsJSON = getJSONObject(blooddonorresponses);
                    Intent in = getActivity().getIntent();

                    System.out.println(blooddonorresponses);
                    Intent j = new Intent(getActivity().getBaseContext(), MapsActivity.class);
                    startActivity(j);

                    return "created";
                } else {
                    return "Not created";
                }




            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ClientProtocolException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            return null;
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
}
