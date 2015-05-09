package tw.tapforblood;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import tw.tapforblood.fragments.AllRequestsFragment;

public class CustomAdapter extends BaseAdapter {
    List<Map<String, String>> result;
    Context context;
    private static LayoutInflater inflater=null;
    public CustomAdapter(ListFragment listFragment, List<Map<String, String>> maps) {
        // TODO Auto-generated constructor stub
        result=maps;
        context=listFragment.getActivity();
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView name;
        TextView phoneNumber;
        TextView area;
        TextView hiddenRequestId;
        ImageButton acceptButton;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.request, null);
        holder.name=(TextView) rowView.findViewById(R.id.name);
        holder.phoneNumber=(TextView) rowView.findViewById(R.id.phone_number);
        holder.area=(TextView) rowView.findViewById(R.id.area);
        holder.acceptButton=(ImageButton) rowView.findViewById(R.id.accept_icon);
        holder.hiddenRequestId=(TextView) rowView.findViewById(R.id.hidden_request_id);

        holder.name.setText(result.get(position).get("name"));
        holder.area.setText(result.get(position).get("area"));
        holder.phoneNumber.setText(result.get(position).get("phoneNumber"));
        holder.hiddenRequestId.setText(result.get(position).get("requestId"));

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<View, Void, String> execute = new PostHandler(position).execute(v);
                v.setBackground(context.getResources().getDrawable(R.drawable.check_mark_green_circle));
            }


        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + result.get(position).get("phoneNumber")));
                context.startActivity(callIntent);
//                Toast.makeText(context, "You Clicked " + result[position], Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }

    private class PostHandler extends AsyncTask<View, Void, String>{

        private final int position;

        public PostHandler(int position) {
           this.position = position;
        }

        @Override
        protected String doInBackground(View... params) {
            View view = params[0];
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String tap_for_blood_prefs = "TAP_FOR_BLOOD_PREFS";
            final SharedPreferences sharedPreferences = context.getSharedPreferences(tap_for_blood_prefs, 0);
            String userId = sharedPreferences.getString("user_id", "");
            HttpPost post = new HttpPost(tw.tapforblood.helpers.Environment.acceptRequestByUser(result.get(position).get("requestId"),userId ));
            try {
                HttpResponse httpResponse = httpClient.execute(post);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "OK" ;
        }
    }

}