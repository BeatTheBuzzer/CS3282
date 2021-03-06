package com.example.crowdmotoringdemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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

import com.example.crowdmotoringdemo.R;
import com.example.crowdmotoringdemo.customadapter.StopListAdapter;
import com.example.crowdmotoringdemo.customadapter.StopListElement;
import com.example.crowdmotoringdemo.servercommunication.QueryBuilder;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunication;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunicationCallback;
import com.example.crowdmotoringdemo.variables.Constant;
import com.example.crowdmotoringdemo.variables.JSONComparatorByDistance;
import com.example.crowdmotoringdemo.variables.MiscFunctions;
import com.example.crowdmotoringdemo.variables.Properties;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements ServerCommunicationCallback{
	
	StopListAdapter stopArray;
	ListView stopList;
	ArrayList<JSONObject> stopArrayJsonList;
	EditText searchText;
	
	LocationManager locationManager;
	LocationListener locationListener;
	Location currLoc;
	
	Toast loading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		stopList = (ListView) findViewById(R.id.list);
        
		searchText = (EditText) findViewById(R.id.search_text);
        stopArray = new StopListAdapter(getApplicationContext(), R.layout.stop_list_element);
        stopList.setAdapter(stopArray);
        
        loading = Toast.makeText(getApplicationContext(), "Loading..", Properties.TOAST_VERY_LONG_DURATION);
        loading.show();
        
        stopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String stopId = stopArrayJsonList.get(position).optString("stop_id");
				String stopName = stopArrayJsonList.get(position).optString("name");
				
				Intent stopInfoActivityScreen = new Intent(getApplicationContext(), StopInfoActivity.class);
				stopInfoActivityScreen.putExtra(Constant.EXTRA_STOP_ID, stopId);
				stopInfoActivityScreen.putExtra(Constant.EXTRA_STOP_NAME, stopName);
				startActivity(stopInfoActivityScreen);
			}
				
    	});
        
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            	refreshStopList();
            	searchTextFilter();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            } 

        });
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		System.out.println("onCreate finishing");
		System.out.println(QueryBuilder.getAllStops());
		ServerCommunication retriever = new ServerCommunication();
		retriever.setCallback(this);
        retriever.execute(QueryBuilder.getAllStops());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
        	public void onLocationChanged(Location location) {
        		currLoc = location;
        		updateStopDistance();
        	}

        	public void onStatusChanged(String provider, int status, Bundle extras) {}

        	public void onProviderEnabled(String provider) {}

        	public void onProviderDisabled(String provider) {}
        };

        currLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Properties.LOCATION_REFRESH_TIME, Properties.LOCATION_REFRESH_DISTANCE, locationListener);
        
        // Mock location setter
//        currLoc = new Location(LocationManager.GPS_PROVIDER);
//        currLoc.setLatitude(1.297625);
//        currLoc.setLongitude(103.772545);
//        updateStopDistance();
	}

	@Override
	public void onDataRetrieved(Object output, String requestStr) {
		// TODO Auto-generated method stub
		// 1. Translate the string into JSON object
		// 2. Create clickable bus stop buttons sorted by distance
		// 3. Add events to each bus stop button to change to StopView
		loading.cancel();
		System.out.println("Success obtaining json " + output);
		JSONArray stopArrayJson;
		
		try {
			stopArrayJson = new JSONArray((String)output);
			stopArrayJsonList = new ArrayList<JSONObject>();
			for(int i = 0; i < stopArrayJson.length(); i++){
				stopArrayJsonList.add(stopArrayJson.optJSONObject(i));
				stopArrayJson.optJSONObject(i).put("distance", -1.0);
			}
			
			this.updateStopDistance();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public void onNoDataRetrieved(String requestStr){
		loading.cancel();
		Toast error = Toast.makeText(getApplicationContext(),
				"Something went wrong :(\nTry to go back and reload this page, or reopen the app",
				Properties.TOAST_DEFAULT_DURATION);
        error.show();
	}
	
	protected void updateStopDistance(){
		if(stopArrayJsonList == null) return;
		
		double latitude = 0;
		double longitude= 0;
		boolean locationValid;
		
		try{
			latitude = currLoc.getLatitude();
			longitude = currLoc.getLongitude();
			locationValid = true;
		}catch(Exception e){
			locationValid = false;
		}
		
		for(int i = 0; i < stopArrayJsonList.size(); i++){
			JSONObject currBusStop = stopArrayJsonList.get(i);
			
			if(locationValid){	
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
			else{
				try {
					currBusStop.put("distance", -1.0);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		this.refreshStopList();
		this.searchTextFilter();
	}

	protected void refreshStopList(){
		if(stopArrayJsonList == null) return;
		
		stopArray.clear();
		Collections.sort(stopArrayJsonList, new JSONComparatorByDistance());
		ArrayList<StopListElement> tempList = new ArrayList<StopListElement>();
		for(int i = 0; i < stopArrayJsonList.size(); i++){
			JSONObject currBusStop = stopArrayJsonList.get(i);
			StopListElement temp = new StopListElement();
			temp.setName(currBusStop.optString("name"));
			temp.setDistance(currBusStop.optDouble("distance"));
			tempList.add(temp);
		}
		
		stopArray.addAll(tempList);
//		stopList.setAdapter(stopArray);
	}
	
	protected void searchTextFilter(){
		int size = stopArray.getCount();
		String search = searchText.getText().toString().toLowerCase(Locale.ENGLISH);
		for(int i = 0; i < size; i++){
			StopListElement element = stopArray.getItem(i);
			if(!element.getName().toLowerCase(Locale.ENGLISH).startsWith(search)){
				stopArray.remove(element);
				size--;
				i--;
			}
		}
		
		stopArray.notifyDataSetChanged();
	}
}
