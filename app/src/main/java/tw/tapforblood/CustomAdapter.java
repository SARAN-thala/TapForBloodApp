package tw.tapforblood;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        ImageView img;
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
        holder.img=(ImageView) rowView.findViewById(R.id.phone_icon);

        holder.name.setText(result.get(position).get("name"));
        holder.area.setText(result.get(position).get("area"));
        holder.phoneNumber.setText(result.get(position).get("phoneNumber"));

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

}