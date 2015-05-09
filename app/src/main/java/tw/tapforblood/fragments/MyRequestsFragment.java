package tw.tapforblood.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import tw.tapforblood.CustomAdapter;
import tw.tapforblood.R;

public class MyRequestsFragment extends ListFragment {
    String[] numbers_text = new String[] { "one", "two", "three", "four",
            "five" };
    String[] numbers_digits = new String[] { "1", "2", "3", "4", "5" };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setListAdapter(new CustomAdapter(this, numbers_text));

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(getActivity(), numbers_digits[(int) id], Toast.LENGTH_SHORT).show();
    }

}
