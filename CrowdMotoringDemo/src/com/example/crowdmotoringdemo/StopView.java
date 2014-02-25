package com.example.crowdmotoringdemo;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.crowdmotoringdemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class StopView extends Activity implements DataRetrieverResponse{
	
	final int QUERY_BUS_INFO = 1;
	final int QUERY_CROWDEDNESS_INFO = 2;
	
	String stopId;
	int currentQuery;
	
	StopViewListAdapter transportArray;
	ListView transportList;
	JSONArray transportArrayJson;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stop_view);
		
		stopId = getIntent().getStringExtra(Constant.EXTRA_STOP_ID);
		
		transportList = (ListView) findViewById(R.id.list);
		
        
        transportArray = new StopViewListAdapter(getApplicationContext(), R.layout.stop_view_list_element);
        transportArray.stopId = stopId;
        transportList.setAdapter(transportArray);
	}
	
	protected void onStart(){
		super.onStart();
		
		System.out.println("onCreate finishing");
		System.out.println(QueryBuilder.getBusInfo(stopId));
		DataRetriever retriever = new DataRetriever();
		retriever.caller = this;
		currentQuery = QUERY_BUS_INFO;
        retriever.execute(QueryBuilder.getBusInfo(stopId));
	}
	
	protected void setCrowdedness(){
		
	}

	@Override
	public void onDataRetrieved(Object output) {
		// TODO Auto-generated method stub
		System.out.println("Success obtaining json " + output);
		
		try {
			transportArrayJson = new JSONArray((String)output);
			transportArray.clear();
			
			for(int i = 0; i < transportArrayJson.length(); i++){
				StopViewListElement temp = new StopViewListElement();
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
				System.out.println(arrivalTimeArr[2]+":"+arrivalTimeArr[1]+":"+arrivalTimeArr[0]);
				System.out.println(busArrivalTime.getTimeInMillis() + " "+ currTime.getTimeInMillis());
				temp.setArrivalTimeMin(timeUntilArrival/60000);
				temp.setRouteId(data.optInt("route_id"));
				temp.setCrowdedness(true);
				transportArray.add(temp);
			}
			
			transportArray.notifyDataSetChanged();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
