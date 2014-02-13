package com.example.crowdmotoringdemo;

import java.util.List;

import android.content.ClipData.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class StopViewListAdapter extends ArrayAdapter<StopViewListElement> {

		public StopViewListAdapter(Context context, int textViewResourceId) {
		    super(context, textViewResourceId);
		}

		public StopViewListAdapter(Context context, int resource, List<StopViewListElement> items) {
		    super(context, resource, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

		    View v = convertView;

		    if (v == null) {

		        LayoutInflater vi;
		        vi = LayoutInflater.from(getContext());
		        v = vi.inflate(R.layout.stop_view_list_element, null);

		    }

		    StopViewListElement p = getItem(position);

		    if (p != null) {
		    	
		        // Fill with your own
		    }

		    return v;

		}
}
