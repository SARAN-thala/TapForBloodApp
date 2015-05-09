package tw.tapforblood.fragments;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import tw.tapforblood.CustomAdapter;

public class AllRequestsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<String> {
    String[] numbers_text = new String[] { "one", "two", "three", "four",
            "five", "six", "seven", "eight", "nine", "ten", "eleven",
            "twelve", "thirteen", "fourteen", "fifteen" };
    String[] numbers_digits = new String[] { "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "11", "12", "13", "14", "15" };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setListAdapter(new CustomAdapter(this, numbers_text));

        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:+919840692259"));
        startActivity(callIntent);

//        Toast.makeText(getActivity(), numbers_digits[(int) id], Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
