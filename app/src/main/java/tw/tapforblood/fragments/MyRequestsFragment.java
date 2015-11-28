package tw.tapforblood.fragments;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import tw.tapforblood.CustomAdapter;
import tw.tapforblood.helpers.Environment;

public class MyRequestsFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AsyncTask<View, Void, List<Map<String, String>>> execute = new RequestHandler().execute();
        try {
            setListAdapter(new CustomAdapter(this, execute.get()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

    private class RequestHandler extends AsyncTask<View, Void, List<Map<String, String>>> {

        @Override
        protected List<Map<String, String>> doInBackground(View... params) {

            String tap_for_blood_prefs = "TAP_FOR_BLOOD_PREFS";
            final SharedPreferences sharedPreferences = getActivity().getBaseContext().getSharedPreferences(tap_for_blood_prefs, 0);
            String userId = sharedPreferences.getString("user_id", "");

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet fetchAllRequests = new HttpGet(Environment.myRequestsForUser(userId));
            List<Map<String, String>> allRequests = new ArrayList<Map<String, String>>();

            try {
                HttpResponse response = httpClient.execute(fetchAllRequests);
                String responseBody = EntityUtils.toString(response.getEntity());
                JSONArray allRequestsJson = new JSONArray(responseBody);
                for (int i = 0; i < allRequestsJson.length(); i++) {
                    JSONObject object = allRequestsJson.getJSONObject(i);
                    String userJson = object.getString("user");
                    String requestJson = object.getString("request");
                    JSONObject user = new JSONObject(userJson);
                    JSONObject request = new JSONObject(requestJson);

                    HashMap<String, String> requestMap = new HashMap<String, String>();
                    String phone_number = user.getString("phone_number");
                    String encryptedNumber = phone_number.charAt(3) + phone_number.substring(4, 12)
                            .replaceAll(".", "x") + phone_number.charAt(phone_number.length()-1);
                    requestMap.put("phoneNumber", encryptedNumber);
                    requestMap.put("name", user.getString("name"));
                    requestMap.put("area", request.getString("area"));
                    requestMap.put("requestId", request.getString("id"));

                    allRequests.add(requestMap);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return allRequests;
        }
    }
}