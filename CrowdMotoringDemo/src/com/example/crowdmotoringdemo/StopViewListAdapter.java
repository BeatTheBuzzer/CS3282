package com.example.crowdmotoringdemo;

import java.util.List;

import com.example.crowdmotoringdemo.R;

import android.content.ClipData.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class StopViewListAdapter extends ArrayAdapter<StopViewListElement> {
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
		    }

		    final StopViewListElement p = getItem(position);

		    if (p != null) {
		    	TextView transportName = (TextView) v.findViewById(R.id.transport_name);
		    	TextView arrivalTimeText = (TextView) v.findViewById(R.id.arrival_time_text);
		    	TextView crowdednessInfo = (TextView) v.findViewById(R.id.crowdedness_info);
		    	Button crowdednessButtonYes = (Button) v.findViewById(R.id.crowdedness_button_yes);
		    	Button crowdednessButtonNo = (Button) v.findViewById(R.id.crowdedness_button_no);
		    	
		    	transportName.setText(p.getTransportName());
		    	if(p.getArrivalTimeMin() < 2) arrivalTimeText.setText("Soon");
		    	else arrivalTimeText.setText("in" + p.getArrivalTimeMin() + " mins");
		    	System.out.println("Button yes is " + crowdednessButtonYes.isSelected());
		    	if(p.getCrowdedness()){
		    		crowdednessInfo.setText(Constant.CROWDEDNESS_TRUE);
		    	}
		    	else{
		    		crowdednessInfo.setText(Constant.CROWDEDNESS_FALSE);
		    	}
		    	if(p.getButtonShown()){
		    		crowdednessButtonYes.setVisibility(View.VISIBLE);
		    		crowdednessButtonNo.setVisibility(View.VISIBLE);
		    		crowdednessButtonYes.setSelected(p.getToggleYes());
		    		crowdednessButtonNo.setSelected(p.getToggleNo());
		    	}
		    	else{
		    		crowdednessButtonYes.setVisibility(View.GONE);
		    		crowdednessButtonNo.setVisibility(View.GONE);
		    	}
		    	
		    	crowdednessButtonYes.setOnClickListener(new View.OnClickListener() { 
			    	  @Override
			    	  public void onClick(View v) {
			    		  System.out.println("onCreate finishing");
			    		  System.out.println(QueryBuilder.post("1", "16:00:00", "crowded", p.getTransportName(), stopId));
			    		  DataRetriever retriever = new DataRetriever();
			    	      retriever.execute(QueryBuilder.post("1", "16:00:00", "crowded", p.getTransportName(), stopId));
			    	      p.setToggleYes(true);
			    	      p.setToggleNo(false);
			    	  }    
			    });
		        
		    	crowdednessButtonNo.setOnClickListener(new View.OnClickListener() {           
			    	  @Override
			    	  public void onClick(View v) {
			    		  System.out.println("onCreate finishing");
			    		  System.out.println(QueryBuilder.post("1", "16:00:00", "uncrowded", p.getTransportName(), stopId));
			    		  DataRetriever retriever = new DataRetriever();
			    	      retriever.execute(QueryBuilder.post("1", "16:00:00", "uncrowded", p.getTransportName(), stopId));
			    	      p.setToggleYes(true);
			    	      p.setToggleNo(false);
			    	  }    
			    });
		    }

		    return v;

		}
}
