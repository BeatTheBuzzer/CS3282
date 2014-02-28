package com.example.crowdmotoringdemo;

import java.util.List;

import com.example.crowdmotoringdemo.R;

import android.content.ClipData.Item;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class StopViewListAdapter extends ArrayAdapter<StopViewListElement>{
		String stopId;

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
		        
		        if(position%2 == 0) v.setBackgroundColor(Color.parseColor(Constant.COLOR_WHITE));
			    else v.setBackgroundColor(Color.parseColor(Constant.COLOR_DARKER_WHITE));
		    }

		    final StopViewListElement p = getItem(position);

		    if (p != null) {
		    	TextView transportName = (TextView) v.findViewById(R.id.transport_name);
		    	TextView arrivalTimeText = (TextView) v.findViewById(R.id.arrival_time_text);
		    	TextView crowdednessInfo = (TextView) v.findViewById(R.id.crowdedness_info);
		    	
		    	transportName.setText(p.getTransportName());
		    	if(p.getArrivalTimeMin() < 2) arrivalTimeText.setText("Soon");
		    	else arrivalTimeText.setText("in " + p.getArrivalTimeMin() + " mins");
		    	System.out.println(p.getCrowdedness());
		    	if(p.getCrowdedness() == Constant.CROWDEDNESS_TRUE){
		    		crowdednessInfo.setText("Crowded");
		    	}
		    	else if (p.getCrowdedness() == Constant.CROWDEDNESS_FALSE){
		    		crowdednessInfo.setText("Uncrowded");
		    	}
		    	else if (p.getCrowdedness() == Constant.CROWDEDNESS_NO_DATA){
		    		crowdednessInfo.setText("No Data");
		    	}
		    }
		    
		    return v;

		}
}
