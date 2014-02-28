package com.example.crowdmotoringdemo;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.crowdmotoringdemo.R;
import com.example.crowdmotoringdemo.customadapter.TransportElementGetCrowdedness;
import com.example.crowdmotoringdemo.customadapter.TransportListAdapter;
import com.example.crowdmotoringdemo.customadapter.TransportListElement;
import com.example.crowdmotoringdemo.servercommunication.QueryBuilder;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunication;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunicationCallback;
import com.example.crowdmotoringdemo.variables.Constant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class StopInfoActivity extends Activity implements ServerCommunicationCallback{
	
	String stopId;
	int currentQuery;
	
	TransportListAdapter transportArray;
	ListView transportList;
	JSONArray transportArrayJson;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stop_info_activity);
		
		stopId = getIntent().getStringExtra(Constant.EXTRA_STOP_ID);
		
		transportList = (ListView) findViewById(R.id.list);
		
        
        transportArray = new TransportListAdapter(getApplicationContext(), R.layout.transport_list_element);
        transportArray.setStopId(stopId);
        transportList.setAdapter(transportArray);
        
        transportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent transportViewScreen = new Intent(getApplicationContext(), TransportInfoActivity.class);
				transportViewScreen.putExtra(Constant.EXTRA_STOP_ID, stopId);
				transportViewScreen.putExtra(Constant.EXTRA_ROUTE_ID, transportArray.getItem(position).getRouteId());
				startActivity(transportViewScreen);
			}
				
    	});
	}
	
	protected void onStart(){
		super.onStart();
		
		System.out.println("onCreate finishing");
		System.out.println(QueryBuilder.getBusInfo(stopId));
		ServerCommunication retriever = new ServerCommunication();
		retriever.setCallback(this);
        retriever.execute(QueryBuilder.getBusInfo(stopId));
	}
	
	protected void setCrowdedness(){
		
	}

	@Override
	public void onDataRetrieved(Object output, String requestStr) {
		// TODO Auto-generated method stub
		System.out.println("Success obtaining json " + output);
		
		try {
			transportArrayJson = new JSONArray((String)output);
			transportArray.clear();
			
			for(int i = 0; i < transportArrayJson.length(); i++){
				TransportListElement temp = new TransportListElement();
				JSONObject data = transportArrayJson.optJSONObject(i);
				
				temp.setTransportName(data.optString("name"));
				
				Calendar currTime = Calendar.getInstance(TimeZone.getTimeZone("SGT"));
				String arrivalTimeStr = data.optString("time");
				String[] arrivalTimeArr = arrivalTimeStr.split(":");
				Calendar busArrivalTime = Calendar.getInstance(TimeZone.getTimeZone("SGT"));
				busArrivalTime.set(Calendar.SECOND, Integer.parseInt(arrivalTimeArr[2]));
				busArrivalTime.set(Calendar.MINUTE, Integer.parseInt(arrivalTimeArr[1]));
				busArrivalTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrivalTimeArr[0]));
				busArrivalTime.add(Calendar.MILLISECOND, -1*Constant.TIME_OFFSET);
				long timeUntilArrival = busArrivalTime.getTimeInMillis() - currTime.getTimeInMillis();
				temp.setArrivalTimeMin(timeUntilArrival/60000);
				
				temp.setRouteId(data.optInt("route_id"));
				
				TransportElementGetCrowdedness.getCrowdedness(temp, stopId, transportArray, transportList);
				transportArray.add(temp);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}