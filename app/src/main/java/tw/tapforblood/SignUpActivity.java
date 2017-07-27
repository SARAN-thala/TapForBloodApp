package tw.tapforblood;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import tw.tapforblood.fragments.DatePickerFragment;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SignUpActivity extends Activity
        implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener, LocationListener {

    private static final int TAG_CODE_PERMISSION_LOCATION = 1;
    private LocationManager locationManager;
    private String lastDonatedDate;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> bloodGroups = Arrays.asList("A+", "O+", "B+", "AB+", "A-", "O-", "B-", "AB-", "Whats your blood group?");
        Spinner spinner = (Spinner) findViewById(R.id.blood_group);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, bloodGroups) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
                    ((TextView) v.findViewById(android.R.id.text1)).setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; // you dont display last item. It is used as hint.
            }

        };

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.blood_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt("Whats your blood group?");
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
        spinner.setOnItemSelectedListener(this);

        setLocation();
    }

    public static boolean isLocationEnabled(Context context) {
        //...............
        return true;
    }

    protected void setLocation() {
        if (isLocationEnabled(this)) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED && checkSelfPermission(ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
                String bestProvider = String.valueOf(locationManager.getBestProvider(new Criteria(), true));
                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null) {
                    Log.e("TAG", "GPS is on");
                    Toast.makeText(this, "location is: " + location, Toast.LENGTH_SHORT).show();
//                searchNearestPlace(voice2text);
                    this.location  =  location;
                }
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            } else {
                this.requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, TAG_CODE_PERMISSION_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                setLocation();
            } else {
                // User refused to grant permission. You can add AlertDialog here
                Toast.makeText(this, "You didn't give permission to access device location", Toast.LENGTH_LONG).show();
//                startInstalledAppDetailsActivity();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    public void submitForm(View view) {
        View spinner = findViewById(R.id.loading_spinner);
        spinner.setVisibility(View.VISIBLE);
        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setEnabled(false);
        AsyncTask<View, Void, String> execute = new RequestHandler().execute(view);

    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        EditText lastDonatedAtEditText = (EditText) findViewById(R.id.last_donated_at);
        lastDonatedAtEditText.setText(day + " - " + (month + 1) + " - " + year);
        lastDonatedDate = year + "-" + (month + 1) + "-" + day;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
        this.location = location;

        Toast.makeText(this, "location: " + location, Toast.LENGTH_SHORT).show();
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

        private String userId = "";


        @Override
        protected String doInBackground(View... views) {

            String name = ((EditText) findViewById(R.id.name)).getText().toString();
            String phoneNumber = ((EditText) findViewById(R.id.phone_number_prefix)).getText().toString() + ((EditText) findViewById(R.id.phone_number)).getText().toString();
            String bloodGroup = ((Spinner) findViewById(R.id.blood_group)).getSelectedItem().toString();

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(tw.tapforblood.helpers.Environment.createUser());
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("name", name);
                jsonObject.put("phone_number", phoneNumber);
                jsonObject.put("blood_group", bloodGroup);
                jsonObject.put("last_donated", lastDonatedDate);
                jsonObject.put("latitude", location.getLatitude());
                jsonObject.put("longitude", location.getLongitude());
                jsonObject.put("registration_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

                StringEntity se = new StringEntity(jsonObject.toString());
                se.setContentType("application/json");
                post.setEntity(se);
                HttpResponse response = httpClient.execute(post);
                Log.d("TAG", response.getStatusLine().getStatusCode() + "");
                if (response.getStatusLine().getStatusCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    userId = new JSONObject(responseBody).getString("user_id");
                    System.out.println("created");
                    return "OK";
                } else {
                    return "ERROR";
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

        @Override
        protected void onPostExecute(String s) {
            String name = ((EditText) findViewById(R.id.name)).getText().toString();
            String phoneNumber = ((EditText) findViewById(R.id.phone_number_prefix)).getText().toString() + ((EditText) findViewById(R.id.phone_number)).getText().toString();

            View spinner = findViewById(R.id.loading_spinner);
            spinner.setVisibility(View.GONE);
            Button submitButton = (Button) findViewById(R.id.submit);
            submitButton.setEnabled(true);
            if (s.equals("OK")) {
                SharedPreferences.Editor preferenceEditor = getBaseContext().getSharedPreferences("TAP_FOR_BLOOD_PREFS", MODE_PRIVATE).edit();
                preferenceEditor.putString("phoneNumber", phoneNumber);
                preferenceEditor.putString("name", name);
                preferenceEditor.putString("user_id", userId);

                preferenceEditor.commit();

                Toast.makeText(getBaseContext(), "Sign up successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getBaseContext(), "Something went wrong. Please try again later!", Toast.LENGTH_LONG).show();
            }


        }
    }
}