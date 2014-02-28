package com.example.crowdmotoringdemo;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StopListAdapter extends ArrayAdapter<StopListElement>{
	public StopListAdapter(Context context, int textViewResourceId) {
	    super(context, textViewResourceId);
	}

	public StopListAdapter(Context context, int resource, List<StopListElement> items) {
	    super(context, resource, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View v = convertView;

	    if (v == null) {

	        LayoutInflater vi;
	        vi = LayoutInflater.from(getContext());
	        v = vi.inflate(R.layout.stop_list_element, null);
	        
	        if(position%2 == 0) v.setBackgroundColor(Color.parseColor(Constant.COLOR_WHITE));
		    else v.setBackgroundColor(Color.parseColor(Constant.COLOR_DARKER_WHITE));
	    }
	    
	    final StopListElement p = getItem(position);
	    
	    if(p != null){
	    	TextView stopNameText = (TextView) v.findViewById(R.id.stop_name);
	    	TextView stopDistanceText = (TextView) v.findViewById(R.id.stop_distance);
	    	
	    	DecimalFormat df = new DecimalFormat("#.##");
	    	
	    	stopNameText.setText(p.getName());
	    	stopDistanceText.setText("Distance: " + df.format(p.getDistance()) + " m");
	    }
	    
	    return v;

	}
}
