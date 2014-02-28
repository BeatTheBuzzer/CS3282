package com.example.crowdmotoringdemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.crowdmotoringdemo.Constant;
import com.example.crowdmotoringdemo.DataRetriever;
import com.example.crowdmotoringdemo.DataRetrieverResponse;
import com.example.crowdmotoringdemo.QueryBuilder;
import com.example.crowdmotoringdemo.R;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity implements DataRetrieverResponse{
	/*
	boolean clicked96 = false;
	boolean clickedCircle = false;
	boolean clicked183 = false;
	
	Button mButton96;
	Button mButtonCircleLine;
	Button mButton183;
*/
	
	ArrayAdapter<String> stopArray;
	ListView stopList;
	ArrayList<JSONObject> stopArrayJsonList;
	LocationManager locationManager;
	LocationListener locationListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		stopList = (ListView) findViewById(R.id.list);
        
        stopArray = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_item_custom);
        stopList.setAdapter(stopArray);
        
        stopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				String stopId = stopArrayJsonList.get(position).optString("stop_id");
				
				Intent stopViewScreen = new Intent(getApplicationContext(), StopView.class);
				stopViewScreen.putExtra(Constant.EXTRA_STOP_ID, stopId);
				startActivity(stopViewScreen);
			}
				
    	});
		
		/*
		mButton96 = (Button) findViewById(R.id.bus_96_button);
		mButtonCircleLine = (Button) findViewById(R.id.mrt_circle_button);
		mButton183 = (Button) findViewById(R.id.bus_183_button);
		
		mButton96.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!clicked96) mButton96.setBackgroundColor(Color.rgb(255,165,0));
				else mButton96.setBackgroundResource(android.R.drawable.btn_default);
				clicked96 = !clicked96;
				
				// Test php call
				if(clicked96){
					
				}
			}
		});
		
		mButtonCircleLine.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!clickedCircle) mButtonCircleLine.setBackgroundColor(Color.rgb(255,165,0));
				else mButtonCircleLine.setBackgroundResource(android.R.drawable.btn_default);
				clickedCircle = !clickedCircle;
			}
		});

		mButton183.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!clicked183) mButton183.setBackgroundColor(Color.rgb(255,165,0));
				else mButton183.setBackgroundResource(android.R.drawable.btn_default);
				clicked183 = !clicked183;
			}
		});
		*/
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		System.out.println("onCreate finishing");
		System.out.println(QueryBuilder.getAllStops());
		DataRetriever retriever = new DataRetriever();
		retriever.setCallback(this);
        retriever.execute(QueryBuilder.getAllStops());
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
        	public void onLocationChanged(Location location) {
        		// Called when a new location is found by the network location provider.
        		System.out.println("I am called!");
        		double latitude = location.getLatitude();
        		double longitude = location.getLongitude();
        		
        		for(int i = 0; i < stopArrayJsonList.size(); i++){
        			JSONObject currBusStop = stopArrayJsonList.get(i);
        			double busStopLatitude = Double.parseDouble(currBusStop.optString("latitude"));
        			double busStopLongitude = Double.parseDouble(currBusStop.optString("longitude"));
        			double distance = MiscFunctions.GPSDistance(latitude, longitude, busStopLatitude, busStopLongitude);
        			try {
        				currBusStop.put("distance", distance);
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		sortBusStop();
        	}

        	public void onStatusChanged(String provider, int status, Bundle extras) {}

        	public void onProviderEnabled(String provider) {}

        	public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}

	@Override
	public void onDataRetrieved(Object output, String requestStr) {
		// TODO Auto-generated method stub
		// 1. Translate the string into JSON object
		// 2. Create clickable bus stop buttons sorted by distance
		// 3. Add events to each bus stop button to change to StopView
		
		System.out.println("Success obtaining json " + output);
		JSONArray stopArrayJson;
		
		try {
			stopArrayJson = new JSONArray((String)output);
			stopArrayJsonList = new ArrayList<JSONObject>();
			for(int i = 0; i < stopArrayJson.length(); i++){
				stopArrayJsonList.add(stopArrayJson.optJSONObject(i));
				stopArrayJson.optJSONObject(i).put("distance", 0.0);
			}
			
			this.sortBusStop();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	protected void sortBusStop(){
		stopArray.clear();
		Collections.sort(stopArrayJsonList, new JSONComparatorByDistance());
		for(int i = 0; i < stopArrayJsonList.size(); i++){
			JSONObject currBusStop = stopArrayJsonList.get(i);
			stopArray.add(currBusStop.optString("name"));
		}
	}
}
