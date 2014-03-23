package com.example.crowdmotoringdemo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.crowdmotoringdemo.R;
import com.example.crowdmotoringdemo.customadapter.StopListElement;
import com.example.crowdmotoringdemo.customadapter.TransportElementGetCrowdedness;
import com.example.crowdmotoringdemo.customadapter.TransportListAdapter;
import com.example.crowdmotoringdemo.customadapter.TransportListElement;
import com.example.crowdmotoringdemo.servercommunication.QueryBuilder;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunication;
import com.example.crowdmotoringdemo.servercommunication.ServerCommunicationCallback;
import com.example.crowdmotoringdemo.variables.Constant;
import com.example.crowdmotoringdemo.variables.Properties;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class StopInfoActivity extends Activity implements ServerCommunicationCallback{
	
	String stopId;
	String stopName;
	int currentQuery;
	
	TransportListAdapter transportArray;
	ListView transportList;
	JSONArray transportArrayJson;
	EditText searchText;
	ArrayList<TransportListElement> transportArrayBackup;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stop_info_activity);
		
		stopId = getIntent().getStringExtra(Constant.EXTRA_STOP_ID);
		stopName = getIntent().getStringExtra(Constant.EXTRA_STOP_NAME);
		
		transportList = (ListView) findViewById(R.id.list);
		
		searchText = (EditText) findViewById(R.id.search_text);
		transportArrayBackup = new ArrayList<TransportListElement>();
        transportArray = new TransportListAdapter(getApplicationContext(), R.layout.transport_list_element);
        transportArray.setStopId(stopId);
        transportList.setAdapter(transportArray);
        
        transportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent transportInfoActivityScreen = new Intent(getApplicationContext(), TransportInfoActivity.class);
				transportInfoActivityScreen.putExtra(Constant.EXTRA_STOP_ID, stopId);
				transportInfoActivityScreen.putExtra(Constant.EXTRA_ROUTE_ID, transportArray.getItem(position).getRouteId());
				transportInfoActivityScreen.putExtra(Constant.EXTRA_STOP_NAME, stopName);
				transportInfoActivityScreen.putExtra(Constant.EXTRA_TRANSPORT_NAME, transportArray.getItem(position).getTransportName());
				startActivity(transportInfoActivityScreen);
			}
				
    	});
        
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            	refreshTransportList();
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
	
	protected void onStart(){
		super.onStart();
		
		System.out.println("onCreate finishing");
		System.out.println(QueryBuilder.getBusInfo(stopId));
		ServerCommunication retriever = new ServerCommunication();
		retriever.setCallback(this);
        retriever.execute(QueryBuilder.getBusInfo(stopId));
	}

	@Override
	public void onDataRetrieved(Object output, String requestStr) {
		// TODO Auto-generated method stub
		System.out.println("Success obtaining json " + output);
		if(output == null) return;
		
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
				transportArrayBackup.add(temp);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onNoDataRetrieved(String requestStr){
		Toast error = Toast.makeText(getApplicationContext(),
				"Something went wrong :(\nTry to go back and reload this page, or reopen the app",
				Properties.TOAST_DEFAULT_DURATION);
        error.show();
	}
	
	protected void refreshTransportList(){
		transportArray.clear();
		transportArray.addAll(transportArrayBackup);
	}
	
	protected void searchTextFilter(){
		int size = transportArray.getCount();
		String search = searchText.getText().toString().toLowerCase(Locale.ENGLISH);
		for(int i = 0; i < size; i++){
			TransportListElement element = transportArray.getItem(i);
			if(!element.getTransportName().toLowerCase(Locale.ENGLISH).startsWith(search)){
				transportArray.remove(element);
				size--;
				i--;
			}
		}
		
		transportArray.notifyDataSetChanged();
	}

}
